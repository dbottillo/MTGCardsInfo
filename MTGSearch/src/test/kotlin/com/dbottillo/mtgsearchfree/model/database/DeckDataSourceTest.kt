package com.dbottillo.mtgsearchfree.model.database

import com.dbottillo.mtgsearchfree.model.CardsBucket
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.google.gson.Gson
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class DeckDataSourceTest {

    lateinit var cardsInfoDbHelper: CardsInfoDbHelper
    lateinit var mtgDatabaseHelper: MTGDatabaseHelper
    lateinit var mtgCardDataSource: MTGCardDataSource
    lateinit var cardDataSource: CardDataSource
    lateinit var underTest: DeckDataSource

    @Before
    fun setup() {
        mtgDatabaseHelper = MTGDatabaseHelper(RuntimeEnvironment.application)
        cardsInfoDbHelper = CardsInfoDbHelper(RuntimeEnvironment.application)
        cardDataSource = CardDataSource(cardsInfoDbHelper.writableDatabase, Gson())
        mtgCardDataSource = MTGCardDataSource(mtgDatabaseHelper.readableDatabase, cardDataSource)
        underTest = DeckDataSource(cardsInfoDbHelper.writableDatabase, cardDataSource, mtgCardDataSource)
    }

    @After
    fun tearDown() {
        cardsInfoDbHelper.clear()
        cardsInfoDbHelper.close()
        mtgDatabaseHelper.close()
    }

    @Test
    fun generate_table_is_correct() {
        val query = DeckDataSource.generateCreateTable()
        val queryJoin = DeckDataSource.generateCreateTableJoin()
        assertNotNull(query)
        assertThat(query, `is`("CREATE TABLE IF NOT EXISTS decks (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT not null,color TEXT,archived INT)"))
        assertThat(queryJoin, `is`("CREATE TABLE IF NOT EXISTS deck_card (deck_id INT not null,card_id INT not null,quantity INT not null,side INT)"))
    }

    @Test
    fun test_deck_can_be_saved_in_database() {
        val id = underTest.addDeck("deck")
        val deckFromDb = underTest.getDeck(id)
        assertNotNull(deckFromDb)
        assertThat(deckFromDb.id, `is`(id))
        assertThat(deckFromDb.name, `is`("deck"))
        assertThat(deckFromDb.isArchived, `is`(false))
    }

    @Test
    fun test_deck_can_be_removed_from_database() {
        val id = underTest.addDeck("deck")
        val card = mtgCardDataSource.getRandomCard(1)[0]
        underTest.addCardToDeck(id, card, 2)
        var decks = underTest.decks
        assertThat(decks[0].numberOfCards, `is`(2))
        underTest.deleteDeck(decks[0])
        decks = underTest.decks
        assertThat(decks.size, `is`(0))
        // also need to test that the join table has been cleared
        val cursor = cardsInfoDbHelper.readableDatabase.rawQuery("select * from " + DeckDataSource.TABLE_JOIN, null)
        assertNotNull(cursor)
        assertThat(cursor.count, `is`(0))
        cursor.close()
    }

    @Test
    fun DeckDataSource_nameDeckCanBeEdited() {
        val id = underTest.addDeck("deck")
        val updatedId = underTest.renameDeck(id, "New name")
        assertThat(updatedId > -1, `is`(true))
        val decks = underTest.decks
        assertThat(decks[0].name, `is`("New name"))
    }

    @Test
    fun test_deck_cards_can_be_retrieved_from_database() {
        generateDeckWithSmallAmountOfCards()
        val deck = underTest.decks[0]
        val cards = underTest.getCards(deck)
        assertNotNull(cards)
        assertThat(cards.size, `is`(SMALL_NUMBER_OF_CARDS))
    }

    @Test
    fun test_cards_can_be_added_to_deck() {
        val id = underTest.addDeck("new deck")
        val card = mtgCardDataSource.getRandomCard(1)[0]
        underTest.addCardToDeck(id, card, 2)
        // first check that the card has been saved in the db
        val cursor = cardsInfoDbHelper.readableDatabase.rawQuery("select * from " + CardDataSource.TABLE + " where multiVerseId =?", arrayOf(card.multiVerseId.toString() + ""))
        assertNotNull(cursor)
        assertThat(cursor.count, `is`(1))
        cursor.moveToFirst()
        val cardFromDb = cardDataSource.fromCursor(cursor, true)
        assertNotNull(cardFromDb)
        assertThat(cardFromDb.multiVerseId, `is`(card.multiVerseId))
        // then check that the decks contain at least one card
        val decks = underTest.decks
        assertThat(decks.size, `is`(1))
        val (id1, _, _, numberOfCards) = decks[0]
        assertThat(id1, `is`(id))
        assertThat(numberOfCards, `is`(2))
    }

    @Test
    fun test_cards_can_be_removed_from_deck() {
        val id = underTest.addDeck("new deck")
        val card = mtgCardDataSource.getRandomCard(1)[0]
        underTest.addCardToDeck(id, card, 2)
        var decks = underTest.decks
        assertThat(decks[0].numberOfCards, `is`(2))
        underTest.removeCardFromDeck(id, card)
        decks = underTest.decks
        assertThat(decks[0].numberOfCards, `is`(0))
    }

    @Test
    fun test_multiple_cards_can_be_added_to_deck() {
        val id = generateDeckWithSmallAmountOfCards()
        val decks = underTest.decks
        assertThat(decks.size, `is`(1))
        val (id1, _, _, numberOfCards) = decks[0]
        assertThat(id1, `is`(id))
        assertThat(numberOfCards, `is`(10))
    }

    @Test
    fun test_negative_quantity_will_decrease_cards() {
        val id = underTest.addDeck("new deck")
        val card = mtgCardDataSource.getRandomCard(1)[0]

        underTest.addCardToDeck(id, card, 4)
        var deck = underTest.decks[0]
        assertThat(deck.numberOfCards, `is`(4))

        underTest.addCardToDeck(id, card, -2)
        deck = underTest.decks[0]
        assertThat(deck.numberOfCards, `is`(2))

        underTest.addCardToDeck(id, card, -1)
        deck = underTest.decks[0]
        assertThat(deck.numberOfCards, `is`(1))

        underTest.addCardToDeck(id, card, -4)
        deck = underTest.decks[0]
        assertThat(deck.numberOfCards, `is`(0))
    }

    @Test
    fun test_minus_1_with_1_will_remove_card() {
        val id = underTest.addDeck("new deck")
        val card = mtgCardDataSource.getRandomCard(1)[0]

        underTest.addCardToDeck(id, card, 1)
        var deck = underTest.decks[0]
        var cards = underTest.getCards(deck)
        assertFalse(cards.isEmpty())
        assertThat(deck.numberOfCards, `is`(1))
        assertThat(cards[0].quantity, `is`(1))

        underTest.addCardToDeck(id, card, -1)
        deck = underTest.decks[0]
        cards = underTest.getCards(deck)
        assertTrue(cards.isEmpty())
        assertThat(deck.numberOfCards, `is`(0))
    }

    @Test
    fun test_add_sideboard_cards_are_independent() {
        val id = underTest.addDeck("new deck")
        val card = mtgCardDataSource.getRandomCard(1)[0]

        underTest.addCardToDeck(id, card, 2)
        card.isSideboard = true
        underTest.addCardToDeck(id, card, 2)
        var deck = underTest.decks[0]
        assertThat(deck.numberOfCards, `is`(2))
        assertThat(deck.sizeOfSideboard, `is`(2))

        card.isSideboard = false
        underTest.addCardToDeck(id, card, 2)
        card.isSideboard = true
        underTest.addCardToDeck(id, card, -4)
        deck = underTest.decks[0]
        assertThat(deck.numberOfCards, `is`(4))
        assertThat(deck.sizeOfSideboard, `is`(0))

        card.isSideboard = false
        underTest.addCardToDeck(id, card, -1)
        card.isSideboard = true
        underTest.addCardToDeck(id, card, 6)
        deck = underTest.decks[0]
        assertThat(deck.numberOfCards, `is`(3))
        assertThat(deck.sizeOfSideboard, `is`(6))
    }

    @Test
    fun test_remove_sideboard_cards_are_independent() {
        val id = underTest.addDeck("new deck")
        val card = mtgCardDataSource.getRandomCard(1)[0]

        card.isSideboard = false
        underTest.addCardToDeck(id, card, 2)
        card.isSideboard = true
        underTest.addCardToDeck(id, card, 2)
        var deck = underTest.decks[0]
        assertThat(deck.numberOfCards, `is`(2))
        assertThat(deck.sizeOfSideboard, `is`(2))

        card.isSideboard = false
        underTest.removeCardFromDeck(id, card)
        deck = underTest.decks[0]
        assertThat(deck.numberOfCards, `is`(0))
        assertThat(deck.sizeOfSideboard, `is`(2))

        card.isSideboard = true
        underTest.removeCardFromDeck(id, card)
        deck = underTest.decks[0]
        assertThat(deck.numberOfCards, `is`(0))
        assertThat(deck.sizeOfSideboard, `is`(0))
    }

    @Test
    fun test_add_deck_with_empty_bucket() {
        val bucket = CardsBucket(key = "deck")
        val deckId = underTest.addDeck(bucket)
        assertTrue(deckId > 0)
        val (_, name, _, numberOfCards) = underTest.getDeck(deckId)
        assertThat(name, `is`("deck"))
        assertThat(numberOfCards, `is`(0))
    }

    @Test
    fun test_add_deck_with_non_empty_bucket() {
        val cardNames = arrayOf("Counterspell", "Oath of Jace", "Fireball", "Thunderbolt", "Countersquall")
        val quantities = intArrayOf(2, 3, 4, 1, 3)
        val side = booleanArrayOf(true, true, false, false, true)
        val bucket = CardsBucket(key = "deck")
        val namedCards = ArrayList<MTGCard>(cardNames.size)
        for (i in cardNames.indices) {
            val card = MTGCard()
            card.setCardName(cardNames[i])
            card.quantity = quantities[i]
            card.isSideboard = side[i]
            namedCards.add(card)
        }
        bucket.cards = namedCards
        val deckId = underTest.addDeck(bucket)
        assertTrue(deckId > 0)
        val (_, name) = underTest.getDeck(deckId)
        assertThat(name, `is`("deck"))
        val deckCards = underTest.getCards(deckId)
        assertThat(deckCards.size, `is`(cardNames.size))
        for (card in deckCards) {
            var found = false
            var index = 0
            for (i in cardNames.indices) {
                if (card.name.contains(cardNames[i])) {
                    found = true
                    index = i
                    break
                }
            }
            assertTrue(found)
            assertThat(card.quantity, `is`(quantities[index]))
            assertThat(card.isSideboard, `is`(side[index]))
        }
    }

    @Test
    fun test_add_deck_with_ignore_non_card_name() {
        val cardNames = arrayOf("Counterspell", "Eistein")
        val quantities = intArrayOf(2, 3)
        val bucket = CardsBucket(key = "deck")
        val namedCards = ArrayList<MTGCard>(cardNames.size)
        for (i in cardNames.indices) {
            val card = MTGCard()
            card.setCardName(cardNames[i])
            card.quantity = quantities[i]
            namedCards.add(card)
        }
        bucket.cards = namedCards
        val deckId = underTest.addDeck(bucket)
        val deckCards = underTest.getCards(deckId)
        assertThat(deckCards.size, `is`(cardNames.size - 1))
    }

    @Test
    fun movesCardFromDeckToSideBoard() {
        val deckId = underTest.addDeck("new deck")
        val cards = mtgCardDataSource.getRandomCard(3)

        val card1 = cards[0]
        val card2 = cards[1]
        val card3 = cards[2]

        /*        normal    side
        card 1      4         0
        card 2      2         2
        card 3      0         4
         */
        underTest.addCardToDeck(deckId, card1, 4)
        underTest.addCardToDeck(deckId, card2, 2)
        card2.isSideboard = true
        underTest.addCardToDeck(deckId, card2, 2)
        card3.isSideboard = true
        underTest.addCardToDeck(deckId, card3, 4)

        assertQuantityAndSideboard(deckId, 6, 6)

        /*        normal    side
        card 1      4         0
        card 2      1         3
        card 3      0         4
         */
        underTest.moveCardToSideBoard(deckId, card2, 1)
        var deckCards = underTest.getCards(deckId)
        assertThat(deckCards.size, `is`(4))
        assertThat(deckCards[1], `is`(card2))
        assertThat(deckCards[1].quantity, `is`(1))
        assertThat(deckCards[2], `is`(card2))
        assertThat(deckCards[2].quantity, `is`(3))
        assertQuantityAndSideboard(deckId, 5, 7)

        /*        normal    side
        card 1      0         4
        card 2      1         3
        card 3      0         4
         */
        underTest.moveCardToSideBoard(deckId, card1, 4)
        deckCards = underTest.getCards(deckId)
        assertThat(deckCards.size, `is`(4))
        assertThat(deckCards[0], `is`(card2))
        assertThat(deckCards[1], `is`(card2))
        assertThat(deckCards[1].quantity, `is`(3))
        assertThat(deckCards[2], `is`(card3))
        assertThat(deckCards[2].quantity, `is`(4))
        assertThat(deckCards[3], `is`(card1))
        assertThat(deckCards[3].quantity, `is`(4))
        assertQuantityAndSideboard(deckId, 1, 11)

        /*        normal    side
        card 1      0         4
        card 2      0         4
        card 3      0         4
         */
        underTest.moveCardToSideBoard(deckId, card2, 1)
        deckCards = underTest.getCards(deckId)
        assertThat(deckCards.size, `is`(3))
        assertQuantityAndSideboard(deckId, 0, 12)
    }

    @Test
    fun movesCardFromSideBoardToDeck() {
        val deckId = underTest.addDeck("new deck")
        val cards = mtgCardDataSource.getRandomCard(3)

        val card1 = cards[0]
        val card2 = cards[1]
        val card3 = cards[2]

        /*        normal    side
        card 1      4         0
        card 2      2         2
        card 3      0         4
         */
        underTest.addCardToDeck(deckId, card1, 4)
        underTest.addCardToDeck(deckId, card2, 2)
        card2.isSideboard = true
        underTest.addCardToDeck(deckId, card2, 2)
        card3.isSideboard = true
        underTest.addCardToDeck(deckId, card3, 4)

        assertQuantityAndSideboard(deckId, 6, 6)

        /*        normal    side
        card 1      4         0
        card 2      3         1
        card 3      0         4
         */
        underTest.moveCardFromSideBoard(deckId, card2, 1)
        var deckCards = underTest.getCards(deckId)
        assertThat(deckCards.size, `is`(4))
        assertThat(deckCards[1], `is`(card2))
        assertThat(deckCards[1].quantity, `is`(3))
        assertThat(deckCards[2], `is`(card2))
        assertThat(deckCards[2].quantity, `is`(1))
        assertQuantityAndSideboard(deckId, 7, 5)

        /*        normal    side
        card 1      4         0
        card 2      3         1
        card 3      4         0
         */
        underTest.moveCardFromSideBoard(deckId, card3, 4)
        deckCards = underTest.getCards(deckId)
        assertThat(deckCards.size, `is`(4))
        assertThat(deckCards[0], `is`(card1))
        assertThat(deckCards[1], `is`(card2))
        assertThat(deckCards[1].quantity, `is`(3))
        assertThat(deckCards[2], `is`(card2))
        assertThat(deckCards[2].quantity, `is`(1))
        assertThat(deckCards[3], `is`(card3))
        assertThat(deckCards[3].quantity, `is`(4))
        assertQuantityAndSideboard(deckId, 11, 1)

        /*        normal    side
        card 1      4         0
        card 2      4         0
        card 3      4         0
         */
        underTest.moveCardFromSideBoard(deckId, card2, 1)
        deckCards = underTest.getCards(deckId)
        assertThat(deckCards.size, `is`(3))
        assertQuantityAndSideboard(deckId, 12, 0)
    }

    @Test
    fun test_deck_can_be_copied() {
        generateDeckWithSmallAmountOfCards()
        val deck = underTest.decks[0]
        val originalCards = underTest.getCards(deck)
        underTest.copy(deck)
        val decks = underTest.decks
        assertThat(decks.size, `is`(2))
        assertThat(decks[1].name, `is`("new deck copy"))
        val copiedCards = underTest.getCards(decks[1])
        assertThat(copiedCards, `is`(originalCards))
    }

    private fun generateDeckWithSmallAmountOfCards(): Long {
        val id = underTest.addDeck("new deck")
        val cards = mtgCardDataSource.getRandomCard(SMALL_NUMBER_OF_CARDS)
        for (i in 0 until SMALL_NUMBER_OF_CARDS) {
            underTest.addCardToDeck(id, cards[i], i + 1)
        }
        return id
    }

    private fun assertQuantityAndSideboard(deckId: Long, quantity: Int, sideboard: Int) {
        val decks = underTest.decks
        assertThat(decks.size, `is`(1))
        val (id, _, _, numberOfCards, sizeOfSideboard) = decks[0]
        assertThat(id, `is`(deckId))
        assertThat(numberOfCards, `is`(quantity))
        assertThat(sizeOfSideboard, `is`(sideboard))
    }
}

const val SMALL_NUMBER_OF_CARDS = 4
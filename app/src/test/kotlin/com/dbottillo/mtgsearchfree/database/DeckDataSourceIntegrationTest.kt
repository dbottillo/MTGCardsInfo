package com.dbottillo.mtgsearchfree.database

import com.dbottillo.mtgsearchfree.model.CardsBucket
import com.dbottillo.mtgsearchfree.model.Color
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.util.Logger
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class DeckDataSourceIntegrationTest {

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
        underTest = DeckDataSource(cardsInfoDbHelper.writableDatabase, cardDataSource, mtgCardDataSource, DeckColorMapper(Gson()), Logger())
    }

    @After
    fun tearDown() {
        cardsInfoDbHelper.clear()
        cardsInfoDbHelper.close()
        mtgDatabaseHelper.close()
    }

    @Test
    fun `generate table is correct`() {
        val query = DeckDataSource.generateCreateTable()
        val queryJoin = DeckDataSource.generateCreateTableJoin()
        assertThat(query).isNotNull()
        assertThat(query).isEqualTo("CREATE TABLE IF NOT EXISTS decks (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT not null,color TEXT,archived INT)")
        assertThat(queryJoin).isEqualTo("CREATE TABLE IF NOT EXISTS deck_card (deck_id INT not null,card_id INT not null,quantity INT not null,side INT)")
    }

    @Test
    fun `deck can be saved into database`() {
        val id = underTest.addDeck("deck")

        val deckFromDb = underTest.getDeck(id)

        assertThat(deckFromDb).isNotNull()
        assertThat(deckFromDb.id).isEqualTo(id)
        assertThat(deckFromDb.name).isEqualTo("deck")
        assertThat(deckFromDb.isArchived).isEqualTo(false)
        assertThat(deckFromDb.numberOfCards).isEqualTo(0)
        assertThat(deckFromDb.sizeOfSideboard).isEqualTo(0)
        assertThat(deckFromDb.colors).isEqualTo(emptyList<Color>())
    }

    @Test
    fun `deck can be removed from database`() {
        val id = underTest.addDeck("deck")
        val card = mtgCardDataSource.getRandomCard(1)[0]
        underTest.addCardToDeck(id, card, 2)
        var decks = underTest.decks
        assertThat(decks[0].numberOfCards).isEqualTo(2)
        underTest.deleteDeck(decks[0])
        decks = underTest.decks
        assertThat(decks.size).isEqualTo(0)
        // also need to test that the join table has been cleared
        val cursor = cardsInfoDbHelper.readableDatabase.rawQuery("select * from " + DeckDataSource.TABLE_JOIN, null)
        assertThat(cursor).isNotNull()
        assertThat(cursor.count).isEqualTo(0)
        cursor.close()
    }

    @Test
    fun `name deck can be edited`() {
        val id = underTest.addDeck("deck")
        val updatedId = underTest.renameDeck(id, "New name")
        assertThat(updatedId > -1).isEqualTo(true)
        val decks = underTest.decks
        assertThat(decks[0].name).isEqualTo("New name")
    }

    @Test
    fun `test deck cards can be retrieved from database`() {
        generateDeckWithSmallAmountOfCards()
        val deck = underTest.decks[0]

        val cards = underTest.getCards(deck)

        assertThat(cards).isNotNull()
        assertThat(cards.size).isEqualTo(SMALL_NUMBER_OF_CARDS)
    }

    @Test
    fun `test cards can be added to deck`() {
        val id = underTest.addDeck("new deck")
        val card = mtgCardDataSource.getRandomCard(1)[0]
        underTest.addCardToDeck(id, card, 2)
        // first check that the card has been saved in the db
        val cursor = cardsInfoDbHelper.readableDatabase.rawQuery("select * from " + CardDataSource.TABLE + " where multiVerseId =?", arrayOf(card.multiVerseId.toString() + ""))
        assertThat(cursor).isNotNull()
        assertThat(cursor.count).isEqualTo(1)
        cursor.moveToFirst()
        val cardFromDb = cardDataSource.fromCursor(cursor, true)
        assertThat(cardFromDb).isNotNull()
        assertThat(cardFromDb.multiVerseId).isEqualTo(card.multiVerseId)
        // then check that the decks contain at least one card
        val decks = underTest.decks
        assertThat(decks.size).isEqualTo(1)
        val (id1, _, _, numberOfCards) = decks[0]
        assertThat(id1).isEqualTo(id)
        assertThat(numberOfCards).isEqualTo(2)
    }

    @Test
    fun `test cards can be removed from deck`() {
        val id = underTest.addDeck("new deck")
        val card = mtgCardDataSource.getRandomCard(1)[0]
        underTest.addCardToDeck(id, card, 2)
        var decks = underTest.decks
        assertThat(decks[0].numberOfCards).isEqualTo(2)
        underTest.removeCardFromDeck(id, card)
        decks = underTest.decks
        assertThat(decks[0].numberOfCards).isEqualTo(0)
    }

    @Test
    fun `test multiple cards can be added to deck`() {
        val id = generateDeckWithSmallAmountOfCards()
        val decks = underTest.decks
        assertThat(decks.size).isEqualTo(1)
        val (id1, _, _, numberOfCards) = decks[0]
        assertThat(id1).isEqualTo(id)
        assertThat(numberOfCards).isEqualTo(10)
    }

    @Test
    fun `test negative quantity will decrease cards`() {
        val id = underTest.addDeck("new deck")
        val card = mtgCardDataSource.getRandomCard(1)[0]

        underTest.addCardToDeck(id, card, 4)
        var deck = underTest.decks[0]
        assertThat(deck.numberOfCards).isEqualTo(4)

        underTest.addCardToDeck(id, card, -2)
        deck = underTest.decks[0]
        assertThat(deck.numberOfCards).isEqualTo(2)

        underTest.addCardToDeck(id, card, -1)
        deck = underTest.decks[0]
        assertThat(deck.numberOfCards).isEqualTo(1)

        underTest.addCardToDeck(id, card, -4)
        deck = underTest.decks[0]
        assertThat(deck.numberOfCards).isEqualTo(0)
    }

    @Test
    fun `test minus 1 with 1 will remove card`() {
        val id = underTest.addDeck("new deck")
        val card = mtgCardDataSource.getRandomCard(1)[0]

        underTest.addCardToDeck(id, card, 1)
        var deck = underTest.decks[0]
        var cards = underTest.getCards(deck)
        assertThat(cards.isEmpty()).isFalse()
        assertThat(deck.numberOfCards).isEqualTo(1)
        assertThat(cards[0].quantity).isEqualTo(1)

        underTest.addCardToDeck(id, card, -1)
        deck = underTest.decks[0]
        cards = underTest.getCards(deck)
        assertThat(cards.isEmpty()).isTrue()
        assertThat(deck.numberOfCards).isEqualTo(0)
    }

    @Test
    fun `test add sideboard cards are independent`() {
        val id = underTest.addDeck("new deck")
        val card = mtgCardDataSource.getRandomCard(1)[0]

        underTest.addCardToDeck(id, card, 2)
        card.isSideboard = true
        underTest.addCardToDeck(id, card, 2)
        var deck = underTest.decks[0]
        assertThat(deck.numberOfCards).isEqualTo(2)
        assertThat(deck.sizeOfSideboard).isEqualTo(2)

        card.isSideboard = false
        underTest.addCardToDeck(id, card, 2)
        card.isSideboard = true
        underTest.addCardToDeck(id, card, -4)
        deck = underTest.decks[0]
        assertThat(deck.numberOfCards).isEqualTo(4)
        assertThat(deck.sizeOfSideboard).isEqualTo(0)

        card.isSideboard = false
        underTest.addCardToDeck(id, card, -1)
        card.isSideboard = true
        underTest.addCardToDeck(id, card, 6)
        deck = underTest.decks[0]
        assertThat(deck.numberOfCards).isEqualTo(3)
        assertThat(deck.sizeOfSideboard).isEqualTo(6)
    }

    @Test
    fun `test remove sideboard cards are independent`() {
        val id = underTest.addDeck("new deck")
        val card = mtgCardDataSource.getRandomCard(1)[0]

        card.isSideboard = false
        underTest.addCardToDeck(id, card, 2)
        card.isSideboard = true
        underTest.addCardToDeck(id, card, 2)
        var deck = underTest.decks[0]
        assertThat(deck.numberOfCards).isEqualTo(2)
        assertThat(deck.sizeOfSideboard).isEqualTo(2)

        card.isSideboard = false
        underTest.removeCardFromDeck(id, card)
        deck = underTest.decks[0]
        assertThat(deck.numberOfCards).isEqualTo(0)
        assertThat(deck.sizeOfSideboard).isEqualTo(2)

        card.isSideboard = true
        underTest.removeCardFromDeck(id, card)
        deck = underTest.decks[0]
        assertThat(deck.numberOfCards).isEqualTo(0)
        assertThat(deck.sizeOfSideboard).isEqualTo(0)
    }

    @Test
    fun `test add deck with empty bucket`() {
        val bucket = CardsBucket(key = "deck")
        val deckId = underTest.addDeck(bucket)
        assertThat(deckId > 0).isTrue()
        val (_, name, _, numberOfCards) = underTest.getDeck(deckId)
        assertThat(name).isEqualTo("deck")
        assertThat(numberOfCards).isEqualTo(0)
    }

    @Test
    fun `add deck with non empty bucket`() {
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
        assertThat(deckId > 0).isTrue()
        val (_, name) = underTest.getDeck(deckId)
        assertThat(name).isEqualTo("deck")
        val deckCards = underTest.getCards(deckId)
        assertThat(deckCards.size).isEqualTo(cardNames.size)
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
            assertThat(found).isTrue()
            assertThat(card.quantity).isEqualTo(quantities[index])
            assertThat(card.isSideboard).isEqualTo(side[index])
        }
    }

    @Test
    fun `add deck with ignore non card name`() {
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
        assertThat(deckCards.size).isEqualTo(cardNames.size - 1)
    }

    @Test
    fun `moves card from deck to sideboard`() {
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
        assertThat(deckCards.size).isEqualTo(4)
        assertThat(deckCards[1]).isEqualTo(card2)
        assertThat(deckCards[1].quantity).isEqualTo(1)
        assertThat(deckCards[2]).isEqualTo(card2)
        assertThat(deckCards[2].quantity).isEqualTo(3)
        assertQuantityAndSideboard(deckId, 5, 7)

        /*        normal    side
        card 1      0         4
        card 2      1         3
        card 3      0         4
         */
        underTest.moveCardToSideBoard(deckId, card1, 4)
        deckCards = underTest.getCards(deckId)
        assertThat(deckCards.size).isEqualTo(4)
        assertThat(deckCards[0]).isEqualTo(card2)
        assertThat(deckCards[1]).isEqualTo(card2)
        assertThat(deckCards[1].quantity).isEqualTo(3)
        assertThat(deckCards[2]).isEqualTo(card3)
        assertThat(deckCards[2].quantity).isEqualTo(4)
        assertThat(deckCards[3]).isEqualTo(card1)
        assertThat(deckCards[3].quantity).isEqualTo(4)
        assertQuantityAndSideboard(deckId, 1, 11)

        /*        normal    side
        card 1      0         4
        card 2      0         4
        card 3      0         4
         */
        underTest.moveCardToSideBoard(deckId, card2, 1)
        deckCards = underTest.getCards(deckId)
        assertThat(deckCards.size).isEqualTo(3)
        assertQuantityAndSideboard(deckId, 0, 12)
    }

    @Test
    fun `moves card from sideboard to deck`() {
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
        assertThat(deckCards.size).isEqualTo(4)
        assertThat(deckCards[1]).isEqualTo(card2)
        assertThat(deckCards[1].quantity).isEqualTo(3)
        assertThat(deckCards[2]).isEqualTo(card2)
        assertThat(deckCards[2].quantity).isEqualTo(1)
        assertQuantityAndSideboard(deckId, 7, 5)

        /*        normal    side
        card 1      4         0
        card 2      3         1
        card 3      4         0
         */
        underTest.moveCardFromSideBoard(deckId, card3, 4)
        deckCards = underTest.getCards(deckId)
        assertThat(deckCards.size).isEqualTo(4)
        assertThat(deckCards[0]).isEqualTo(card1)
        assertThat(deckCards[1]).isEqualTo(card2)
        assertThat(deckCards[1].quantity).isEqualTo(3)
        assertThat(deckCards[2]).isEqualTo(card2)
        assertThat(deckCards[2].quantity).isEqualTo(1)
        assertThat(deckCards[3]).isEqualTo(card3)
        assertThat(deckCards[3].quantity).isEqualTo(4)
        assertQuantityAndSideboard(deckId, 11, 1)

        /*        normal    side
        card 1      4         0
        card 2      4         0
        card 3      4         0
         */
        underTest.moveCardFromSideBoard(deckId, card2, 1)
        deckCards = underTest.getCards(deckId)
        assertThat(deckCards.size).isEqualTo(3)
        assertQuantityAndSideboard(deckId, 12, 0)
    }

    @Test
    fun `deck can be copied`() {
        generateDeckWithSmallAmountOfCards()
        val deck = underTest.decks[0]
        val originalCards = underTest.getCards(deck)
        underTest.copy(deck)
        val decks = underTest.decks
        assertThat(decks.size).isEqualTo(2)
        assertThat(decks[1].name).isEqualTo("new deck copy")
        val copiedCards = underTest.getCards(decks[1])
        assertThat(copiedCards).isEqualTo(originalCards)
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
        assertThat(decks.size).isEqualTo(1)
        val (id, _, _, numberOfCards, sizeOfSideboard) = decks[0]
        assertThat(id).isEqualTo(deckId)
        assertThat(numberOfCards).isEqualTo(quantity)
        assertThat(sizeOfSideboard).isEqualTo(sideboard)
    }
}

const val SMALL_NUMBER_OF_CARDS = 4
package com.dbottillo.mtgsearchfree.storage

import android.net.Uri
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.model.CardsBucket
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.database.DeckDataSource
import com.dbottillo.mtgsearchfree.util.FileUtil
import com.dbottillo.mtgsearchfree.util.Logger
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

class DecksStorageImplTest {

    lateinit var underTest: DecksStorage

    @Rule @JvmField var mockitoRule = MockitoJUnit.rule()!!
    @Rule @JvmField var exception = ExpectedException.none()!!

    @Mock lateinit var deckDataSource: DeckDataSource
    @Mock lateinit var deck: Deck
    @Mock lateinit var editedDeck: Deck
    @Mock lateinit var card: MTGCard
    @Mock lateinit var fileUtil: FileUtil
    @Mock lateinit var cardsBucket: CardsBucket
    @Mock lateinit var generalData: GeneralData
    @Mock lateinit var logger: Logger
    @Mock lateinit var decks: List<Deck>

    private val deckCards = listOf(MTGCard(18, 1), MTGCard(19, 2))

    @Before
    fun setup() {
        whenever(deck.id).thenReturn(DECK_ID)
        whenever(deckDataSource.decks).thenReturn(decks)
        whenever(deckDataSource.getDeck(DECK_ID)).thenReturn(deck)
        whenever(deckDataSource.addDeck("deck2")).thenReturn(DECK_ID)
        whenever(deckDataSource.addDeck(cardsBucket)).thenReturn(DECK_ID)
        whenever(deckDataSource.getCards(deck)).thenReturn(deckCards)
        whenever(deckDataSource.getCards(DECK_ID)).thenReturn(deckCards)
        whenever(deckDataSource.getDeck(DECK_ID)).thenReturn(deck)
        underTest = DecksStorageImpl(fileUtil, deckDataSource, generalData, logger)
    }

    @Test
    fun testLoad() {
        val decksLoaded = underTest.load()

        assertThat(decksLoaded).isNotNull()
        assertThat(decksLoaded).isEqualTo(decks)
        verify(deckDataSource).decks
        verifyNoMoreInteractions(fileUtil, deckDataSource, generalData)
    }

    @Test
    fun testAddDeck() {
        underTest.addDeck("deck")

        verify(deckDataSource).addDeck("deck")
        verify(deckDataSource).decks
        verifyNoMoreInteractions(fileUtil, deckDataSource, generalData)
    }

    @Test
    fun testDeleteDeck() {
        val decksLoaded = underTest.deleteDeck(deck)

        verify(deckDataSource).deleteDeck(deck)
        verify(deckDataSource).decks
        assertThat(decksLoaded).isEqualTo(decks)
        verifyNoMoreInteractions(fileUtil, deckDataSource, generalData)
    }

    @Test
    fun testLoadDeck() {
        val cards = underTest.loadDeck(DECK_ID)

        verify(deckDataSource).getCards(DECK_ID)
        assertThat(cards.allCards()).isEqualTo(deckCards)
        verifyNoMoreInteractions(fileUtil, deckDataSource, generalData)
    }

    @Test
    fun testLoadDeckById() {
        val deck = underTest.loadDeckById(DECK_ID)

        verify(deckDataSource).getDeck(DECK_ID)
        assertThat(deck).isEqualTo(deck)
        verifyNoMoreInteractions(fileUtil, deckDataSource, generalData)
    }

    @Test
    fun testEditDeck() {
        whenever(deckDataSource.getDeck(DECK_ID)).thenReturn(editedDeck)

        val deck = underTest.editDeck(deck, "new")

        verify(deckDataSource).renameDeck(DECK_ID, "new")
        verify(deckDataSource).getDeck(DECK_ID)
        assertThat(deck).isEqualTo(editedDeck)
        verifyNoMoreInteractions(fileUtil, deckDataSource, generalData)
    }

    @Test
    fun testAddCard() {
        val cards = underTest.addCard(deck, card, 2)

        verify(deckDataSource).addCardToDeck(DECK_ID, card, 2)
        assertThat(cards.allCards()).isEqualTo(deckCards)
        verify(generalData).lastDeckSelected = DECK_ID
        verify(deckDataSource).getCards(DECK_ID)
        verifyNoMoreInteractions(fileUtil, deckDataSource, generalData)
    }

    @Test
    fun testAddCardNewDeck() {
        val cards = underTest.addCard("deck2", card, 2)

        verify(deckDataSource).addDeck("deck2")
        verify(deckDataSource).addCardToDeck(DECK_ID, card, 2)
        verify(deckDataSource).getCards(DECK_ID)
        assertThat(cards.allCards()).isEqualTo(deckCards)
        verify(generalData).lastDeckSelected = DECK_ID
        verifyNoMoreInteractions(fileUtil, deckDataSource, generalData)
    }

    @Test
    fun testRemoveCard() {
        val cards = underTest.removeCard(deck, card)

        verify(deckDataSource).addCardToDeck(DECK_ID, card, -1)
        verify(deckDataSource).getCards(DECK_ID)
        assertThat(cards.allCards()).isEqualTo(deckCards)
        verifyNoMoreInteractions(fileUtil, deckDataSource, generalData)
    }

    @Test
    fun movesCardFromSideboard() {
        val cards = underTest.moveCardFromSideboard(deck, card, 2)

        verify(deckDataSource).moveCardFromSideBoard(DECK_ID, card, 2)
        verify(deckDataSource).getCards(DECK_ID)
        assertThat(cards.allCards()).isEqualTo(deckCards)
        verifyNoMoreInteractions(fileUtil, deckDataSource, generalData)
    }

    @Test
    fun movesCardToSideboard() {
        val cards = underTest.moveCardToSideboard(deck, card, 2)

        verify(deckDataSource).moveCardToSideBoard(DECK_ID, card, 2)
        verify(deckDataSource).getCards(DECK_ID)
        assertThat(cards.allCards()).isEqualTo(deckCards)
        verifyNoMoreInteractions(fileUtil, deckDataSource, generalData)
    }

    @Test
    fun testRemoveAllCard() {
        val cards = underTest.removeAllCard(deck, card)

        verify(deckDataSource).removeCardFromDeck(DECK_ID, card)
        verify(deckDataSource).getCards(DECK_ID)
        assertThat(cards.allCards()).isEqualTo(deckCards)
        verifyNoMoreInteractions(fileUtil, deckDataSource, generalData)
    }

    @Test
    @Throws(Throwable::class)
    fun `should import deck from uri`() {
        val uri = mock<Uri>()
        whenever(fileUtil.readFileContent(uri)).thenReturn(cardsBucket)

        val decksLoaded = underTest.importDeck(uri)

        verify(deckDataSource).addDeck(cardsBucket)
        verify(deckDataSource).decks
        assertThat(decksLoaded).isNotNull()
        assertThat(decksLoaded).isEqualTo(decks)
        verify(fileUtil).readFileContent(uri)
        verifyNoMoreInteractions(fileUtil, deckDataSource, generalData)
    }

    @Test
    @Throws(Exception::class)
    fun `should not import null deck`() {
        exception.expect(MTGException::class.java)
        val uri = mock<Uri>()
        val e = Exception("error")
        whenever(fileUtil.readFileContent(uri)).thenThrow(e)

        underTest.importDeck(uri)

        verify(fileUtil).readFileContent(uri)
        verifyNoMoreInteractions(fileUtil, deckDataSource, generalData)
    }

    @Test
    fun `should copy deck`() {
        val result = underTest.copy(deck)

        verify(deckDataSource).copy(deck)
        verify(deckDataSource).decks
        assertThat(result).isEqualTo(decks)
        verifyNoMoreInteractions(fileUtil, deckDataSource, generalData)
    }
}

private const val DECK_ID = 200L

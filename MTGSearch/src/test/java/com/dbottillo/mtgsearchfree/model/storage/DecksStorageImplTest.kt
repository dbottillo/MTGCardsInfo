package com.dbottillo.mtgsearchfree.model.storage

import android.net.Uri

import com.dbottillo.mtgsearchfree.exceptions.ExceptionCode
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.mapper.DeckMapper
import com.dbottillo.mtgsearchfree.model.CardsBucket
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.database.DeckDataSource
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource
import com.dbottillo.mtgsearchfree.util.FileUtil
import com.dbottillo.mtgsearchfree.util.Logger
import com.dbottillo.mtgsearchfree.util.MTGExceptionMatcher

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

import java.util.Arrays

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class DecksStorageImplTest {

    lateinit var underTest: DecksStorage

    @Rule @JvmField
    var mockitoRule = MockitoJUnit.rule()

    @Rule @JvmField
    var exception = ExpectedException.none()

    @Mock
    lateinit var deckDataSource: DeckDataSource

    @Mock
    lateinit var deck: Deck

    @Mock
    lateinit var card: MTGCard

    @Mock
    lateinit var fileUtil: FileUtil

    @Mock
    lateinit var cardsBucket: CardsBucket

    @Mock
    lateinit var logger: Logger

    @Mock
    lateinit var deckMapper: DeckMapper

    private val deckCards = Arrays.asList(MTGCard(18), MTGCard(19))
    private val decks = Arrays.asList(Deck(1), Deck(2))

    @Before
    fun setup() {
        `when`(deck.id).thenReturn(DECK_ID)
        `when`(deckDataSource.decks).thenReturn(decks)
        `when`(deckDataSource.getDeck(DECK_ID)).thenReturn(deck)
        `when`(deckDataSource.addDeck("deck2")).thenReturn(2L)
        `when`(deckDataSource.addDeck(cardsBucket)).thenReturn(DECK_ID)
        `when`(deckDataSource.getCards(deck)).thenReturn(deckCards)
        `when`(deckMapper.order(deckCards)).thenReturn(deckCards)
        `when`(deckDataSource.getCards(2L)).thenReturn(deckCards)
        underTest = DecksStorageImpl(fileUtil, deckDataSource, deckMapper, logger)
    }

    @Test
    fun testLoad() {
        val decksLoaded = underTest.load()
        verify<DeckDataSource>(deckDataSource).decks
        assertNotNull(decksLoaded)
        assertThat(decksLoaded, `is`(decks))
    }

    @Test
    fun testAddDeck() {
        underTest.addDeck("deck")
        verify<DeckDataSource>(deckDataSource).addDeck("deck")
    }

    @Test
    fun testDeleteDeck() {
        underTest.deleteDeck(deck)
        verify<DeckDataSource>(deckDataSource).deleteDeck(deck)
    }

    @Test
    fun testLoadDeck() {
        val cards = underTest.loadDeck(deck)

        verify<DeckMapper>(deckMapper).order(deckCards)
        verify<DeckDataSource>(deckDataSource).getCards(deck)
        assertThat(cards.list, `is`(deckCards))
    }

    @Test
    fun testEditDeck() {
        val cards = underTest.editDeck(deck, "new")
        verify<DeckDataSource>(deckDataSource).renameDeck(DECK_ID, "new")
        assertThat(cards.list, `is`(deckCards))
    }

    @Test
    fun testAddCard() {
        val cards = underTest.addCard(deck, card, 2)
        verify<DeckDataSource>(deckDataSource).addCardToDeck(DECK_ID, card, 2)
        assertThat(cards.list, `is`(deckCards))
    }

    @Test
    fun testAddCardNewDeck() {
        val cards = underTest.addCard("deck2", card, 2)
        verify<DeckDataSource>(deckDataSource).addDeck("deck2")
        verify<DeckDataSource>(deckDataSource).addCardToDeck(2L, card, 2)
        assertThat(cards.list, `is`(deckCards))
    }

    @Test
    fun testRemoveCard() {
        val cards = underTest.removeCard(deck, card)
        verify<DeckDataSource>(deckDataSource).addCardToDeck(DECK_ID, card, -1)
        assertThat(cards.list, `is`(deckCards))
    }

    @Test
    fun movesCardFromSideboard() {
        val cards = underTest.moveCardFromSideboard(deck, card, 2)
        verify<DeckDataSource>(deckDataSource).moveCardFromSideBoard(DECK_ID, card, 2)
        assertThat(cards.list, `is`(deckCards))
    }

    @Test
    fun movesCardToSideboard() {
        val cards = underTest.moveCardToSideboard(deck, card, 2)
        verify<DeckDataSource>(deckDataSource).moveCardToSideBoard(DECK_ID, card, 2)
        assertThat(cards.list, `is`(deckCards))
    }

    @Test
    fun testRemoveAllCard() {
        val cards = underTest.removeAllCard(deck, card)
        verify<DeckDataSource>(deckDataSource).removeCardFromDeck(DECK_ID, card)
        assertThat(cards.list, `is`(deckCards))
    }

    @Test
    @Throws(Throwable::class)
    fun DecksStorage_willImportDeck() {
        val uri = mock(Uri::class.java)
        `when`(fileUtil.readFileContent(uri)).thenReturn(cardsBucket)
        val decksLoaded = underTest.importDeck(uri)
        verify<DeckDataSource>(deckDataSource).addDeck(cardsBucket)
        assertNotNull(decksLoaded)
        assertThat(decksLoaded, `is`(decks))
    }

    @Test
    @Throws(Exception::class)
    fun DecksStorage_willNotImportNullDeck() {
        exception.expect(MTGException::class.java)
        exception.expect(MTGExceptionMatcher.hasCode(ExceptionCode.DECK_NOT_IMPORTED))

        val uri = mock(Uri::class.java)
        val e = Exception("error")
        `when`(fileUtil.readFileContent(uri)).thenThrow(e)
        underTest.importDeck(uri)
    }

    companion object {
        private val DECK_ID = 200L
    }

}
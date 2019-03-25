package com.dbottillo.mtgsearchfree.database

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.dbottillo.mtgsearchfree.model.Color
import com.dbottillo.mtgsearchfree.util.Logger
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

class DeckDataSourceTest {

    @JvmField @Rule val mockitoRule = MockitoJUnit.rule()!!

    @Mock lateinit var database: SQLiteDatabase
    @Mock lateinit var cardDataSource: CardDataSource
    @Mock lateinit var mtgCardDataSource: MTGCardDataSource
    @Mock lateinit var deckColorMapper: DeckColorMapper
    @Mock lateinit var logger: Logger

    lateinit var underTest: DeckDataSource

    @Before
    fun setup() {
        underTest = DeckDataSource(database, cardDataSource, mtgCardDataSource, deckColorMapper, logger)
    }

    @Test
    fun `should retrieve decks from database`() {
        val cursor = mock<Cursor>()
        whenever(cursor.isAfterLast).thenReturn(false, false, true)
        whenever(cursor.getLong(0)).thenReturn(1L, 2L)
        whenever(cursor.getColumnIndex(DeckDataSource.COLUMNS.NAME.noun)).thenReturn(1)
        whenever(cursor.getString(1)).thenReturn("first", "second")
        whenever(cursor.getInt(3)).thenReturn(0)
        val firstCardCursor = mock<Cursor>()
        whenever(firstCardCursor.isAfterLast).thenReturn(false, false, true)
        whenever(firstCardCursor.getInt(0)).thenReturn(0, 1) // side
        whenever(firstCardCursor.getInt(1)).thenReturn(2, 5) // quantity
        whenever(firstCardCursor.getString(2)).thenReturn("[W]", "[U,B]") // color identity
        val firstDeckColors = mock<List<Color>>()
        whenever(deckColorMapper.convert(listOf("[W]", "[U,B]"))).thenReturn(firstDeckColors)
        val secondCardCursor = mock<Cursor>()
        whenever(secondCardCursor.isAfterLast).thenReturn(false, false, false, true)
        whenever(secondCardCursor.getInt(0)).thenReturn(0) // side
        whenever(secondCardCursor.getInt(1)).thenReturn(2, 5, 6) // quantity
        whenever(secondCardCursor.getString(2)).thenReturn("[W]", "[W]", "[R,G]") // color identity
        val secondDeckColors = mock<List<Color>>()
        whenever(deckColorMapper.convert(listOf("[W]", "[W]", "[R,G]"))).thenReturn(secondDeckColors)
        whenever(database.rawQuery("select H.side,H.quantity,P.colorIdentity from MTGCard P inner join deck_card H on (H.card_id = P.multiVerseId and H.deck_id = ?)", arrayOf("1"))).thenReturn(firstCardCursor)
        whenever(database.rawQuery("select H.side,H.quantity,P.colorIdentity from MTGCard P inner join deck_card H on (H.card_id = P.multiVerseId and H.deck_id = ?)", arrayOf("2"))).thenReturn(secondCardCursor)
        whenever(database.rawQuery("Select * from decks", null)).thenReturn(cursor)

        val result = underTest.decks

        assertThat(result.size).isEqualTo(2)
        result[0].also {
            assertThat(it.id).isEqualTo(1L)
            assertThat(it.name).isEqualTo("first")
            assertThat(it.isArchived).isFalse()
            assertThat(it.numberOfCards).isEqualTo(2)
            assertThat(it.sizeOfSideboard).isEqualTo(5)
            assertThat(it.colors).isEqualTo(firstDeckColors)
        }
        result[1].also {
            assertThat(it.id).isEqualTo(2L)
            assertThat(it.name).isEqualTo("second")
            assertThat(it.isArchived).isFalse()
            assertThat(it.numberOfCards).isEqualTo(13)
            assertThat(it.sizeOfSideboard).isEqualTo(0)
            assertThat(it.colors).isEqualTo(secondDeckColors)
        }
        verify(cursor).close()
        verify(database).rawQuery("Select * from decks", null)
        verify(database).rawQuery("select H.side,H.quantity,P.colorIdentity from MTGCard P inner join deck_card H on (H.card_id = P.multiVerseId and H.deck_id = ?)", arrayOf("1"))
        verify(database).rawQuery("select H.side,H.quantity,P.colorIdentity from MTGCard P inner join deck_card H on (H.card_id = P.multiVerseId and H.deck_id = ?)", arrayOf("2"))
        verify(deckColorMapper).convert(listOf("[W]", "[U,B]"))
        verify(deckColorMapper).convert(listOf("[W]", "[W]", "[R,G]"))
        verifyNoMore()
    }

    private fun verifyNoMore() {
        verifyNoMoreInteractions(database, cardDataSource, mtgCardDataSource, deckColorMapper)
    }
}
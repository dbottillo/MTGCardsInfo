package com.dbottillo.mtgsearchfree.model.database

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.google.gson.Gson
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit

class CardDataSourceTest {

    @Rule
    @JvmField
    var mockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var cursor: Cursor

    @Mock
    lateinit var card: MTGCard

    @Mock
    lateinit var database: SQLiteDatabase

    lateinit var underTest: CardDataSource

    @Before
    fun setUp() {
        underTest = CardDataSource(database, Gson())
        `when`(database.rawQuery("DELETE FROM MTGCard where _id=?", arrayOf("100"))).thenReturn(cursor)
        `when`(card.id).thenReturn(100)
    }

    @Test
    fun `should remove card from database`() {
        underTest.removeCard(card)

        verify(database).rawQuery("DELETE FROM MTGCard where _id=?", arrayOf("100"))
        verify(cursor).moveToFirst()
        verify(cursor).close()
        verifyNoMoreInteractions(database, cursor)
    }
}
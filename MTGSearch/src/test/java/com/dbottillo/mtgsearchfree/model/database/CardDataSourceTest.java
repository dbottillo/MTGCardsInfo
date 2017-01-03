package com.dbottillo.mtgsearchfree.model.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class CardDataSourceTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Cursor cursor;

    @Mock
    MTGCard card;

    @Mock
    SQLiteDatabase database;

    private CardDataSource underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new CardDataSource(database, new Gson());
        when(database.rawQuery("DELETE FROM MTGCard where _id=?", new String[]{"100"})).thenReturn(cursor);
        when(card.getId()).thenReturn(100L);
    }

    @Test
    public void onRemoveCard_shouldRemoveCard(){
        underTest.removeCard(card);

        verify(database).rawQuery("DELETE FROM MTGCard where _id=?", new String[]{"100"});
        verify(cursor).moveToFirst();
        verify(cursor).close();
        verifyNoMoreInteractions(database, cursor);
    }
}
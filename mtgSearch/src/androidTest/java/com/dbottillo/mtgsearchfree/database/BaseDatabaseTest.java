package com.dbottillo.mtgsearchfree.database;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import org.junit.After;
import org.junit.Before;

public class BaseDatabaseTest {

    CardsInfoDbHelper dataHelper;
    private RenamingDelegatingContext context;

    @Before
    public void create_data_helper() throws Exception {
        context = new RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().getContext(), "test_");
        dataHelper = new CardsInfoDbHelper(context);
        Log.e("test", dataHelper.toString());
        Log.e("test", dataHelper.getReadableDatabase().toString());
        dataHelper.clear();
    }

    @After
    public void close_data_helper() throws Exception {
        dataHelper.close();
    }

    protected SQLiteDatabase getRawDatabase() {
        return SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath("cardsinfo.db"), null);
    }
}

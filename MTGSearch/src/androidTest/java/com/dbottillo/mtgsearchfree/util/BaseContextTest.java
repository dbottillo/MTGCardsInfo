package com.dbottillo.mtgsearchfree.util;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.test.RenamingDelegatingContext;

import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.model.database.MTGDatabaseHelper;

import org.junit.After;
import org.junit.Before;

public abstract class BaseContextTest {

    protected Context context;
    public CardsInfoDbHelper cardsInfoDbHelper;
    public MTGDatabaseHelper mtgDatabaseHelper;

    @Before
    public void create_data_helper() throws Exception {
        context = new RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");
        cardsInfoDbHelper = new CardsInfoDbHelper(context);
        cardsInfoDbHelper.clear();
        mtgDatabaseHelper = new MTGDatabaseHelper(context);
    }

    @After
    public void close_data_helper() throws Exception {
        cardsInfoDbHelper.close();
        mtgDatabaseHelper.close();
    }

}

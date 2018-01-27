package com.dbottillo.mtgsearchfree.util

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.test.RenamingDelegatingContext

import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper
import com.dbottillo.mtgsearchfree.model.database.MTGDatabaseHelper

import org.junit.After
import org.junit.Before

abstract class BaseContextTest {

    lateinit var context: Context
    lateinit var cardsInfoDbHelper: CardsInfoDbHelper
    lateinit var mtgDatabaseHelper: MTGDatabaseHelper

    @Before
    fun create_data_helper() {
        context = RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().targetContext, "test_")
        cardsInfoDbHelper = CardsInfoDbHelper(context)
        cardsInfoDbHelper.clear()
        mtgDatabaseHelper = MTGDatabaseHelper(context)
    }

    @After
    fun close_data_helper() {
        cardsInfoDbHelper.close()
        mtgDatabaseHelper.close()
    }

}

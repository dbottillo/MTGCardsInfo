package com.dbottillo.mtgsearchfree.model.database

import android.support.test.runner.AndroidJUnit4
import com.dbottillo.mtgsearchfree.debug.test.BuildConfig
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.util.BaseContextTest
import com.dbottillo.mtgsearchfree.util.FileHelper
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.json.JSONException
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetDataSourceTest : BaseContextTest() {

    lateinit var underTest: SetDataSource

    @Before
    fun setup() {
        underTest = SetDataSource(mtgDatabaseHelper.writableDatabase)
    }

    @Test
    fun test_generate_table_is_correct() {
        val query = SetDataSource.generateCreateTable()
        assertNotNull(query)
        assertThat(query, `is`("CREATE TABLE IF NOT EXISTS MTGSet (_id INTEGER PRIMARY KEY, name TEXT,code TEXT)"))
    }

    @Test
    fun test_set_can_be_saved_in_database() {
        val set = MTGSet(5000)
        set.name = "Commander"
        set.code = "CMX"
        val id = underTest.saveSet(set)
        val cursor = mtgDatabaseHelper.readableDatabase.rawQuery("select * from " + SetDataSource.TABLE + " where rowid =?", arrayOf(id.toString() + ""))
        assertNotNull(cursor)
        assertThat(cursor.count, `is`(1))
        cursor.moveToFirst()
        val setFromDb = underTest.fromCursor(cursor)
        assertNotNull(setFromDb)
        assertThat(setFromDb.id, `is`(set.id))
        assertThat(setFromDb.name, `is`(set.name))
        assertThat(setFromDb.code, `is`(set.code))
        cursor.close()
        // need to clear up the db:
        underTest.removeSet(id)
    }

    @Test
    fun test_sets_can_be_retrieved_from_database() {
        val sets = underTest.sets
        assertNotNull(sets)
        assertThat(sets.size, `is`(BuildConfig.NUMBER_OF_SET)) // the one added from the previous test
    }

    @Test
    fun test_all_set_are_loaded_correctly() {
        val fromJson = FileHelper.readSetListJSON(context)
        val sets = underTest.sets
        assertNotNull(fromJson)
        assertTrue(fromJson.containsAll(sets))
    }
}
package com.dbottillo.mtgsearchfree.database

import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.storage.SetDataSource
import com.dbottillo.mtgsearchfree.util.readSetListJSON
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class SetDataSourceTest {

    private lateinit var underTest: SetDataSource
    private lateinit var mtgDatabaseHelper: MTGDatabaseHelper

    @Before
    fun setup() {
        mtgDatabaseHelper = MTGDatabaseHelper(RuntimeEnvironment.application)
        underTest = SetDataSource(mtgDatabaseHelper.readableDatabase)
    }

    @After
    fun close_data_helper() {
        mtgDatabaseHelper.close()
    }

    @Test
    fun `should generate right create table`() {
        val query = SetDataSource.generateCreateTable()
        assertThat(query).isNotNull()
        assertThat(query).isEqualTo("CREATE TABLE IF NOT EXISTS MTGSet (_id INTEGER PRIMARY KEY, name TEXT,code TEXT,type TEXT)")
    }

    @Test
    fun `should save sets inside database`() {
        val set = MTGSet(id = 5000, name = "Commander", code = "CMX")
        val id = underTest.saveSet(set)
        val cursor = mtgDatabaseHelper.readableDatabase.rawQuery("select * from " + SetDataSource.TABLE + " where rowid =?", arrayOf(id.toString() + ""))
        assertThat(cursor).isNotNull()
        assertThat(cursor.count).isEqualTo(1)
        cursor.moveToFirst()
        val setFromDb = underTest.fromCursor(cursor)
        assertThat(setFromDb).isNotNull()
        assertThat(setFromDb.id).isEqualTo(set.id)
        assertThat(setFromDb.name).isEqualTo(set.name)
        assertThat(setFromDb.code).isEqualTo(set.code)
        cursor.close()
        // need to clear up the db:
        underTest.removeSet(id)
    }

    @Test
    fun `should retrieve sets from database`() {
        val sets = underTest.sets
        assertThat(sets).isNotNull()
        assertThat(sets.size).isEqualTo(NUMBER_OF_SET) // the one added from the previous test
    }

    @Test
    fun `should load all sets correctly`() {
        val fromJson = readSetListJSON()
        val sets = underTest.sets
        assertThat(fromJson).isNotNull()
        assertThat(fromJson).containsAtLeastElementsIn(sets)
    }
}

private const val NUMBER_OF_SET = 233

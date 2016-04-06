package com.dbottillo.mtgsearchfree.model.database;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.util.FileHelper;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class SetDataSourceTest extends BaseDatabaseTest {

    private static final int NUMBER_OF_SET = 149;

    @Test
    public void test_generate_table_is_correct() {
        String query = SetDataSource.generateCreateTable();
        assertNotNull(query);
        assertThat(query, is("CREATE TABLE IF NOT EXISTS MTGSet (_id INTEGER PRIMARY KEY, name TEXT,code TEXT)"));
    }

    @Test
    public void test_set_can_be_saved_in_database() {
        MTGSet set = new MTGSet(5000);
        set.setName("Commander");
        set.setCode("CMX");
        long id = SetDataSource.saveSet(mtgDatabaseHelper.getWritableDatabase(), set);
        Cursor cursor = mtgDatabaseHelper.getReadableDatabase().rawQuery("select * from " + SetDataSource.TABLE + " where rowid =?", new String[]{id + ""});
        assertNotNull(cursor);
        assertThat(cursor.getCount(), is(1));
        cursor.moveToFirst();
        MTGSet setFromDb = SetDataSource.fromCursor(cursor);
        assertNotNull(setFromDb);
        assertThat(setFromDb.getId(), is(set.getId()));
        assertThat(setFromDb.getName(), is(set.getName()));
        assertThat(setFromDb.getCode(), is(set.getCode()));
        cursor.close();
        // need to clear up the db:
        SetDataSource.removeSet(mtgDatabaseHelper.getWritableDatabase(), id);
    }

    @Test
    public void test_sets_can_be_retrieved_from_database() {
        ArrayList<MTGSet> sets = mtgDatabaseHelper.getSets();
        assertNotNull(sets);
        assertThat(sets.size(), is(NUMBER_OF_SET)); // the one added from the previous test
    }

    @Test
    public void test_all_set_are_loaded_correctly() throws JSONException {
        ArrayList<MTGSet> fromJson = FileHelper.readSetListJSON(context);
        ArrayList<MTGSet> sets = mtgDatabaseHelper.getSets();
        assertNotNull(fromJson);
        assertTrue(fromJson.containsAll(sets));
    }
}
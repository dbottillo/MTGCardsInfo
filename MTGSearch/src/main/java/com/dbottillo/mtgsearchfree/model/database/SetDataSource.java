package com.dbottillo.mtgsearchfree.model.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.util.LOG;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SetDataSource {

    public static final String TABLE = "MTGSet";

    private SQLiteDatabase database;

    private enum COLUMNS {
        NAME("name", "TEXT"),
        CODE("code", "TEXT");

        private String name;
        private String type;

        COLUMNS(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }
    }

    public SetDataSource(SQLiteDatabase database) {
        this.database = database;
    }

    public static String generateCreateTable() {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        builder.append(TABLE).append(" (_id INTEGER PRIMARY KEY, ");
        for (COLUMNS column : COLUMNS.values()) {
            builder.append(column.name).append(' ').append(column.type);
            if (column != COLUMNS.CODE) {
                builder.append(',');
            }
        }
        return builder.append(')').toString();
    }

    long saveSet(MTGSet set) {
        ContentValues values = new ContentValues();
        if (set.getId() > -1) {
            values.put("_id", set.getId());
        }
        values.put(COLUMNS.NAME.getName(), set.getName());
        values.put(COLUMNS.CODE.getName(), set.getCode());
        return database.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<MTGSet> getSets() {
        String query = "SELECT * FROM " + TABLE;
        Cursor cursor = database.rawQuery(query, null);
        ArrayList<MTGSet> sets = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                sets.add(fromCursor(cursor));
                cursor.moveToNext();
            }
        }
        LOG.query(query);
        cursor.close();
        return sets;
    }

    void removeSet(long id) {
        String[] args = new String[]{id + ""};
        String query = "DELETE FROM " + TABLE + " where _id=? ";
        Cursor cursor = database.rawQuery(query, args);
        cursor.moveToFirst();
        cursor.close();
        LOG.query(query, args);
    }

    public MTGSet fromCursor(Cursor cursor) {
        MTGSet set = new MTGSet(cursor.getInt(cursor.getColumnIndex("_id")));
        set.setName(cursor.getString(cursor.getColumnIndex(COLUMNS.NAME.getName())));
        set.setCode(cursor.getString(cursor.getColumnIndex(COLUMNS.CODE.getName())));
        return set;
    }

    public ContentValues fromJSON(JSONObject object) throws JSONException {
        ContentValues values = new ContentValues();
        values.put(COLUMNS.CODE.getName(), object.getString("code"));
        values.put(COLUMNS.NAME.getName(), object.getString("name"));
        return values;
    }
}

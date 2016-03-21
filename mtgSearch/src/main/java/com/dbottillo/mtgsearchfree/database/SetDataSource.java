package com.dbottillo.mtgsearchfree.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.mtgsearchfree.model.MTGSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class SetDataSource {

    public static final String TABLE = "MTGSet";

    public enum COLUMNS {
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

    private SetDataSource() {
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

    public static long saveSet(SQLiteDatabase db, MTGSet set) {
        ContentValues values = new ContentValues();
        if (set.getId() > -1) {
            values.put("_id", set.getId());
        }
        values.put(COLUMNS.NAME.getName(), set.getName());
        values.put(COLUMNS.CODE.getName(), set.getCode());
        return db.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static ArrayList<MTGSet> getSets(SQLiteDatabase db) {
        String query = "SELECT * FROM " + TABLE;
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<MTGSet> sets = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                sets.add(fromCursor(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return sets;
    }

    public static void removeSet(SQLiteDatabase database, long id) {
        String[] args = new String[]{id + ""};
        database.rawQuery("DELETE FROM " + TABLE + " where _id=? ", args).moveToFirst();
    }

    public static MTGSet fromCursor(Cursor cursor) {
        MTGSet set = new MTGSet(cursor.getInt(cursor.getColumnIndex("_id")));
        set.setName(cursor.getString(cursor.getColumnIndex(COLUMNS.NAME.getName())));
        set.setCode(cursor.getString(cursor.getColumnIndex(COLUMNS.CODE.getName())));
        return set;
    }

    public static ContentValues fromJSON(JSONObject object) throws JSONException {
        ContentValues values = new ContentValues();
        values.put(COLUMNS.CODE.getName(), object.getString("code"));
        values.put(COLUMNS.NAME.getName(), object.getString("name"));
        return values;
    }
}

package com.dbottillo.mtgsearchfree.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.mtgsearchfree.resources.MTGSet;

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
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE + " (_id INTEGER PRIMARY KEY, ";
        for (COLUMNS column : COLUMNS.values()) {
            query += column.name + " " + column.type + ",";
        }
        return query.substring(0, query.length() - 1) + ")";
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

    public static MTGSet fromCursor(Cursor cursor) {
        MTGSet set = new MTGSet(cursor.getInt(cursor.getColumnIndex("_id")));
        set.setName(cursor.getString(cursor.getColumnIndex(COLUMNS.NAME.getName())));
        set.setCode(cursor.getString(cursor.getColumnIndex(COLUMNS.CODE.getName())));
        return set;
    }
}

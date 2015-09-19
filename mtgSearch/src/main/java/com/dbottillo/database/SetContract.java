package com.dbottillo.database;

import android.provider.BaseColumns;

public final class SetContract {

    public SetContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class SetEntry implements BaseColumns {
        public static final String TABLE_NAME = "MTGSet";
        public static final String COLUMN_NAME_CODE = "code";
        public static final String COLUMN_NAME_NAME = "name";
    }

    protected static final String SQL_CREATE_SET_TABLE =
            "CREATE TABLE " + SetContract.SetEntry.TABLE_NAME + " (" +
                    SetContract.SetEntry._ID + " INTEGER PRIMARY KEY," +
                    SetContract.SetEntry.COLUMN_NAME_CODE + "text ,"+
                    SetContract.SetEntry.COLUMN_NAME_NAME + "text )";

    protected static final String SQL_DELETE_SET_TABLE =
            "DROP TABLE IF EXISTS " + SetContract.SetEntry.TABLE_NAME;
}

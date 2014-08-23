package com.dbottillo.database;

import android.provider.BaseColumns;

/**
 * Created by danielebottillo on 04/03/2014.
 */
public final class HSSetContract {

    public HSSetContract() {}

    /* Inner class that defines the table contents */
    public static abstract class HSSetEntry implements BaseColumns {
        public static final String TABLE_NAME = "HSSet";
        public static final String COLUMN_NAME_NAME = "name";
    }
}

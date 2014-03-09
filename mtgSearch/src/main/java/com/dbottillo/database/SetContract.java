package com.dbottillo.database;

import android.provider.BaseColumns;

/**
 * Created by danielebottillo on 04/03/2014.
 */
public final class SetContract {

    public SetContract() {}

    /* Inner class that defines the table contents */
    public static abstract class SetEntry implements BaseColumns {
        public static final String TABLE_NAME = "MTGSet";
        public static final String COLUMN_NAME_CODE = "code";
        public static final String COLUMN_NAME_NAME = "name";
    }
}

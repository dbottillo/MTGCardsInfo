package com.dbottillo.database;

import android.provider.BaseColumns;

public final class CardContract {

    private CardContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class CardEntry implements BaseColumns {
        public static final String TABLE_NAME = "MTGCard";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_TYPES = "types";
        public static final String COLUMN_NAME_SUBTYPES = "subtypes";
        public static final String COLUMN_NAME_COLORS = "colors";
        public static final String COLUMN_NAME_CMC = "cmc";
        public static final String COLUMN_NAME_RARITY = "rarity";
        public static final String COLUMN_NAME_POWER = "power";
        public static final String COLUMN_NAME_TOUGHNESS = "toughness";
        public static final String COLUMN_NAME_MANACOST = "manaCost";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_MULTICOLOR = "multicolor";
        public static final String COLUMN_NAME_LAND = "land";
        public static final String COLUMN_NAME_ARTIFACT = "artifact";
        public static final String COLUMN_NAME_MULTIVERSEID = "multiVerseId";
        public static final String COLUMN_NAME_SET_ID = "setId";
        public static final String COLUMN_NAME_SET_NAME = "setName";
    }

    protected static final String SQL_CREATE_CARDS_TABLE =
            "CREATE TABLE " + CardContract.CardEntry.TABLE_NAME + " ("
                    + CardContract.CardEntry._ID + " INTEGER PRIMARY KEY,"
                    + CardContract.CardEntry.COLUMN_NAME_NAME + " TEXT ,"
                    + CardContract.CardEntry.COLUMN_NAME_TYPE + " TEXT ,"
                    + CardContract.CardEntry.COLUMN_NAME_TYPES + " TEXT ,"
                    + CardContract.CardEntry.COLUMN_NAME_SUBTYPES + " TEXT ,"
                    + CardContract.CardEntry.COLUMN_NAME_COLORS + " TEXT ,"
                    + CardContract.CardEntry.COLUMN_NAME_CMC + " INTEGER ,"
                    + CardContract.CardEntry.COLUMN_NAME_RARITY + " TEXT ,"
                    + CardContract.CardEntry.COLUMN_NAME_POWER + " TEXT ,"
                    + CardContract.CardEntry.COLUMN_NAME_TOUGHNESS + " TEXT ,"
                    + CardContract.CardEntry.COLUMN_NAME_MANACOST + " TEXT ,"
                    + CardContract.CardEntry.COLUMN_NAME_TEXT + " TEXT ,"
                    + CardContract.CardEntry.COLUMN_NAME_MULTICOLOR + " INTEGER ,"
                    + CardContract.CardEntry.COLUMN_NAME_LAND + " INTEGER ,"
                    + CardContract.CardEntry.COLUMN_NAME_ARTIFACT + " INTEGER ,"
                    + CardContract.CardEntry.COLUMN_NAME_MULTIVERSEID + " INTEGER ,"
                    + CardContract.CardEntry.COLUMN_NAME_SET_ID + " INTEGER ,"
                    + CardContract.CardEntry.COLUMN_NAME_SET_NAME + " TEXT )";


    protected static final String SQL_DELETE_CARDS_TABLE =
            "DROP TABLE IF EXISTS " + CardContract.CardEntry.TABLE_NAME;
}

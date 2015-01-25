package com.dbottillo.database;

import android.provider.BaseColumns;

public final class CardContract {

    public CardContract() {
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
}

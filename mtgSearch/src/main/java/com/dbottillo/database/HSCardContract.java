package com.dbottillo.database;

import android.provider.BaseColumns;

/**
 * Created by danielebottillo on 04/03/2014.
 */
public final class HSCardContract {

    public HSCardContract() {}

    /* Inner class that defines the table contents */
    public static abstract class HSCardEntry implements BaseColumns {
        public static final String TABLE_NAME = "HSCard";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_COST = "cost";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_RARITY = "rarity";
        public static final String COLUMN_NAME_FACTION = "faction";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_MECHANICS = "mechanics";
        public static final String COLUMN_NAME_ATTACK = "attack";
        public static final String COLUMN_NAME_HEALTH = "health";
        public static final String COLUMN_NAME_COLLECTIBLE = "collectible";
        public static final String COLUMN_NAME_ELITE = "elite";
        public static final String COLUMN_NAME_SET_ID = "setId";
        public static final String COLUMN_NAME_SET_NAME = "setName";
        public static final String COLUMN_NAME_HEARTHSTONE_ID = "hearthstoneId";
    }
}

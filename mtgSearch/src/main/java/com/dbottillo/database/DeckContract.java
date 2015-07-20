package com.dbottillo.database;

import android.provider.BaseColumns;

public final class DeckContract {

    public DeckContract() {
    }

    public static abstract class DeckEntry implements BaseColumns {
        public static final String TABLE_NAME = "decks";
        public static final String COLUMN_NAME_NAME = "name";
    }

    public static abstract class DeckCardEntry implements BaseColumns {
        public static final String TABLE_NAME = "deck_card";
        public static final String COLUMN_NAME_DECK_ID = "deck_id";
        public static final String COLUMN_NAME_CARD_ID = "card_id";
        public static final String COLUMN_NAME_SIDE = "side";
    }
}

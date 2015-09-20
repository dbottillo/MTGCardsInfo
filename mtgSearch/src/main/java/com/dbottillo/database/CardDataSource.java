package com.dbottillo.database;

import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.resources.MTGCard;

public final class CardDataSource {

    private CardDataSource() {
    }

    public static long saveCard(SQLiteDatabase database, MTGCard card) {
        return database.insertWithOnConflict(CardContract.CardEntry.TABLE_NAME, null, card.createContentValue(), SQLiteDatabase.CONFLICT_IGNORE);
    }
}

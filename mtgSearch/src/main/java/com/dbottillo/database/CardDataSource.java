package com.dbottillo.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.resources.MTGCard;

public final class CardDataSource {

    private SQLiteDatabase database;
    private CardsInfoDbHelper dbHelper;

    public CardDataSource() {
    }

    public static long saveCard(SQLiteDatabase database, MTGCard card) {
        ContentValues values = new ContentValues();
        values.put(CardContract.CardEntry.COLUMN_NAME_MULTIVERSEID, card.getMultiVerseId());
        values.put(CardContract.CardEntry.COLUMN_NAME_NAME, card.getName());
        return database.insertWithOnConflict(CardContract.CardEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        /*values.put(COLUMN_ID, recipient.getId());
        values.put(COLUMN_NAME, recipient.getName());
        values.put(COLUMN_CURRENCY, recipient.getCurrency());
        values.put(COLUMN_EMAIL, recipient.getEmail());
        values.put(COLUMN_TYPE, recipient.getType());
        values.put(COLUMN_ADDRESS_FIRST_LINE, recipient.getAddressFirstLine());
        values.put(COLUMN_ADDRESS_POST_CODE, recipient.getAddressPostCode());
        values.put(COLUMN_ADDRESS_CITY, recipient.getAddressCity());
        values.put(COLUMN_ADDRESS_COUNTRY_CODE, recipient.getAddressCountryCode());
        values.put(COLUMN_ADDRESS_STATE, recipient.getAddressState());
        values.put(COLUMN_ADDRESS_COUNTRY, recipient.getCountry());
        values.put(COLUMN_ADDRESS_IMAGE, recipient.getImage());
        values.put(COLUMN_FIELD_MAP, String.valueOf(new JSONObject(recipient.getFieldMap())));
        if (recipient.getId() > 0) {
            return database.insertWithOnConflict(TABLE_RECIPIENTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } else {
            return database.insert(TABLE_RECIPIENTS, null, values);
        }*/
    }
}

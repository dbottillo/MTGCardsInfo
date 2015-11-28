package com.dbottillo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.dbottillo.resources.MTGCard;

import java.util.ArrayList;

public final class FavouritesDataSource {

    private FavouritesDataSource() {

    }

    public static abstract class FavouritesEntry implements BaseColumns {
        public static final String TABLE_NAME = "Favourites";
    }

    protected static final String CREATE_FAVOURITES_TABLE =
            "CREATE TABLE " + FavouritesEntry.TABLE_NAME + " ("
                    + FavouritesEntry._ID + " INTEGER PRIMARY KEY)";

    public static long saveFavourites(SQLiteDatabase db, MTGCard card) {
        Cursor current = db.rawQuery("select * from MTGCard where multiVerseId=?", new String[]{card.getMultiVerseId() + ""});
        if (current.getCount() == 0) {
            // need to add the card
            CardDataSource.saveCard(db, card);
        }
        current.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavouritesEntry._ID, card.getMultiVerseId());
        return db.insertWithOnConflict(FavouritesEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static ArrayList<MTGCard> getCards(SQLiteDatabase db, boolean fullCard) {
        ArrayList<MTGCard> cards = new ArrayList<>();
        Cursor cursor = db.rawQuery("select P.* from MTGCard P inner join Favourites H on (H._id = P.multiVerseId)", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MTGCard card = MTGCard.createCardFromCursor(cursor, fullCard);
            cards.add(card);
            cursor.moveToNext();
        }
        cursor.close();
        return cards;
    }

    public static void removeFavourites(SQLiteDatabase db, MTGCard card) {
        String[] args = new String[]{card.getMultiVerseId() + ""};
        db.rawQuery("DELETE FROM " + FavouritesEntry.TABLE_NAME + " where " + FavouritesEntry._ID + "=? ", args).moveToFirst();
    }

    public static void clear(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("DELETE FROM " + FavouritesEntry.TABLE_NAME, null);
        cursor.moveToFirst();
        cursor.close();
    }
}

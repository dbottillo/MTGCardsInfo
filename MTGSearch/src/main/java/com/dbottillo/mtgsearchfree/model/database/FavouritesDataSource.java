package com.dbottillo.mtgsearchfree.model.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.mtgsearchfree.helper.LOG;
import com.dbottillo.mtgsearchfree.model.MTGCard;

import java.util.ArrayList;

public final class FavouritesDataSource {

    public static final String TABLE = "Favourites";

    private FavouritesDataSource() {

    }

    public static String generateCreateTable() {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        builder.append(TABLE).append(" (_id INTEGER PRIMARY KEY)");
        return builder.toString();
    }


    public static long saveFavourites(SQLiteDatabase db, MTGCard card) {
        Cursor current = db.rawQuery("select * from MTGCard where multiVerseId=?", new String[]{card.getMultiVerseId() + ""});
        if (current.getCount() == 0) {
            // need to add the card
            CardDataSource.saveCard(db, card);
        }
        current.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", card.getMultiVerseId());
        return db.insertWithOnConflict(TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static ArrayList<MTGCard> getCards(SQLiteDatabase db, boolean fullCard) {
        ArrayList<MTGCard> cards = new ArrayList<>();
        String query = "select P.* from MTGCard P inner join Favourites H on (H._id = P.multiVerseId)";
        LOG.d("[getFavourites] query: " + query);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MTGCard card = CardDataSource.fromCursor(cursor, fullCard);
            if (card != null) {
                cards.add(card);
            }
            cursor.moveToNext();
        }
        cursor.close();
        return cards;
    }

    public static void removeFavourites(SQLiteDatabase db, MTGCard card) {
        String[] args = new String[]{card.getMultiVerseId() + ""};
        db.rawQuery("DELETE FROM " + TABLE + " where _id=? ", args).moveToFirst();
    }

    public static void clear(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("DELETE FROM " + TABLE, null);
        cursor.moveToFirst();
        cursor.close();
    }
}

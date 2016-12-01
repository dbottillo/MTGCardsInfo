package com.dbottillo.mtgsearchfree.model.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.ArrayList;
import java.util.List;

public class FavouritesDataSource {

    public static final String TABLE = "Favourites";

    private SQLiteDatabase database;

    public FavouritesDataSource(SQLiteDatabase database) {
        this.database = database;
    }

    public static String generateCreateTable() {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        builder.append(TABLE).append(" (_id INTEGER PRIMARY KEY)");
        return builder.toString();
    }


    public long saveFavourites(MTGCard card) {
        LOG.d("saving " + card.toString() + " as favourite");
        Cursor current = database.rawQuery("select * from MTGCard where multiVerseId=?", new String[]{card.getMultiVerseId() + ""});
        if (current.getCount() == 0) {
            // need to add the card
            CardDataSource.saveCard(database, card);
        }
        current.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", card.getMultiVerseId());
        return database.insertWithOnConflict(TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<MTGCard> getCards(boolean fullCard) {
        LOG.d("get cards, flag full: " + fullCard);
        ArrayList<MTGCard> cards = new ArrayList<>();
        String query = "select P.* from MTGCard P inner join Favourites H on (H._id = P.multiVerseId)";
        LOG.query(query);
        Cursor cursor = database.rawQuery(query, null);
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

    public void removeFavourites(MTGCard card) {
        LOG.d("remove card  " + card.toString() + " from favourites");
        String[] args = new String[]{card.getMultiVerseId() + ""};
        String query = "DELETE FROM " + TABLE + " where _id=? ";
        LOG.query(query);
        database.rawQuery(query, args).moveToFirst();
    }

    public void clear() {
        String query = "DELETE FROM " + TABLE;
        LOG.query(query);
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        cursor.close();
    }
}

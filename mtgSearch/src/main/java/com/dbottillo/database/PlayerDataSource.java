package com.dbottillo.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.dbottillo.helper.LOG;
import com.dbottillo.resources.Player;

import java.util.ArrayList;

public final class PlayerDataSource {

    private PlayerDataSource() {

    }

    public static abstract class PlayerEntry implements BaseColumns {
        public static final String TABLE_NAME = "MTGPlayer";
        public static final String COLUMN_NAME_LIFE = "life";
        public static final String COLUMN_NAME_POISON = "poison";
        public static final String COLUMN_NAME_NAME = "name";
    }

    protected static final String CREATE_PLAYERS_TABLE =
            "CREATE TABLE " + PlayerEntry.TABLE_NAME + " ("
                    + PlayerEntry._ID + " INTEGER PRIMARY KEY,"
                    + PlayerEntry.COLUMN_NAME_LIFE + " INT ,"
                    + PlayerEntry.COLUMN_NAME_POISON + " INT ,"
                    + PlayerEntry.COLUMN_NAME_NAME + " TEXT)";

    public static long savePlayer(SQLiteDatabase db, Player player) {
        return db.insertWithOnConflict(PlayerEntry.TABLE_NAME, null, player.createContentValue(), SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static ArrayList<Player> getPlayers(SQLiteDatabase db) {
        String query = "SELECT * FROM " + PlayerEntry.TABLE_NAME + " order by _ID ASC";
        LOG.d("[getPlayers] query: " + query);
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Player> players = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                players.add(Player.fromCursor(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return players;
    }

    public static void removePlayer(SQLiteDatabase db, Player player) {
        String[] args = new String[]{player.getId() + ""};
        String query = "DELETE FROM " + PlayerEntry.TABLE_NAME + " where " + PlayerEntry._ID + "=? ";
        LOG.d("[getPlayers] query: " + query + " with args: " + player.getId());
        db.rawQuery(query, args).moveToFirst();
    }

}

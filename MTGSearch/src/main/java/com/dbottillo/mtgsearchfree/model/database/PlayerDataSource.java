package com.dbottillo.mtgsearchfree.model.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.ArrayList;
import java.util.List;

public class PlayerDataSource {

    public static final String TABLE = "MTGPlayer";

    private SQLiteDatabase database;

    private enum COLUMNS {
        NAME("name", "TEXT"),
        LIFE("life", "INT"),
        POISON("poison", "INT");

        private String name;
        private String type;

        COLUMNS(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }
    }

    public PlayerDataSource(SQLiteDatabase database) {
        this.database = database;
    }

    public static String generateCreateTable() {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        builder.append(TABLE).append(" (_id INTEGER PRIMARY KEY, ");
        for (COLUMNS column : COLUMNS.values()) {
            builder.append(column.name).append(' ').append(column.type);
            if (column != COLUMNS.POISON) {
                builder.append(',');
            }
        }
        builder.append(')');
        return builder.toString();
    }

    public long savePlayer(Player player) {
        LOG.INSTANCE.d("saving " + player.toString());
        ContentValues values = new ContentValues();
        values.put("_id", player.getId());
        values.put(COLUMNS.LIFE.getName(), player.getLife());
        values.put(COLUMNS.POISON.getName(), player.getPoisonCount());
        values.put(COLUMNS.NAME.getName(), player.getName());
        return database.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<Player> getPlayers() {
        LOG.INSTANCE.d("get players");
        String query = "SELECT * FROM " + TABLE + " order by _ID ASC";
        LOG.INSTANCE.query(query);
        Cursor cursor = database.rawQuery(query, null);
        ArrayList<Player> players = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                players.add(fromCursor(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return players;
    }

    public void removePlayer(Player player) {
        LOG.INSTANCE.d("remove " + player.toString());
        String[] args = new String[]{player.getId() + ""};
        String query = "DELETE FROM " + TABLE + " where _id=? ";
        LOG.INSTANCE.query(query, player.getId() + "");
        Cursor cursor = database.rawQuery(query, args);
        cursor.moveToFirst();
        cursor.close();
    }

    public Player fromCursor(Cursor cursor) {
        Player player = new Player();
        player.setId(cursor.getInt(cursor.getColumnIndex("_id")));
        player.setLife(cursor.getInt(cursor.getColumnIndex(COLUMNS.LIFE.getName())));
        player.setPoisonCount(cursor.getInt(cursor.getColumnIndex(COLUMNS.POISON.getName())));
        player.setName(cursor.getString(cursor.getColumnIndex(COLUMNS.NAME.getName())));
        return player;
    }
}

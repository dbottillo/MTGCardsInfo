package com.dbottillo.database;

import android.content.Context;
import android.util.Log;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.diagnostic.DiagnosticToConsole;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.resources.Player;

import java.util.ArrayList;

public class DB40Helper {

    private static final String TAG = DB40Helper.class.getName();

    public static final String NAME = "mtg_hs_database";

    private static ObjectContainer db;
    private static Context ctx;

    public static void init(Context context) {
        ctx = context;
    }

    public static synchronized boolean openDb() {
        Log.e(TAG, "open db!");
        try {
            if (db == null || db.ext().isClosed()) {
                db = Db4oEmbedded.openFile(dbConfig(), db4oDBFullPath(ctx));
            }
            return true;
        } catch (Exception ie) {
            Log.e(TAG, "[DBHELPER] " + ie.toString());
            return false;
        }
    }

    public static void closeDb() {
        Log.e(TAG, "close db!");
        if (db != null) {
            db.close();
        }
    }

    private static EmbeddedConfiguration dbConfig() {
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common().messageLevel(3);
        configuration.common().diagnostic().addListener(new DiagnosticToConsole());
        configuration.common().objectClass(MTGCard.class).objectField("id").indexed(true);
        configuration.common().objectClass(Player.class).objectField("id").indexed(true);
        return configuration;
    }

    private static String db4oDBFullPath(Context ctx) {
        return ctx.getDir("data", 0) + "/" + NAME + ".db40";
    }

    @SuppressWarnings("rawtypes")
    public void emptyDB() {
        openDb();
        ObjectSet result = db.queryByExample(new Object());
        while (result.hasNext()) {
            db.delete(result.next());
        }
        Log.e(TAG, "[DBELPER] database cleared");
    }

    public void commit(boolean close) {
        db.commit();
        if (close) {
            closeDb();
        }
    }

    public static void storeCard(MTGCard card) {
        if (db.ext().isClosed()) {
            openDb();
        }
        db.store(card);
        db.commit();
        Log.e(TAG, "[DBELPER] card " + card.getName() + " saved inside database");
    }

    public static void removeCard(MTGCard card) {
        if (db.ext().isClosed()) {
            openDb();
        }
        db.delete(card);
        ObjectSet<MTGCard> result = db.query(MTGCard.class);
        while (result.hasNext()) {
            MTGCard next = result.next();
            if (next.getId() == card.getId()) {
                db.delete(next);
            }
        }
        db.commit();
    }

    public static ArrayList<MTGCard> getCards() {
        if (db.ext().isClosed()) {
            openDb();
        }
        ArrayList<MTGCard> cards = new ArrayList<MTGCard>();
        ObjectSet<MTGCard> result = db.query(MTGCard.class);
        while (result.hasNext()) {
            cards.add(result.next());
        }
        return cards;
    }

    public static synchronized boolean isCardStored(MTGCard card) {
        if (db.ext().isClosed()) {
            openDb();
        }
        ObjectSet<MTGCard> result = db.queryByExample(card);
        return !result.isEmpty();
    }

    public static void storePlayer(Player player) {
        db.store(player);
        db.commit();
        Log.e(TAG, "[DBELPER] player " + player.toString() + " saved inside database");
    }

    public static void removePlayer(Player player) {
        ObjectSet<Player> result = db.queryByExample(player);
        if (result.hasNext()) {
            db.delete(result.next());
            db.commit();
        }
    }

    public static ArrayList<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<Player>();
        ObjectSet<Player> result = db.query(Player.class);
        while (result.hasNext()) {
            players.add(result.next());
        }
        return players;
    }
}

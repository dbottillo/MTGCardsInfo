package com.dbottillo.database;

import android.content.Context;
import android.util.Log;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.dbottillo.resources.MTGCard;

import java.util.ArrayList;

/**
 * Created by danielebottillo on 13/07/2014.
 */
public class DB40Helper {

    private static final String TAG = DB40Helper.class.getName();

    public static final String name ="mtgsearch";

    private ObjectContainer db;
    private Context ctx;

    private static DB40Helper dbh;

    public static DB40Helper getInstance(Context ctx){
        if (dbh == null){
            dbh = new DB40Helper(ctx);
        }
        return dbh;
    }


    public DB40Helper(Context ctx){
        this.ctx = ctx;
    }

    public boolean openDb(){
        Log.e(TAG, "open db!");
        try {
            if (db == null || db.ext().isClosed()){
                db = Db4oEmbedded.openFile(dbConfig(), db4oDBFullPath(ctx));
            }
            return true;
        } catch (Exception ie){
            Log.e(TAG, "[DBHELPER] " + ie.toString());
            return false;
        }
    }

    public void closeDb(){
        if (db != null) db.close();
    }

    private EmbeddedConfiguration dbConfig(){
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        //configuration.common().messageLevel(3);
        configuration.common().objectClass(MTGCard.class).indexed(true);
        return configuration;
    }

    private String db4oDBFullPath (Context ctx){
        return ctx.getDir("data",0)+"/"+name+".db40";
    }

    @SuppressWarnings("rawtypes")
    public void emptyDB(){
        openDb();
        ObjectSet result = db.queryByExample(new Object());
        while (result.hasNext()){
            db.delete(result.next());
        }
        Log.e(TAG, "[DBELPER] database cleared");
    }

    public void storeCard(MTGCard card){
        db.store(card);
        db.commit();
        Log.e(TAG, "[DBELPER] user " + card.toString() + " saved inside database");
    }

    public void removeCard(MTGCard card){
        ObjectSet<MTGCard> result = db.queryByExample(card);
        if (result.hasNext()) {
            db.delete(result.next());
            db.commit();
        }
    }

    public void commit(boolean close){
        db.commit();
        if (close) closeDb();
    }

    public ArrayList<MTGCard> getCards(){
        ArrayList<MTGCard> cards = new ArrayList<MTGCard>();
        ObjectSet<MTGCard> result = db.query(MTGCard.class);
        while (result.hasNext()){
            cards.add(result.next());
        }
        return cards;
    }

    public boolean isCardStored(MTGCard card){
        ObjectSet<MTGCard> result = db.queryByExample(card);
        return !result.isEmpty();
    }
}

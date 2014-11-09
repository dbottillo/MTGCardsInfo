package com.dbottillo.helper;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.dbottillo.BuildConfig;
import com.dbottillo.database.CardDatabaseHelper;
import com.dbottillo.database.DB40Helper;
import com.dbottillo.resources.GameCard;
import com.dbottillo.resources.HSCard;
import com.dbottillo.resources.HSSet;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.resources.MTGSet;
import com.dbottillo.resources.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class DBAsyncTask extends AsyncTask<Object, Void, ArrayList<Object>> {

    public interface DBAsyncTaskListener {
        public void onTaskFinished(int type, ArrayList<?> objects);

        public void onTaskEndWithError(int type, String error);
    }

    private boolean error = false;
    private String errorMessage;
    private Context context;
    private String packageName;

    private DBAsyncTaskListener listener;

    public static final int TASK_SET_LIST = 0;
    public static final int TASK_SINGLE_SET = 1;
    public static final int TASK_SEARCH = 2;
    public static final int TASK_SAVED = 3;
    public static final int TASK_PLAYER = 4;
    public static final int TASK_SAVE_CARD = 5;
    public static final int TASK_REMOVE_CARD = 6;
    public static final int TASK_RANDOM_CARD = 7;

    private int type;

    CardDatabaseHelper mDbHelper;
    DB40Helper db40Helper;

    public DBAsyncTask(Context context, DBAsyncTaskListener listener, int type) {
        this.context = context;
        this.listener = listener;
        this.type = type;
        this.mDbHelper = CardDatabaseHelper.getDatabaseHelper(context);
        this.db40Helper = DB40Helper.getInstance(context);
    }

    public void attach(Context context, DBAsyncTaskListener listener) {
        this.listener = listener;
        this.context = context;
    }

    public void detach() {
        this.context = null;
        this.listener = null;
    }

    public DBAsyncTask setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    @Override
    protected ArrayList<Object> doInBackground(Object... params) {
        ArrayList<Object> result = new ArrayList<Object>();

        if (type == TASK_SET_LIST) {

            Cursor cursor = mDbHelper.getSets();

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    if (BuildConfig.magic) {
                        result.add(MTGSet.createMagicSetFromCursor(cursor));
                    } else {
                        HSSet set = HSSet.createHearthstoneSetFromCursor(cursor);
                        result.add(set);
                    }
                    cursor.moveToNext();
                }
            }
            cursor.close();

        } else if (type == TASK_RANDOM_CARD) {
            Cursor cursor = mDbHelper.getRandomCard();
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    result.add(MTGCard.createCardFromCursor(cursor));
                    cursor.moveToNext();
                }
            }
            cursor.close();

        } else if (type == TASK_SAVED) {
            db40Helper.openDb();
            ArrayList<GameCard> cards = db40Helper.getCards();
            for (Object card : cards) {
                result.add(card);
            }
            db40Helper.closeDb();

        } else if (type == TASK_SAVE_CARD) {
            db40Helper.openDb();
            db40Helper.storeCard((GameCard) params[0]);
            ArrayList<GameCard> cards = db40Helper.getCards();
            for (Object card : cards) {
                result.add(card);
            }
            db40Helper.closeDb();

        } else if (type == TASK_REMOVE_CARD) {
            db40Helper.openDb();
            db40Helper.removeCard((GameCard) params[0]);
            ArrayList<GameCard> cards = db40Helper.getCards();
            for (Object card : cards) {
                result.add(card);
            }
            db40Helper.closeDb();

        } else if (type == TASK_PLAYER) {
            ArrayList<Player> players = db40Helper.getPlayers();
            for (Player player : players) {
                result.add(player);
            }
        } else {

            Cursor cursor = null;
            if (type == TASK_SINGLE_SET) {
                cursor = mDbHelper.getSet((String) params[0]);
            } else {
                cursor = mDbHelper.searchCard((String) params[0]);
            }

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    if (BuildConfig.magic) {
                        result.add(MTGCard.createCardFromCursor(cursor));
                    } else {
                        result.add(HSCard.createCardFromCursor(cursor));
                    }

                    cursor.moveToNext();
                }
            }
            cursor.close();

            if (BuildConfig.magic) {
                Collections.sort(result, new Comparator<Object>() {
                    public int compare(Object o1, Object o2) {
                        MTGCard card = (MTGCard) o1;
                        MTGCard card2 = (MTGCard) o2;
                        return card.compareTo(card2);
                    }
                });
            }
        }

        mDbHelper.close();

        return result;
    }

    private int setToLoad(String code) {
        String stringToLoad = code.toLowerCase();
        if (stringToLoad.equalsIgnoreCase("10e")) {
            stringToLoad = "e10";
        } else if (stringToLoad.equalsIgnoreCase("9ed")) {
            stringToLoad = "ed9";
        } else if (stringToLoad.equalsIgnoreCase("5dn")) {
            stringToLoad = "dn5";
        } else if (stringToLoad.equalsIgnoreCase("8ed")) {
            stringToLoad = "ed8";
        } else if (stringToLoad.equalsIgnoreCase("7ed")) {
            stringToLoad = "ed7";
        } else if (stringToLoad.equalsIgnoreCase("6ed")) {
            stringToLoad = "ed6";
        } else if (stringToLoad.equalsIgnoreCase("5ed")) {
            stringToLoad = "ed5";
        } else if (stringToLoad.equalsIgnoreCase("4ed")) {
            stringToLoad = "ed4";
        } else if (stringToLoad.equalsIgnoreCase("3ed")) {
            stringToLoad = "ed3";
        } else if (stringToLoad.equalsIgnoreCase("2ed")) {
            stringToLoad = "ed2";
        }
        return context.getResources().getIdentifier(stringToLoad + "_x", "raw", packageName);
    }

    /*@Override
    protected ArrayList<Object> doInBackground(String... params) {
        ArrayList<Object> result = new ArrayList<Object>();

        int toLoad;
        if (type == TASK_SET_LIST){
            toLoad = R.raw.set_list;
        }else{
            String stringToLoad = params[0].toLowerCase();
            if (stringToLoad.equalsIgnoreCase("10e")){
                stringToLoad = "e10";
            }else if (stringToLoad.equalsIgnoreCase("9ed")){
                stringToLoad = "ed9";
            }else if (stringToLoad.equalsIgnoreCase("5dn")){
                stringToLoad = "dn5";
            }else if (stringToLoad.equalsIgnoreCase("8ed")){
                stringToLoad = "ed8";
            }else if (stringToLoad.equalsIgnoreCase("7ed")){
                stringToLoad = "ed7";
            }else if (stringToLoad.equalsIgnoreCase("6ed")){
                stringToLoad = "ed6";
            }else if (stringToLoad.equalsIgnoreCase("5ed")){
                stringToLoad = "ed5";
            }else if (stringToLoad.equalsIgnoreCase("4ed")){
                stringToLoad = "ed4";
            }else if (stringToLoad.equalsIgnoreCase("3ed")){
                stringToLoad = "ed3";
            }else if (stringToLoad.equalsIgnoreCase("2ed")){
                stringToLoad = "ed2";
            }
            toLoad = context.getResources().getIdentifier(stringToLoad+"_x", "raw", packageName);
        }

        String jsonString = loadFile(toLoad);

        try{
            if (type == TASK_SET_LIST){
                JSONArray json = new JSONArray(jsonString);
                for (int i=json.length()-1; i>=0; i--){
                    JSONObject setJ = json.getJSONObject(i);
                    result.add(MTGSet.createMagicSetFromJson(i, setJ));
                }
            }else{
                JSONObject json = new JSONObject(jsonString);
                JSONArray cards = json.getJSONArray("cards");
                for (int i=0; i<cards.length(); i++){
                    JSONObject cardJ = cards.getJSONObject(i);
                    result.add(MTGCard.createCardFromJson(i, cardJ));
                }
            }
        } catch (JSONException e) {
            error = true;
            errorMessage = e.getLocalizedMessage();
            e.printStackTrace();
        }

        if (type == TASK_SINGLE_SET) {
            Collections.sort(result, new Comparator<Object>() {
                public int compare(Object o1, Object o2) {
                    MTGCard card = (MTGCard) o1;
                    MTGCard card2 = (MTGCard) o2;
                    return card.compareTo(card2);
                }
            });
        }

        return result;
    }*/

    public class MTGCardComparator implements Comparator<MTGCard> {
        @Override
        public int compare(MTGCard o1, MTGCard o2) {
            return 0;
        }
    }

    private String loadFile(int file) {
        InputStream is = context.getResources().openRawResource(file);

        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            error = true;
            errorMessage = e.getLocalizedMessage();
            e.printStackTrace();
        } catch (IOException e) {
            error = true;
            errorMessage = e.getLocalizedMessage();
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                error = true;
                errorMessage = e.getLocalizedMessage();
                e.printStackTrace();
            }
        }

        return writer.toString();
    }

    @Override
    protected void onPostExecute(ArrayList<Object> result) {
        if (listener != null) {
            if (error) {
                listener.onTaskEndWithError(type, errorMessage);
            } else {
                listener.onTaskFinished(type, result);
            }
        }
    }

}

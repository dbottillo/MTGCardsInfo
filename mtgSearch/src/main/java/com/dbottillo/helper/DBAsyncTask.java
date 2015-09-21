package com.dbottillo.helper;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.dbottillo.database.CardsDatabaseHelper;
import com.dbottillo.database.DB40Helper;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.resources.MTGSet;
import com.dbottillo.resources.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DBAsyncTask extends AsyncTask<Object, Void, ArrayList<Object>> {

    public interface DBAsyncTaskListener {
        void onTaskFinished(int type, ArrayList<?> objects);

        void onTaskEndWithError(int type, String error);
    }

    private boolean error = false;

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

    CardsDatabaseHelper mDbHelper;

    public DBAsyncTask(Context context, DBAsyncTaskListener listener, int type) {
        this.listener = listener;
        this.type = type;
        this.mDbHelper = new CardsDatabaseHelper(context);
    }

    public void attach(DBAsyncTaskListener listener) {
        this.listener = listener;
    }

    public void detach() {
        this.listener = null;
    }

    @Override
    protected ArrayList<Object> doInBackground(Object... params) {
        ArrayList<Object> result = new ArrayList<Object>();

        if (type == TASK_SET_LIST) {

            Cursor cursor = mDbHelper.getSets();

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    result.add(MTGSet.createMagicSetFromCursor(cursor));
                    cursor.moveToNext();
                }
            }
            cursor.close();

        } else if (type == TASK_RANDOM_CARD) {
            Cursor cursor = mDbHelper.getRandomCard((Integer) params[0]);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    result.add(MTGCard.createCardFromCursor(cursor));
                    cursor.moveToNext();
                }
            }
            cursor.close();

        } else if (type == TASK_SAVED) {
            DB40Helper.openDb();
            ArrayList<MTGCard> cards = DB40Helper.getCards();
            for (Object card : cards) {
                result.add(card);
            }
            DB40Helper.closeDb();

        } else if (type == TASK_SAVE_CARD) {
            DB40Helper.openDb();
            DB40Helper.storeCard((MTGCard) params[0]);
            ArrayList<MTGCard> cards = DB40Helper.getCards();
            for (Object card : cards) {
                result.add(card);
            }
            DB40Helper.closeDb();

        } else if (type == TASK_REMOVE_CARD) {
            DB40Helper.openDb();
            DB40Helper.removeCard((MTGCard) params[0]);
            ArrayList<MTGCard> cards = DB40Helper.getCards();
            for (Object card : cards) {
                result.add(card);
            }
            DB40Helper.closeDb();

        } else if (type == TASK_PLAYER) {
            ArrayList<Player> players = DB40Helper.getPlayers();
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
                    result.add(MTGCard.createCardFromCursor(cursor));

                    cursor.moveToNext();
                }
            }
            cursor.close();

            Collections.sort(result, new Comparator<Object>() {
                public int compare(Object o1, Object o2) {
                    MTGCard card = (MTGCard) o1;
                    MTGCard card2 = (MTGCard) o2;
                    return card.compareTo(card2);
                }
            });
        }

        mDbHelper.close();

        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<Object> result) {
        if (listener != null) {
            if (error) {
                listener.onTaskEndWithError(type, "error");
            } else {
                listener.onTaskFinished(type, result);
            }
        }
    }

}

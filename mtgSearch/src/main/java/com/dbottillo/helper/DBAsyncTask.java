package com.dbottillo.helper;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.dbottillo.database.MTGDatabaseHelper;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.resources.MTGSet;

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
public class DBAsyncTask extends AsyncTask<String, Void, ArrayList<Object>> {

    public interface DBAsyncTaskListener{
        public void onTaskFinished(ArrayList<?> objects);
        public void onTaskEndWithError(String error);
    }

    private boolean error = false;
    private String errorMessage;
    private Context context;
    private String packageName;

    private DBAsyncTaskListener listener;

    public static final int TASK_SET_LIST = 0;
    public static final int TASK_SINGLE_SET = 1;
    public static final int TASK_SEARCH = 2;

    private int type;

    MTGDatabaseHelper mDbHelper;

    public DBAsyncTask(Context context, DBAsyncTaskListener listener, int type){
        this.context = context;
        this.listener = listener;
        this.type = type;
        this.mDbHelper= new MTGDatabaseHelper(context);
    }

    public DBAsyncTask setPackageName(String packageName){
        this.packageName = packageName;
        return this;
    }

    @Override
    protected ArrayList<Object> doInBackground(String... params) {
        ArrayList<Object> result = new ArrayList<Object>();

        if (type == TASK_SET_LIST){

            Cursor cursor = mDbHelper.getSets();

            if (cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    result.add(MTGSet.createMagicSetFromCursor(cursor));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }else{

            Cursor cursor = null;
            if (type == TASK_SINGLE_SET){
                cursor = mDbHelper.getSet(params[0]);
            }else{
                cursor = mDbHelper.searchCard(params[0]);
            }

            if (cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
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

    private int setToLoad(String code){
        String stringToLoad = code.toLowerCase();
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
        return context.getResources().getIdentifier(stringToLoad+"_x", "raw", packageName);
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

    private String loadFile(int file){
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
        if (error) {
            listener.onTaskEndWithError(errorMessage);
        }else{
            listener.onTaskFinished(result);
        }
    }

}

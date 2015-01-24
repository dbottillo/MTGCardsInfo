package com.dbottillo.helper;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dbottillo.database.CardContract.CardEntry;
import com.dbottillo.database.DatabaseHelper;
import com.dbottillo.database.SetContract.SetEntry;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.resources.MTGSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;

public class CreateDBAsyncTask extends AsyncTask<String, Void, ArrayList<Object>> {

    private boolean error = false;
    private String errorMessage;
    private Context context;
    private String packageName;

    DatabaseHelper mDbHelper;

    public CreateDBAsyncTask(Context context, String packageName) {
        this.context = context;
        this.packageName = packageName;
        this.mDbHelper = new DatabaseHelper(context);
    }

    @Override
    protected ArrayList<Object> doInBackground(String... params) {
        ArrayList<Object> result = new ArrayList<Object>();

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(SetEntry.TABLE_NAME, null, null);
        db.delete(CardEntry.TABLE_NAME, null, null);
        try {
            int set_list = context.getResources().getIdentifier("set_list", "raw", packageName);
            String jsonString = loadFile(set_list);
            JSONArray json = new JSONArray(jsonString);
            for (int i = json.length() - 1; i >= 0; i--) {

                JSONObject setJ = json.getJSONObject(i);
                try {
                    String jsonSetString = loadFile(setToLoad(setJ.getString("code")));

                    long newRowId = db.insert(SetEntry.TABLE_NAME, null, MTGSet.createContentValueFromJSON(setJ));
                    Log.e("MTG", "row id " + newRowId + " -> " + setJ.getString("code"));

                    JSONObject jsonCards = new JSONObject(jsonSetString);
                    JSONArray cards = jsonCards.getJSONArray("cards");
                    //for (int k=0; k<1; k++){
                    for (int k = 0; k < cards.length(); k++) {
                        JSONObject cardJ = cards.getJSONObject(k);
                        //Log.e("BBM", "cardJ "+cardJ);

                        long newRowId2 = db.insert(CardEntry.TABLE_NAME, null, MTGCard.createContentValueFromJSON(cardJ, newRowId, setJ.getString("name")));
                        //Log.e("MTG", "row id card"+newRowId2);
                        //result.add(MTGCard.createCardFromJson(i, cardJ));
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e("MTG", setJ.getString("code") + " file not found");
                }

                /*
            Danieles-MacBook-Pro:~ danielebottillo$ adb -d shell 'run-as com.dbottillo.mtgsearchfree.debug cat /data/data/com.dbottillo.mtgsearchfree.debug/databases/MTGCardsInfo.db > /sdcard/mtgsearch.db'
            Danieles-MacBook-Pro:~ danielebottillo$ adb pull /sdcard/mtgsearch.db
            */
            }
        } catch (JSONException e) {
            Log.e("MTG", "error create db async task: " + e.getLocalizedMessage());
            error = true;
            errorMessage = e.getLocalizedMessage();
            e.printStackTrace();
        }

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

    private int hsSetToLoad(String code) {
        String stringToLoad = code.toLowerCase().replace(" ", "_");
        return context.getResources().getIdentifier(stringToLoad + "", "raw", packageName);
    }


    private String loadFile(int file) throws Resources.NotFoundException {
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
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "finished", Toast.LENGTH_SHORT).show();
        }
    }

}

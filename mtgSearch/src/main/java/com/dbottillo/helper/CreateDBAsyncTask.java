package com.dbottillo.helper;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.dbottillo.database.CardContract;
import com.dbottillo.database.CreateDatabaseHelper;
import com.dbottillo.database.SetContract;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.resources.MTGSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Locale;

public class CreateDBAsyncTask extends AsyncTask<String, Void, ArrayList<Object>> {

    private boolean error = false;
    private String errorMessage;
    private Context context;
    private String packageName;

    CreateDatabaseHelper mDbHelper;

    public CreateDBAsyncTask(Context context, String packageName) {
        this.context = context;
        this.packageName = packageName;
        this.mDbHelper = new CreateDatabaseHelper(context);
    }

    @Override
    protected ArrayList<Object> doInBackground(String... params) {
        ArrayList<Object> result = new ArrayList<Object>();

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(SetContract.SetEntry.TABLE_NAME, null, null);
        db.delete(CardContract.CardEntry.TABLE_NAME, null, null);
        try {
            int setList = context.getResources().getIdentifier("set_list", "raw", packageName);
            String jsonString = loadFile(setList);
            JSONArray json = new JSONArray(jsonString);
            for (int i = json.length() - 1; i >= 0; i--) {

                JSONObject setJ = json.getJSONObject(i);
                try {
                    String jsonSetString = loadFile(setToLoad(setJ.getString("code")));

                    long newRowId = db.insert(SetContract.SetEntry.TABLE_NAME, null, MTGSet.createContentValueFromJSON(setJ));
                    LOG.e("row id " + newRowId + " -> " + setJ.getString("code"));

                    JSONObject jsonCards = new JSONObject(jsonSetString);
                    JSONArray cards = jsonCards.getJSONArray("cards");
                    //for (int k=0; k<1; k++){
                    for (int k = 0; k < cards.length(); k++) {
                        JSONObject cardJ = cards.getJSONObject(k);
                        //Log.e("BBM", "cardJ "+cardJ);

                        long newRowId2 = db.insert(CardContract.CardEntry.TABLE_NAME, null, MTGCard.createContentValueFromJSON(cardJ, newRowId, setJ.getString("name")));
                        //Log.e("MTG", "row id card"+newRowId2);
                        //result.add(MTGCard.createCardFromJson(i, cardJ));
                    }
                } catch (Resources.NotFoundException e) {
                    LOG.e(setJ.getString("code") + " file not found");
                }

                /*
            Danieles-MacBook-Pro:~ danielebottillo$ adb pull /sdcard/MTGCardsInfo.db
            */
            }
        } catch (JSONException e) {
            LOG.e("error create db async task: " + e.getLocalizedMessage());
            error = true;
            errorMessage = e.getLocalizedMessage();
            e.printStackTrace();
        }

        copyDbToSdcard();

        return result;
    }

    public void copyDbToSdcard() {
        LOG.e("copy db to sd card");
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//com.dbottillo.mtgsearchfree.debug//databases//MTGCardsInfo.db";
                String backupDBPath = "MTGCardsInfo.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB)
                            .getChannel();
                    FileChannel dst = new FileOutputStream(backupDB)
                            .getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                } else {
                    LOG.e("current db dont exist");
                }
            } else {
                LOG.e("sd card cannot be write");
            }
        } catch (Exception e) {
            LOG.e("exception copy db: " + e.getLocalizedMessage());
        }
    }

    private int setToLoad(String code) {
        String stringToLoad = code.toLowerCase(Locale.getDefault());
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
            reader.close();
            is.close();
        } catch (IOException e) {
            error = true;
            errorMessage = e.getLocalizedMessage();
            e.printStackTrace();
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

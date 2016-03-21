package com.dbottillo.mtgsearchfree.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.database.CardDataSource;
import com.dbottillo.mtgsearchfree.database.CreateDatabaseHelper;
import com.dbottillo.mtgsearchfree.database.SetDataSource;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.util.FileUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
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
        ArrayList<Object> result = new ArrayList<>();

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(SetDataSource.TABLE, null, null);
        db.delete(CardDataSource.TABLE, null, null);
        try {
            int setList = context.getResources().getIdentifier("set_list", "raw", packageName);
            String jsonString = loadFile(setList);
            JSONArray json = new JSONArray(jsonString);
            for (int i = json.length() - 1; i >= 0; i--) {

                JSONObject setJ = json.getJSONObject(i);
                try {
                    int setToLoad = setToLoad(context, setJ.getString("code"));
                    String jsonSetString = loadFile(setToLoad);

                    long newRowId = db.insert(SetDataSource.TABLE, null, SetDataSource.fromJSON(setJ));
                    LOG.e("row id " + newRowId + " -> " + setJ.getString("code"));

                    JSONObject jsonCards = new JSONObject(jsonSetString);
                    JSONArray cards = jsonCards.getJSONArray("cards");

                    MTGSet set = new MTGSet((int) newRowId);
                    set.setName(setJ.getString("name"));
                    set.setCode(setJ.getString("code"));
                    //for (int k=0; k<1; k++){
                    for (int k = 0; k < cards.length(); k++) {
                        JSONObject cardJ = cards.getJSONObject(k);
                        //Log.e("BBM", "cardJ "+cardJ);

                        long newRowId2 = db.insert(CardDataSource.TABLE, null, createContentValueFromJSON(cardJ, set));
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
        }

        FileUtil.copyDbToSdCard(context, "MTGCardsInfo.db");

        return result;
    }

    public static int setToLoad(Context context, String code) {
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
        return context.getResources().getIdentifier(stringToLoad + "_x", "raw", context.getPackageName());
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

    private static ContentValues createContentValueFromJSON(JSONObject jsonObject, MTGSet set) throws JSONException {
        ContentValues values = new ContentValues();

        boolean isASplit = false;
        if (jsonObject.getString("layout").equalsIgnoreCase("split")) {
            isASplit = true;
        }

        if (!isASplit) {
            values.put(CardDataSource.COLUMNS.NAME.getName(), jsonObject.getString("name"));
        } else {
            JSONArray namesJ = jsonObject.getJSONArray("names");
            StringBuilder names = new StringBuilder();
            for (int k = 0; k < namesJ.length(); k++) {
                String name = namesJ.getString(k);
                names.append(name);
                if (k < namesJ.length() - 1) {
                    names.append('/');
                }
            }
            values.put(CardDataSource.COLUMNS.NAME.getName(), names.toString());
        }
        values.put(CardDataSource.COLUMNS.TYPE.getName(), jsonObject.getString("type"));
        values.put(CardDataSource.COLUMNS.SET_ID.getName(), set.getId());
        values.put(CardDataSource.COLUMNS.SET_NAME.getName(), set.getName());
        values.put(CardDataSource.COLUMNS.SET_CODE.getName(), set.getCode());

        int multicolor;
        int land;
        int artifact;

        if (jsonObject.has("colors")) {
            JSONArray colorsJ = jsonObject.getJSONArray("colors");
            StringBuilder colors = new StringBuilder();
            for (int k = 0; k < colorsJ.length(); k++) {
                String color = colorsJ.getString(k);
                colors.append(color);
                if (k < colorsJ.length() - 1) {
                    colors.append(',');
                }
            }
            values.put(CardDataSource.COLUMNS.COLORS.getName(), colors.toString());

            if (colorsJ.length() > 1) {
                multicolor = 1;
            } else {
                multicolor = 0;
            }
            land = 0;
        } else {
            multicolor = 0;
            land = 1;
        }

        if (jsonObject.has("types")) {
            JSONArray typesJ = jsonObject.getJSONArray("types");
            StringBuilder types = new StringBuilder();
            for (int k = 0; k < typesJ.length(); k++) {
                types.append(typesJ.getString(k));
                if (k < typesJ.length() - 1) {
                    types.append(',');
                }
            }
            values.put(CardDataSource.COLUMNS.TYPES.getName(), types.toString());
        }

        if (jsonObject.getString("type").contains("Artifact")) {
            artifact = 1;
        } else {
            artifact = 0;
        }

        if (jsonObject.has("manaCost")) {
            values.put(CardDataSource.COLUMNS.MANA_COST.getName(), jsonObject.getString("manaCost"));
            land = 0;
        }
        values.put(CardDataSource.COLUMNS.RARITY.getName(), jsonObject.getString("rarity"));

        if (jsonObject.has("multiverseid")) {
            values.put(CardDataSource.COLUMNS.MULTIVERSE_ID.getName(), jsonObject.getInt("multiverseid"));
        }

        String power = "";
        if (jsonObject.has("power")) {
            power = jsonObject.getString("power");
        }
        values.put(CardDataSource.COLUMNS.POWER.getName(), power);

        String toughness = "";
        if (jsonObject.has("toughness")) {
            toughness = jsonObject.getString("toughness");
        }
        values.put(CardDataSource.COLUMNS.TOUGHNESS.getName(), toughness);

        if (!isASplit && jsonObject.has("text")) {
            values.put(CardDataSource.COLUMNS.TEXT.getName(), jsonObject.getString("text"));
        }

        if (isASplit && jsonObject.has("originalText")) {
            values.put(CardDataSource.COLUMNS.TEXT.getName(), jsonObject.getString("originalText"));
        }

        int cmc = -1;
        if (jsonObject.has("cmc")) {
            cmc = jsonObject.getInt("cmc");
        }
        values.put(CardDataSource.COLUMNS.CMC.getName(), cmc);
        values.put(CardDataSource.COLUMNS.MULTICOLOR.getName(), multicolor);
        values.put(CardDataSource.COLUMNS.LAND.getName(), land);
        values.put(CardDataSource.COLUMNS.ARTIFACT.getName(), artifact);

        if (jsonObject.has("rulings")) {
            JSONArray rulingsJ = jsonObject.getJSONArray("rulings");
            values.put(CardDataSource.COLUMNS.RULINGS.getName(), rulingsJ.toString());
        }

        if (jsonObject.has("layout")) {
            values.put(CardDataSource.COLUMNS.LAYOUT.getName(), jsonObject.getString("layout"));
        }

        if (jsonObject.has("number")) {
            values.put(CardDataSource.COLUMNS.NUMBER.getName(), jsonObject.getString("number"));
        }
        return values;
    }

}

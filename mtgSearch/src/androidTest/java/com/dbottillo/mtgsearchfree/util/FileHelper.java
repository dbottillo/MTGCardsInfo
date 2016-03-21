package com.dbottillo.mtgsearchfree.util;

import android.content.Context;
import android.content.res.Resources;

import com.dbottillo.mtgsearchfree.helper.CreateDBAsyncTask;
import com.dbottillo.mtgsearchfree.helper.LOG;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;

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

public final class FileHelper {

    private FileHelper() {

    }

    public static ArrayList<MTGSet> readSetListJSON(Context context) throws JSONException {
        int setList = context.getResources().getIdentifier("set_list", "raw", context.getPackageName());
        String jsonString = loadFile(context, setList);
        JSONArray jsonArray = new JSONArray(jsonString);
        ArrayList<MTGSet> sets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject setJ = jsonArray.getJSONObject(i);
            try {
                MTGSet set = new MTGSet(i);
                set.setName(setJ.getString("name"));
                set.setCode(setJ.getString("code"));
                sets.add(set);
            } catch (Resources.NotFoundException e) {
                LOG.e("e: " + e.getLocalizedMessage());
            }
        }
        return sets;
    }

    public static ArrayList<MTGCard> readSingleSetFile(MTGSet set, Context context) throws JSONException {
        String jsonSetString = loadFile(context, CreateDBAsyncTask.setToLoad(context, set.getCode()));
        JSONObject jsonCards = new JSONObject(jsonSetString);
        JSONArray cardsJ = jsonCards.getJSONArray("cards");
        ArrayList<MTGCard> cards = new ArrayList<>();

        for (int k = 0; k < cardsJ.length(); k++) {
            JSONObject cardJ = cardsJ.getJSONObject(k);
            cards.add(cardFromJSON(cardJ, set));
        }

        return cards;
    }

    private static String loadFile(Context context, int file) throws Resources.NotFoundException {
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
            return null;
        }

        return writer.toString();
    }

    private static MTGCard cardFromJSON(JSONObject jsonObject, MTGSet set) throws JSONException {
        MTGCard card = new MTGCard();
        boolean isASplit = false;
        if (jsonObject.getString("layout").equalsIgnoreCase("split")) {
            isASplit = true;
        }
        if (!isASplit) {
            card.setCardName(jsonObject.getString("name"));
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
            card.setCardName(names.toString());
        }
        card.setType(jsonObject.getString("type"));
        card.setIdSet(set.getId());
        card.setSetName(set.getName());
        card.setSetCode(set.getCode());

        int multicolor;
        int land;
        int artifact;

        if (jsonObject.has("colors")) {
            JSONArray colorsJ = jsonObject.getJSONArray("colors");
            for (int k = 0; k < colorsJ.length(); k++) {
                String color = colorsJ.getString(k);
                card.addColor(MTGCard.mapIntColor(color));
            }

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
            for (int k = 0; k < typesJ.length(); k++) {
                card.addType(typesJ.getString(k));
            }
        }

        if (jsonObject.getString("type").contains("Artifact")) {
            artifact = 1;
        } else {
            artifact = 0;
        }

        if (jsonObject.has("manaCost")) {
            card.setManaCost(jsonObject.getString("manaCost"));
            land = 0;
        }
        card.setRarity(jsonObject.getString("rarity"));

        if (jsonObject.has("multiverseid")) {
            card.setMultiVerseId(jsonObject.getInt("multiverseid"));
        }

        String power = "";
        if (jsonObject.has("power")) {
            power = jsonObject.getString("power");
        }
        card.setPower(power);

        String toughness = "";
        if (jsonObject.has("toughness")) {
            toughness = jsonObject.getString("toughness");
        }
        card.setToughness(toughness);

        if (!isASplit && jsonObject.has("text")) {
            card.setText(jsonObject.getString("text"));
        }

        if (isASplit && jsonObject.has("originalText")) {
            card.setText(jsonObject.getString("originalText"));
        }

        int cmc = -1;
        if (jsonObject.has("cmc")) {
            cmc = jsonObject.getInt("cmc");
        }
        card.setCmc(cmc);
        card.setMultiColor(multicolor == 1);
        card.setAsALand(land == 1);
        card.setAsArtifact(artifact == 1);

        if (jsonObject.has("rulings")) {
            JSONArray rulingsJ = jsonObject.getJSONArray("rulings");
            for (int k = 0; k < rulingsJ.length(); k++) {
                JSONObject ruling = rulingsJ.getJSONObject(k);
                card.addRuling(ruling.getString("text"));
            }
        }

        if (jsonObject.has("layout")) {
            card.setLayout(jsonObject.getString("layout"));
        }

        if (jsonObject.has("number")) {
            card.setNumber(jsonObject.getString("number"));
        }
        return card;
    }
}

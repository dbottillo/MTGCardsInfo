package com.dbottillo.mtgsearchfree.model.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.VisibleForTesting;

import com.crashlytics.android.Crashlytics;
import com.dbottillo.mtgsearchfree.model.CardProperties;
import com.dbottillo.mtgsearchfree.model.Legality;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class CardDataSource {

    public static final int LIMIT = 400;
    public static final String TABLE = "MTGCard";

    private final SQLiteDatabase database;
    private final Gson gson;

    public enum COLUMNS {
        NAME("name", "TEXT"),
        TYPE("type", "TEXT"),
        TYPES("types", "TEXT"),
        SUB_TYPES("subtypes", "TEXT"),
        COLORS("colors", "TEXT"),
        CMC("cmc", "INTEGER"),
        RARITY("rarity", "TEXT"),
        POWER("power", "TEXT"),
        TOUGHNESS("toughness", "TEXT"),
        MANA_COST("manaCost", "TEXT"),
        TEXT("text", "TEXT"),
        MULTICOLOR("multicolor", "INTEGER"),
        LAND("land", "INTEGER"),
        ARTIFACT("artifact", "INTEGER"),
        MULTIVERSE_ID("multiVerseId", "INTEGER"),
        SET_ID("setId", "INTEGER"),
        SET_NAME("setName", "TEXT"),
        RULINGS("rulings", "TEXT"),
        LAYOUT("layout", "TEXT"),
        SET_CODE("setCode", "TEXT"),
        NUMBER("number", "TEXT"),
        NAMES("names", "TEXT"),
        SUPER_TYPES("supertypes", "TEXT"),
        FLAVOR("flavor", "TEXT"),
        ARTIST("artist", "TEXT"),
        LOYALTY("loyalty", "INTEGER"),
        PRINTINGS("printings", "TEXT"),
        LEGALITIES("legalities", "TEXT"),
        ORIGINAL_TEXT("originalText", "TEXT"),
        MCI_NUMBER("mciNumber", "TEXT"),
        COLORS_IDENTITY("colorIdentity", "TEXT");

        private String name;
        private String type;

        COLUMNS(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }

    public CardDataSource(SQLiteDatabase database, Gson gson) {
        this.database = database;
        this.gson = gson;
    }

    static final String SQL_ADD_COLUMN_RULINGS = "ALTER TABLE "
            + TABLE + " ADD COLUMN "
            + COLUMNS.RULINGS.getName() + " " + COLUMNS.RULINGS.getType();

    static final String SQL_ADD_COLUMN_LAYOUT = "ALTER TABLE "
            + TABLE + " ADD COLUMN "
            + COLUMNS.LAYOUT.getName() + " " + COLUMNS.LAYOUT.getType();

    static final String SQL_ADD_COLUMN_SET_CODE = "ALTER TABLE "
            + TABLE + " ADD COLUMN "
            + COLUMNS.SET_CODE.getName() + " " + COLUMNS.SET_CODE.getType();

    static final String SQL_ADD_COLUMN_NUMBER = "ALTER TABLE "
            + TABLE + " ADD COLUMN "
            + COLUMNS.NUMBER.getName() + " " + COLUMNS.NUMBER.getType();

    static final String SQL_ADD_COLUMN_NAMES = "ALTER TABLE "
            + TABLE + " ADD COLUMN "
            + COLUMNS.NAMES.getName() + " " + COLUMNS.NAMES.getType();

    static final String SQL_ADD_COLUMN_SUPER_TYPES = "ALTER TABLE "
            + TABLE + " ADD COLUMN "
            + COLUMNS.SUPER_TYPES.getName() + " " + COLUMNS.SUPER_TYPES.getType();

    static final String SQL_ADD_COLUMN_FLAVOR = "ALTER TABLE "
            + TABLE + " ADD COLUMN "
            + COLUMNS.FLAVOR.getName() + " " + COLUMNS.FLAVOR.getType();

    static final String SQL_ADD_COLUMN_ARTIST = "ALTER TABLE "
            + TABLE + " ADD COLUMN "
            + COLUMNS.ARTIST.getName() + " " + COLUMNS.ARTIST.getType();

    static final String SQL_ADD_COLUMN_LOYALTY = "ALTER TABLE "
            + TABLE + " ADD COLUMN "
            + COLUMNS.LOYALTY.getName() + " " + COLUMNS.LOYALTY.getType();

    static final String SQL_ADD_COLUMN_PRINTINGS = "ALTER TABLE "
            + TABLE + " ADD COLUMN "
            + COLUMNS.PRINTINGS.getName() + " " + COLUMNS.PRINTINGS.getType();

    static final String SQL_ADD_COLUMN_LEGALITIES = "ALTER TABLE "
            + TABLE + " ADD COLUMN "
            + COLUMNS.LEGALITIES.getName() + " " + COLUMNS.LEGALITIES.getType();

    static final String SQL_ADD_COLUMN_ORIGINAL_TEXT = "ALTER TABLE "
            + TABLE + " ADD COLUMN "
            + COLUMNS.ORIGINAL_TEXT.getName() + " " + COLUMNS.ORIGINAL_TEXT.getType();

    static final String SQL_ADD_COLUMN_MCI_NUMBER = "ALTER TABLE "
            + TABLE + " ADD COLUMN "
            + COLUMNS.MCI_NUMBER.getName() + " " + COLUMNS.MCI_NUMBER.getType();

    static final String SQL_ADD_COLUMN_COLORS_IDENTITY = "ALTER TABLE "
            + TABLE + " ADD COLUMN "
            + COLUMNS.COLORS_IDENTITY.getName() + " " + COLUMNS.COLORS_IDENTITY.getType();

    public static String generateCreateTable() {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        builder.append(TABLE).append(" (_id INTEGER PRIMARY KEY, ");
        for (COLUMNS column : COLUMNS.values()) {
            builder.append(column.name).append(' ').append(column.type);
            if (column != COLUMNS.COLORS_IDENTITY) {
                builder.append(',');
            }
        }
        return builder.append(')').toString();
    }

    @VisibleForTesting
    public static String generateCreateTable(int version) {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        builder.append(TABLE).append(" (_id INTEGER PRIMARY KEY, ");
        COLUMNS lastColumn = COLUMNS.ORIGINAL_TEXT;
        if (version < 2) {
            lastColumn = COLUMNS.SET_NAME;
        } else if (version < 3) {
            lastColumn = COLUMNS.LAYOUT;
        } else if (version < 7) {
            lastColumn = COLUMNS.NUMBER;
        }
        for (COLUMNS column : COLUMNS.values()) {
            boolean addColumn = true;
            if ((column == COLUMNS.RULINGS || column == COLUMNS.LAYOUT) && version <= 1) {
                addColumn = false;
            } else if ((column == COLUMNS.NUMBER || column == COLUMNS.SET_CODE) && version <= 2) {
                addColumn = false;
            } else if ((column == COLUMNS.NAMES || column == COLUMNS.SUPER_TYPES
                    || column == COLUMNS.FLAVOR || column == COLUMNS.ARTIST
                    || column == COLUMNS.LOYALTY || column == COLUMNS.PRINTINGS
                    || column == COLUMNS.LEGALITIES)|| column == COLUMNS.ORIGINAL_TEXT
                    && version <= 6) {
                addColumn = false;
            } else if ((column == COLUMNS.COLORS_IDENTITY || column == COLUMNS.MCI_NUMBER)
                    && version <= 7) {
                addColumn = false;
            }
            if (addColumn) {
                builder.append(column.name).append(' ').append(column.type);
                if (column != lastColumn) {
                    builder.append(',');
                }
            }
        }
        return builder.append(')').toString();
    }

    public long saveCard(MTGCard card) {
        return database.insertWithOnConflict(TABLE, null, createContentValue(card), SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void removeCard(MTGCard card) {
        String removeQuery = "DELETE FROM " + TABLE + " where _id=?";
        Cursor cursor = database.rawQuery(removeQuery, new String[]{String.valueOf(card.getId())});
        cursor.moveToFirst();
        cursor.close();
    }

    public List<MTGCard> getCards() {
        Cursor cursor = database.rawQuery("select * from " + CardDataSource.TABLE, null);
        List<MTGCard> cards = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                cards.add(fromCursor(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return cards;
    }

    ContentValues createContentValue(MTGCard card) {
        ContentValues values = new ContentValues();
        values.put(COLUMNS.NAME.getName(), card.getName());
        values.put(COLUMNS.TYPE.getName(), card.getType());
        values.put(COLUMNS.SET_ID.getName(), card.getSet().getId());
        values.put(COLUMNS.SET_NAME.getName(), card.getSet().getName());
        values.put(COLUMNS.SET_CODE.getName(), card.getSet().getCode());
        List<Integer> colors = card.getColors();
        if (colors.size() > 0) {
            StringBuilder col = new StringBuilder();
            for (int k = 0; k < colors.size(); k++) {
                String color = CardProperties.COLOR.getStringFromNumber(colors.get(k));
                col.append(color);
                if (k < colors.size() - 1) {
                    col.append(',');
                }
            }
            values.put(COLUMNS.COLORS.getName(), col.toString());
        }
        List<String> types = card.getTypes();
        if (types.size() > 0) {
            StringBuilder typ = new StringBuilder();
            for (int k = 0; k < types.size(); k++) {
                typ.append(types.get(k));
                if (k < types.size() - 1) {
                    typ.append(',');
                }
            }
            values.put(COLUMNS.TYPES.getName(), typ.toString());
        }
        List<String> subTypes = card.getSubTypes();
        if (subTypes.size() > 0) {
            StringBuilder typ = new StringBuilder();
            for (int k = 0; k < subTypes.size(); k++) {
                typ.append(subTypes.get(k));
                if (k < subTypes.size() - 1) {
                    typ.append(',');
                }
            }
            values.put(COLUMNS.SUB_TYPES.getName(), typ.toString());
        }
        values.put(COLUMNS.MANA_COST.getName(), card.getManaCost());
        values.put(COLUMNS.RARITY.getName(), card.getRarity());
        values.put(COLUMNS.MULTIVERSE_ID.getName(), card.getMultiVerseId());
        values.put(COLUMNS.POWER.getName(), card.getPower());
        values.put(COLUMNS.TOUGHNESS.getName(), card.getToughness());
        values.put(COLUMNS.TEXT.getName(), card.getText());
        values.put(COLUMNS.CMC.getName(), card.getCmc());
        values.put(COLUMNS.MULTICOLOR.getName(), card.isMultiColor());
        values.put(COLUMNS.LAND.getName(), card.isLand());
        values.put(COLUMNS.ARTIFACT.getName(), card.isArtifact());
        List<String> rulings = card.getRulings();
        if (rulings.size() > 0) {
            JSONArray rules = new JSONArray();
            for (String rule : rulings) {
                JSONObject rulJ = new JSONObject();
                try {
                    rulJ.put("text", rule);
                    rules.put(rulJ);
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                    LOG.e(e);
                }
            }
            values.put(COLUMNS.RULINGS.getName(), rules.toString());
        }
        values.put(COLUMNS.LAYOUT.getName(), card.getLayout());
        values.put(COLUMNS.NUMBER.getName(), card.getNumber());

        values.put(COLUMNS.NAMES.getName(), gson.toJson(card.getNames()));
        values.put(COLUMNS.SUPER_TYPES.getName(), gson.toJson(card.getSuperTypes()));
        values.put(COLUMNS.FLAVOR.getName(), card.getFlavor());
        values.put(COLUMNS.ARTIST.getName(), card.getArtist());
        values.put(COLUMNS.LOYALTY.getName(), card.getLoyalty());
        values.put(COLUMNS.PRINTINGS.getName(), gson.toJson(card.getPrintings()));
        values.put(COLUMNS.ORIGINAL_TEXT.getName(), card.getOriginalText());
        values.put(COLUMNS.MCI_NUMBER.getName(), card.getMciNumber());
        values.put(COLUMNS.COLORS_IDENTITY.getName(), gson.toJson(card.getColorsIdentity()));

        return values;
    }


    public MTGCard fromCursor(Cursor cursor) {
        return fromCursor(cursor, true);
    }

    public MTGCard fromCursor(Cursor cursor, boolean fullCard) {
        if (cursor.getColumnIndex("_id") == -1) {
            return null;
        }
        MTGCard card = new MTGCard(cursor.getInt(cursor.getColumnIndex("_id")));
        if (cursor.getColumnIndex(COLUMNS.MULTIVERSE_ID.getName()) != -1) {
            card.setMultiVerseId(cursor.getInt(cursor.getColumnIndex(COLUMNS.MULTIVERSE_ID.getName())));
        }
        if (!fullCard) {
            return card;
        }
        card.setType(cursor.getString(cursor.getColumnIndex(COLUMNS.TYPE.getName())));
        card.setCardName(cursor.getString(cursor.getColumnIndex(COLUMNS.NAME.getName())));

        int setId = cursor.getInt(cursor.getColumnIndex(COLUMNS.SET_ID.getName()));
        MTGSet set = new MTGSet(setId);
        set.setName(cursor.getString(cursor.getColumnIndex(COLUMNS.SET_NAME.getName())));
        if (cursor.getColumnIndex(COLUMNS.SET_CODE.getName()) > -1) {
            set.setCode(cursor.getString(cursor.getColumnIndex(COLUMNS.SET_CODE.getName())));
        } else {
            set.setCode(null);
        }
        card.belongsTo(set);

        if (cursor.getColumnIndex(COLUMNS.COLORS.getName()) != -1) {
            String colors = cursor.getString(cursor.getColumnIndex(COLUMNS.COLORS.getName()));
            if (colors != null) {
                String[] splitted = colors.split(",");
                for (String aSplitted : splitted) {
                    card.addColor(aSplitted);
                }
            }
        }
        if (cursor.getColumnIndex(COLUMNS.TYPES.getName()) != -1) {
            String types = cursor.getString(cursor.getColumnIndex(COLUMNS.TYPES.getName()));
            if (types != null) {
                String[] splitted = types.split(",");
                for (String aSplitted : splitted) {
                    card.addType(aSplitted);
                }
            }
        }
        if (cursor.getColumnIndex(COLUMNS.SUB_TYPES.getName()) != -1) {
            String subTypes = cursor.getString(cursor.getColumnIndex(COLUMNS.SUB_TYPES.getName()));
            if (subTypes != null) {
                String[] splitted = subTypes.split(",");
                for (String aSplitted : splitted) {
                    card.addSubType(aSplitted);
                }
            }
        }

        if (cursor.getColumnIndex(COLUMNS.MANA_COST.getName()) != -1) {
            String manaCost = cursor.getString(cursor.getColumnIndex(COLUMNS.MANA_COST.getName()));
            if (manaCost !=  null) {
                card.setManaCost(manaCost);
            }
        }

        card.setRarity(cursor.getString(cursor.getColumnIndex(COLUMNS.RARITY.getName())));
        card.setPower(cursor.getString(cursor.getColumnIndex(COLUMNS.POWER.getName())));
        card.setToughness(cursor.getString(cursor.getColumnIndex(COLUMNS.TOUGHNESS.getName())));

        if (cursor.getColumnIndex(COLUMNS.TEXT.getName()) != -1) {
            String text = cursor.getString(cursor.getColumnIndex(COLUMNS.TEXT.getName()));
            if (text != null) {
                card.setText(text);
            }
        }

        card.setCmc(cursor.getInt(cursor.getColumnIndex(COLUMNS.CMC.getName())));
        card.setMultiColor(cursor.getInt(cursor.getColumnIndex(COLUMNS.MULTICOLOR.getName())) == 1);
        card.setLand(cursor.getInt(cursor.getColumnIndex(COLUMNS.LAND.getName())) == 1);
        card.setArtifact(cursor.getInt(cursor.getColumnIndex(COLUMNS.ARTIFACT.getName())) == 1);

        String rulings = cursor.getString(cursor.getColumnIndex(COLUMNS.RULINGS.getName()));
        if (rulings != null) {
            try {
                JSONArray jsonArray = new JSONArray(rulings);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject rule = jsonArray.getJSONObject(i);
                    card.addRuling(rule.getString("text"));
                }
            } catch (JSONException e) {
                Crashlytics.logException(e);
                LOG.e(e);
            }
        }

        Type type = new TypeToken<List<String>>() {
        }.getType();

        if (cursor.getColumnIndex(COLUMNS.LAYOUT.getName()) != -1) {
            card.setLayout(cursor.getString(cursor.getColumnIndex(COLUMNS.LAYOUT.getName())));
        }
        if (cursor.getColumnIndex(COLUMNS.NUMBER.getName()) != -1) {
            card.setNumber(cursor.getString(cursor.getColumnIndex(COLUMNS.NUMBER.getName())));
        }

        if (cursor.getColumnIndex(COLUMNS.NAMES.getName()) != -1) {
            String names = cursor.getString(cursor.getColumnIndex(COLUMNS.NAMES.getName()));
            if (names != null) {
                List<String> strings = gson.fromJson(names, type);
                card.setNames(strings);
            }
        }
        if (cursor.getColumnIndex(COLUMNS.SUPER_TYPES.getName()) != -1) {
            String superTypes = cursor.getString(cursor.getColumnIndex(COLUMNS.SUPER_TYPES.getName()));
            if (superTypes != null) {
                List<String> strings = gson.fromJson(superTypes, type);
                card.setSuperTypes(strings);
            }
        }
        if (cursor.getColumnIndex(COLUMNS.LOYALTY.getName()) != -1) {
            card.setLoyalty(cursor.getInt(cursor.getColumnIndex(COLUMNS.LOYALTY.getName())));
        }
        String artist = getString(cursor, COLUMNS.ARTIST);
        if (artist != null){
            card.setArtist(artist);
        }
        String flavor = getString(cursor, COLUMNS.FLAVOR);
        if (flavor != null){
            card.setFlavor(flavor);
        }
        if (cursor.getColumnIndex(COLUMNS.PRINTINGS.getName()) != -1) {
            String printings = cursor.getString(cursor.getColumnIndex(COLUMNS.PRINTINGS.getName()));
            if (printings != null) {
                List<String> strings = gson.fromJson(printings, type);
                card.setPrintings(strings);
            }
        }
        if (cursor.getColumnIndex(COLUMNS.ORIGINAL_TEXT.getName()) != -1) {
            String originalText = cursor.getString(cursor.getColumnIndex(COLUMNS.ORIGINAL_TEXT.getName()));
            if (originalText != null) {
                card.setOriginalText(originalText);
            }
        }

        if (cursor.getColumnIndex(COLUMNS.MCI_NUMBER.getName()) != -1) {
            card.setMciNumber(cursor.getString(cursor.getColumnIndex(COLUMNS.MCI_NUMBER.getName())));
        }

        if (cursor.getColumnIndex(COLUMNS.COLORS_IDENTITY.getName()) != -1) {
            String colorsIdentity = cursor.getString(cursor.getColumnIndex(COLUMNS.COLORS_IDENTITY.getName()));
            if (colorsIdentity != null) {
                List<String> strings = gson.fromJson(colorsIdentity, type);
                card.setColorsIdentity(strings);
            }
        }

        String legalities = cursor.getString(cursor.getColumnIndex(COLUMNS.LEGALITIES.getName()));
        if (legalities != null) {
            try {
                JSONArray jsonArray = new JSONArray(legalities);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject rule = jsonArray.getJSONObject(i);
                    String format = rule.getString("format");
                    String legality = rule.getString("legality");
                    card.addLegality(new Legality(format, legality));
                }
            } catch (JSONException e) {
                try {
                    JSONObject legalitiesJ = new JSONObject(legalities);
                    Iterator keys = legalitiesJ.keys();
                    while(keys.hasNext()){
                        String format = (String) keys.next();
                        String legality = legalitiesJ.getString(format);
                        card.addLegality(new Legality(format, legality));
                    }
                }catch (JSONException e2){
                    Crashlytics.logException(e2);
                    LOG.e(e2);
                }

            }
        }

        return card;
    }

    private String getString(Cursor cursor, COLUMNS column){
        if (cursor.getColumnIndex(column.getName()) != -1) {
            return cursor.getString(cursor.getColumnIndex(column.getName()));
        }
        return null;
    }
}

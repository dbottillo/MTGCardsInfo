package com.dbottillo.mtgsearchfree.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.mtgsearchfree.helper.LOG;
import com.dbottillo.mtgsearchfree.resources.MTGCard;
import com.dbottillo.mtgsearchfree.search.SearchParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CardDataSource {

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
        SET_CODE("setCode", "TEXT"),
        RULINGS("rulings", "TEXT"),
        LAYOUT("layout", "TEXT"),
        NUMBER("number", "TEXT");

        private String name;
        private String type;

        COLUMNS(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }
    }

    public static final int LIMIT = 400;
    public static final String TABLE = "MTGCard";

    private CardDataSource() {
    }

    public static String generateCreateTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE + " (_id INTEGER PRIMARY KEY, ";
        for (COLUMNS column : COLUMNS.values()) {
            query += column.name + " " + column.type + ",";
        }
        return query.substring(0, query.length() - 1) + ")";
    }

    public static long saveCard(SQLiteDatabase database, MTGCard card) {
        return database.insertWithOnConflict(CardContract.CardEntry.TABLE_NAME, null, createContentValue(card), SQLiteDatabase.CONFLICT_IGNORE);
    }

    public static Cursor getSet(SQLiteDatabase db, String idSet) {
        String query = "SELECT * FROM " + CardContract.CardEntry.TABLE_NAME + " WHERE " + CardContract.CardEntry.COLUMN_NAME_SET_ID + " = ?";
        LOG.d("[getSet] query: " + query + " with id: " + idSet);
        return db.rawQuery(query, new String[]{idSet});
    }

    public static Cursor searchCards(SQLiteDatabase db, SearchParams searchParams) {
        String query = "SELECT * FROM " + CardContract.CardEntry.TABLE_NAME + " WHERE ";
        ArrayList<String> selection = new ArrayList<>();

        boolean first = true;
        if (searchParams.getName().length() > 0) {
            query += composeQuery(true, CardContract.CardEntry.COLUMN_NAME_NAME);
            selection.add("%" + searchParams.getName() + "%");
            first = false;
        }
        if (searchParams.getTypes().length() > 0) {
            query += composeQuery(first, CardContract.CardEntry.COLUMN_NAME_TYPE);
            first = false;
            selection.add("%" + searchParams.getTypes().trim() + "%");
        }
        if (searchParams.getText().length() > 0) {
            query += composeQuery(first, CardContract.CardEntry.COLUMN_NAME_TEXT);
            first = false;
            selection.add("%" + searchParams.getText().trim() + "%");
        }
        if (searchParams.getCmc().getValue() > 0) {
            query += composeQuery(first, CardContract.CardEntry.COLUMN_NAME_CMC, searchParams.getCmc().getOperator());
            first = false;
            selection.add("" + searchParams.getCmc().getValue());
        }
        if (searchParams.getPower().getValue() > 0) {
            query += composeQuery(first, CardContract.CardEntry.COLUMN_NAME_POWER, searchParams.getPower().getOperator());
            first = false;
            selection.add("" + searchParams.getPower().getValue());
        }
        if (searchParams.getTough().getValue() > 0) {
            query += composeQuery(first, CardContract.CardEntry.COLUMN_NAME_TOUGHNESS, searchParams.getTough().getOperator());
            first = false;
            selection.add("" + searchParams.getTough().getValue());
        }
        if (searchParams.isNomulti()) {
            if (!first) {
                query += "AND ";
            }
            first = false;
            query += CardContract.CardEntry.COLUMN_NAME_MULTICOLOR + " == 0 ";
        }
        if (searchParams.onlyMulti()) {
            if (!first) {
                query += "AND ";
            }
            first = false;
            query += CardContract.CardEntry.COLUMN_NAME_MULTICOLOR + " == 1 ";
        }
        if (searchParams.getSetId() > 0) {
            if (!first) {
                query += "AND ";
            }
            first = false;
            query += CardContract.CardEntry.COLUMN_NAME_SET_ID + " == " + searchParams.getSetId() + " ";
        }
        if (searchParams.getSetId() == -2) {
            // special case for standard
            if (!first) {
                query += "AND ";
            }
            first = false;
            query += "(setId == 3 OR setId == 4 OR setId == 6 OR setId == 8 OR setId == 10) ";
        }
        if (searchParams.atLeastOneColor()) {
            if (!first) {
                query += "AND ";
            }
            query += "(";
            String colorOperator = searchParams.onlyMulti() ? "AND " : "OR ";
            boolean firstColor = true;
            if (searchParams.isWhite()) {
                query += composeQueryColor(true, colorOperator);
                firstColor = false;
                selection.add("%W%");
            }
            if (searchParams.isBlue()) {
                query += composeQueryColor(firstColor, colorOperator);
                firstColor = false;
                selection.add("%U%");
            }
            if (searchParams.isBlack()) {
                query += composeQueryColor(firstColor, colorOperator);
                firstColor = false;
                selection.add("%B%");
            }
            if (searchParams.isRed()) {
                query += composeQueryColor(firstColor, colorOperator);
                firstColor = false;
                selection.add("%R%");
            }
            if (searchParams.isGreen()) {
                query += composeQueryColor(firstColor, colorOperator);
                selection.add("%G%");
            }
            first = false;
            query += ")";
        }
        if (searchParams.atLeastOneRarity()) {
            if (!first) {
                query += "AND ";
            }
            query += "(";
            boolean firstRarity = true;
            if (searchParams.isCommon()) {
                query += CardContract.CardEntry.COLUMN_NAME_RARITY + "='Common' ";
                firstRarity = false;
            }
            if (searchParams.isUncommon()) {
                if (!firstRarity) {
                    query += "OR ";
                }
                query += CardContract.CardEntry.COLUMN_NAME_RARITY + "='Uncommon' ";
                firstRarity = false;
            }
            if (searchParams.isRare()) {
                if (!firstRarity) {
                    query += "OR ";
                }
                query += CardContract.CardEntry.COLUMN_NAME_RARITY + "='Rare' ";
                firstRarity = false;
            }
            if (searchParams.isMythic()) {
                if (!firstRarity) {
                    query += "OR ";
                }
                query += CardContract.CardEntry.COLUMN_NAME_RARITY + "='Mythic Rare' ";
            }
            query += ")";
        }

        query += " ORDER BY " + CardContract.CardEntry.COLUMN_NAME_MULTIVERSEID + " DESC LIMIT " + LIMIT;

        String[] sel = Arrays.copyOf(selection.toArray(), selection.size(), String[].class);

        LOG.d("[searchCards] query: " + query + " with selection: " + selection);

        return db.rawQuery(query, sel);
    }

    private static String composeQuery(boolean first, String column, String operator) {
        String query = "";
        if (!first) {
            query += "AND ";
        }
        query += column + " " + operator + " ? ";
        return query;
    }

    private static String composeQuery(boolean first, String column) {
        return composeQuery(first, column, "LIKE");
    }

    private static String composeQueryColor(boolean first, String operator) {
        String query = "";
        if (!first) {
            query += operator;
        }
        query += CardContract.CardEntry.COLUMN_NAME_MANACOST + " LIKE ? ";
        return query;
    }

    public static Cursor getRandomCard(SQLiteDatabase db, int number) {
        String query = "SELECT * FROM " + CardContract.CardEntry.TABLE_NAME + " ORDER BY RANDOM() LIMIT " + number;
        LOG.d("[getRandomCard] query: " + query);
        return db.rawQuery(query, null);
    }

    public static ContentValues createContentValue(MTGCard card) {
        ContentValues values = new ContentValues();
        if (card.getId() > -1) {
            values.put("_id", card.getId());
        }
        values.put(CardContract.CardEntry.COLUMN_NAME_NAME, card.getName());
        values.put(CardContract.CardEntry.COLUMN_NAME_TYPE, card.getType());
        values.put(CardContract.CardEntry.COLUMN_NAME_SET_ID, card.getIdSet());
        values.put(CardContract.CardEntry.COLUMN_NAME_SET_NAME, card.getSetName());
        values.put(CardContract.CardEntry.COLUMN_NAME_SET_CODE, card.getSetCode());
        List<Integer> colors = card.getColors();
        if (colors.size() > 0) {
            StringBuilder col = new StringBuilder();
            for (int k = 0; k < colors.size(); k++) {
                String color = MTGCard.mapStringColor(colors.get(k));
                col.append(color);
                if (k < colors.size() - 1) {
                    col.append(',');
                }
            }
            values.put(CardContract.CardEntry.COLUMN_NAME_COLORS, col.toString());
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
            values.put(CardContract.CardEntry.COLUMN_NAME_TYPES, typ.toString());
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
            values.put(CardContract.CardEntry.COLUMN_NAME_SUBTYPES, typ.toString());
        }
        values.put(CardContract.CardEntry.COLUMN_NAME_MANACOST, card.getManaCost());
        values.put(CardContract.CardEntry.COLUMN_NAME_RARITY, card.getRarity());
        values.put(CardContract.CardEntry.COLUMN_NAME_MULTIVERSEID, card.getMultiVerseId());
        values.put(CardContract.CardEntry.COLUMN_NAME_POWER, card.getPower());
        values.put(CardContract.CardEntry.COLUMN_NAME_TOUGHNESS, card.getToughness());
        values.put(CardContract.CardEntry.COLUMN_NAME_TEXT, card.getText());
        values.put(CardContract.CardEntry.COLUMN_NAME_CMC, card.getCmc());
        values.put(CardContract.CardEntry.COLUMN_NAME_MULTICOLOR, card.isMultiColor());
        values.put(CardContract.CardEntry.COLUMN_NAME_LAND, card.isLand());
        values.put(CardContract.CardEntry.COLUMN_NAME_ARTIFACT, card.isArtifact());
        List<String> rulings = card.getRulings();
        if (rulings.size() > 0) {
            JSONArray rules = new JSONArray();
            for (String rule : rulings) {
                JSONObject rulJ = new JSONObject();
                try {
                    rulJ.put("text", rule);
                    rules.put(rulJ);
                } catch (JSONException e) {
                    LOG.d("[MTGCard] exception: " + e.getLocalizedMessage());
                }
            }
            values.put(CardContract.CardEntry.COLUMN_NAME_RULINGS, rules.toString());
        }
        values.put(CardContract.CardEntry.COLUMN_NAME_LAYOUT, card.getLayout());
        values.put(CardContract.CardEntry.COLUMN_NAME_NUMBER, card.getNumber());
        return values;
    }

    public static MTGCard fromCursor(Cursor cursor) {
        return fromCursor(cursor, true);
    }

    public static MTGCard fromCursor(Cursor cursor, boolean fullCard) {
        if (cursor.getColumnIndex(CardContract.CardEntry._ID) == -1) {
            return null;
        }
        MTGCard card = new MTGCard(cursor.getInt(cursor.getColumnIndex(CardContract.CardEntry._ID)));
        if (cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_MULTIVERSEID) != -1) {
            card.setMultiVerseId(cursor.getInt(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_MULTIVERSEID)));
        }
        if (!fullCard) {
            return card;
        }
        card.setType(cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_TYPE)));
        card.setCardName(cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_NAME)));

        card.setIdSet(cursor.getInt(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_SET_ID)));
        card.setSetName(cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_SET_NAME)));
        if (cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_SET_CODE) > -1) {
            card.setSetCode(cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_SET_CODE)));
        } else {
            card.setSetCode(null);
        }

        if (cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_COLORS) != -1) {
            String colors = cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_COLORS));
            if (colors != null) {
                String[] splitted = colors.split(",");
                for (String aSplitted : splitted) {
                    card.addColor(MTGCard.mapIntColor(aSplitted));
                }
            }
        }
        if (cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_TYPES) != -1) {
            String types = cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_TYPES));
            if (types != null) {
                String[] splitted = types.split(",");
                for (String aSplitted : splitted) {
                    card.addType(aSplitted);
                }
            }
        }
        if (cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_SUBTYPES) != -1) {
            String subTypes = cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_SUBTYPES));
            if (subTypes != null) {
                String[] splitted = subTypes.split(",");
                for (String aSplitted : splitted) {
                    card.addSubType(aSplitted);
                }
            }
        }

        if (cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_MANACOST) != -1) {
            card.setManaCost(cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_MANACOST)));
        }

        card.setRarity(cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_RARITY)));

        card.setPower(cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_POWER)));
        card.setToughness(cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_TOUGHNESS)));

        if (cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_TEXT) != -1) {
            card.setText(cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_TEXT)));
        }

        card.setCmc(cursor.getInt(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_CMC)));

        card.setMultiColor(cursor.getInt(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_MULTICOLOR)) == 1);
        card.setAsALand(cursor.getInt(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_LAND)) == 1);
        card.setAsArtifact(cursor.getInt(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_ARTIFACT)) == 1);

        card.setAsEldrazi(false);
        if (!card.isMultiColor() && !card.isLand() && !card.isArtifact() && card.getColors().size() == 0) {
            card.setAsEldrazi(true);
        }

        String rulings = cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_RULINGS));
        if (rulings != null) {
            try {
                JSONArray jsonArray = new JSONArray(rulings);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject rule = jsonArray.getJSONObject(i);
                    card.addRuling(rule.getString("text"));
                }
            } catch (JSONException e) {
                LOG.d("[MTGCard] exception: " + e.getLocalizedMessage());
            }
        }

        if (cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_LAYOUT) != -1) {
            card.setLayout(cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_LAYOUT)));
        }
        if (cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_NUMBER) != -1) {
            card.setNumber(cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.COLUMN_NAME_NUMBER)));
        }

        return card;
    }
}

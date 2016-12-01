package com.dbottillo.mtgsearchfree.model.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.VisibleForTesting;

import com.dbottillo.mtgsearchfree.model.CardProperties;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.util.LOG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public final class CardDataSource {

    public static final int LIMIT = 400;
    public static final String TABLE = "MTGCard";

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

        public String getType() {
            return type;
        }
    }

    private CardDataSource() {
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

    public static String generateCreateTable() {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        builder.append(TABLE).append(" (_id INTEGER PRIMARY KEY, ");
        for (COLUMNS column : COLUMNS.values()) {
            builder.append(column.name).append(' ').append(column.type);
            if (column != COLUMNS.NUMBER) {
                builder.append(',');
            }
        }
        return builder.append(')').toString();
    }

    @VisibleForTesting
    public static String generateCreateTable(int version) {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        builder.append(TABLE).append(" (_id INTEGER PRIMARY KEY, ");
        COLUMNS lastColumn = COLUMNS.NUMBER;
        if (version < 2) {
            lastColumn = COLUMNS.SET_NAME;
        } else if (version < 3) {
            lastColumn = COLUMNS.LAYOUT;
        }
        for (COLUMNS column : COLUMNS.values()) {
            boolean addColumn = true;
            if ((column == COLUMNS.RULINGS || column == COLUMNS.LAYOUT) && version <= 1) {
                addColumn = false;
            } else if ((column == COLUMNS.NUMBER || column == COLUMNS.SET_CODE) && version <= 2) {
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

    @VisibleForTesting
    static String generateCreateTableWithoutLayout() {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        builder.append(TABLE).append(" (_id INTEGER PRIMARY KEY, ");
        for (COLUMNS column : COLUMNS.values()) {
            if (column != COLUMNS.LAYOUT) {
                builder.append(column.name).append(' ').append(column.type);
                if (column != COLUMNS.NUMBER) {
                    builder.append(',');
                }
            }
        }
        return builder.append(')').toString();
    }


    public static long saveCard(SQLiteDatabase database, MTGCard card) {
        return database.insertWithOnConflict(TABLE, null, createContentValue(card), SQLiteDatabase.CONFLICT_IGNORE);
    }

    public static ContentValues createContentValue(MTGCard card) {
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
                    LOG.e(e);
                }
            }
            values.put(COLUMNS.RULINGS.getName(), rules.toString());
        }
        values.put(COLUMNS.LAYOUT.getName(), card.getLayout());
        values.put(COLUMNS.NUMBER.getName(), card.getNumber());
        return values;
    }

    public static MTGCard fromCursor(Cursor cursor) {
        return fromCursor(cursor, true);
    }

    public static MTGCard fromCursor(Cursor cursor, boolean fullCard) {
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
            card.setManaCost(cursor.getString(cursor.getColumnIndex(COLUMNS.MANA_COST.getName())));
        }

        card.setRarity(cursor.getString(cursor.getColumnIndex(COLUMNS.RARITY.getName())));
        card.setPower(cursor.getString(cursor.getColumnIndex(COLUMNS.POWER.getName())));
        card.setToughness(cursor.getString(cursor.getColumnIndex(COLUMNS.TOUGHNESS.getName())));

        if (cursor.getColumnIndex(COLUMNS.TEXT.getName()) != -1) {
            card.setText(cursor.getString(cursor.getColumnIndex(COLUMNS.TEXT.getName())));
        }

        card.setCmc(cursor.getInt(cursor.getColumnIndex(COLUMNS.CMC.getName())));
        card.setMultiColor(cursor.getInt(cursor.getColumnIndex(COLUMNS.MULTICOLOR.getName())) == 1);
        card.setAsALand(cursor.getInt(cursor.getColumnIndex(COLUMNS.LAND.getName())) == 1);
        card.setAsArtifact(cursor.getInt(cursor.getColumnIndex(COLUMNS.ARTIFACT.getName())) == 1);

        String rulings = cursor.getString(cursor.getColumnIndex(COLUMNS.RULINGS.getName()));
        if (rulings != null) {
            try {
                JSONArray jsonArray = new JSONArray(rulings);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject rule = jsonArray.getJSONObject(i);
                    card.addRuling(rule.getString("text"));
                }
            } catch (JSONException e) {
                LOG.e(e);
            }
        }

        if (cursor.getColumnIndex(COLUMNS.LAYOUT.getName()) != -1) {
            card.setLayout(cursor.getString(cursor.getColumnIndex(COLUMNS.LAYOUT.getName())));
        }
        if (cursor.getColumnIndex(COLUMNS.NUMBER.getName()) != -1) {
            card.setNumber(cursor.getString(cursor.getColumnIndex(COLUMNS.NUMBER.getName())));
        }
        return card;
    }
}

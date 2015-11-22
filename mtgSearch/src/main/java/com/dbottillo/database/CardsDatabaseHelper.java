package com.dbottillo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.BuildConfig;
import com.dbottillo.database.CardContract.CardEntry;
import com.dbottillo.database.SetContract.SetEntry;
import com.dbottillo.search.SearchParams;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class CardsDatabaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "mtgsearch.db";
    public static final int LIMIT = 400;

    public CardsDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public CardsDatabaseHelper(Context context) {
        this(context, DATABASE_NAME, null, BuildConfig.DATABASE_VERSION);
        setForcedUpgrade();
    }

    public Cursor getSets() {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + SetEntry.TABLE_NAME;

        return db.rawQuery(query, null);
    }

    public Cursor getSet(String idSet) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + CardEntry.TABLE_NAME + " WHERE " + CardEntry.COLUMN_NAME_SET_ID + " = ?";

        //Log.d("MTG", "query: "+query+" with id: "+idSet);

        return db.rawQuery(query, new String[]{idSet});
    }

    public Cursor searchCards(SearchParams searchParams) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + CardEntry.TABLE_NAME + " WHERE ";
        ArrayList<String> selection = new ArrayList<>();

        boolean first = true;
        if (searchParams.getName().length() > 0) {
            query += composeQuery(true, CardEntry.COLUMN_NAME_NAME);
            selection.add("%" + searchParams.getName() + "%");
            first = false;
        }
        if (searchParams.getTypes().length() > 0) {
            query += composeQuery(first, CardEntry.COLUMN_NAME_TYPE);
            first = false;
            selection.add("%" + searchParams.getTypes().trim() + "%");
        }
        if (searchParams.getCmc().getValue() > 0) {
            query += composeQuery(first, CardEntry.COLUMN_NAME_CMC, searchParams.getCmc().getOperator());
            first = false;
            selection.add("" + searchParams.getCmc().getValue());
        }
        if (searchParams.getPower().getValue() > 0) {
            query += composeQuery(first, CardEntry.COLUMN_NAME_POWER, searchParams.getPower().getOperator());
            first = false;
            selection.add("" + searchParams.getPower().getValue());
        }
        if (searchParams.getTough().getValue() > 0) {
            query += composeQuery(first, CardEntry.COLUMN_NAME_TOUGHNESS, searchParams.getTough().getOperator());
            first = false;
            selection.add("" + searchParams.getTough().getValue());
        }
        if (searchParams.isNomulti()) {
            if (!first) {
                query += "AND ";
            }
            first = false;
            query += CardEntry.COLUMN_NAME_MULTICOLOR + " == 0 ";
        }
        if (searchParams.onlyMulti()) {
            if (!first) {
                query += "AND ";
            }
            first = false;
            query += CardEntry.COLUMN_NAME_MULTICOLOR + " == 1 ";
        }
        if (searchParams.getSetId() > 0) {
            if (!first) {
                query += "AND ";
            }
            first = false;
            query += CardEntry.COLUMN_NAME_SET_ID + " == " + searchParams.getSetId() + " ";
        }
        if (searchParams.getSetId() == -2) {
            // special case for standard
            if (!first) {
                query += "AND ";
            }
            first = false;
            query += "(setId == 2 OR setId == 3 OR setId == 5 OR setId == 7 OR setId == 9) ";
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
                query += CardEntry.COLUMN_NAME_RARITY + "='Common' ";
                firstRarity = false;
            }
            if (searchParams.isUncommon()) {
                if (!firstRarity) {
                    query += "OR ";
                }
                query += CardEntry.COLUMN_NAME_RARITY + "='Uncommon' ";
                firstRarity = false;
            }
            if (searchParams.isRare()) {
                if (!firstRarity) {
                    query += "OR ";
                }
                query += CardEntry.COLUMN_NAME_RARITY + "='Rare' ";
                firstRarity = false;
            }
            if (searchParams.isMythic()) {
                if (!firstRarity) {
                    query += "OR ";
                }
                query += CardEntry.COLUMN_NAME_RARITY + "='Mythic Rare' ";
            }
            query += ")";
        }

        query += " ORDER BY " + CardEntry.COLUMN_NAME_MULTIVERSEID + " DESC LIMIT " + LIMIT;

        String[] sel = Arrays.copyOf(selection.toArray(), selection.size(), String[].class);

        /*Log.d("MTG", "query: " + query + " with selection: ");
        for (String str : sel) {
            Log.d("MTG", "value: " + str);
        }*/

        return db.rawQuery(query, sel);
    }

    private String composeQuery(boolean first, String column, String operator) {
        String query = "";
        if (!first) {
            query += "AND ";
        }
        query += column + " " + operator + " ? ";
        return query;
    }

    private String composeQuery(boolean first, String column) {
        return composeQuery(first, column, "LIKE");
    }

    private String composeQueryColor(boolean first, String operator) {
        String query = "";
        if (!first) {
            query += operator;
        }
        query += CardEntry.COLUMN_NAME_MANACOST + " LIKE ? ";
        return query;
    }

    public Cursor getRandomCard(int number) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + CardEntry.TABLE_NAME + " ORDER BY RANDOM() LIMIT " + number;

        //Log.e("MTG", "query: " + query);

        return db.rawQuery(query, null);
    }

}

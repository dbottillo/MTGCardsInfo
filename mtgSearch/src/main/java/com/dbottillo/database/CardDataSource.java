package com.dbottillo.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.helper.LOG;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.search.SearchParams;

import java.util.ArrayList;
import java.util.Arrays;

public final class CardDataSource {

    public static final int LIMIT = 400;

    private CardDataSource() {
    }

    public static long saveCard(SQLiteDatabase database, MTGCard card) {
        return database.insertWithOnConflict(CardContract.CardEntry.TABLE_NAME, null, card.createContentValue(), SQLiteDatabase.CONFLICT_IGNORE);
    }

    public static Cursor getSets(SQLiteDatabase db) {
        String query = "SELECT * FROM " + SetContract.SetEntry.TABLE_NAME;
        LOG.d("[getSets] query: " + query);
        return db.rawQuery(query, null);
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
}

package com.dbottillo.mtgsearchfree.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.mtgsearchfree.helper.LOG;
import com.dbottillo.mtgsearchfree.resources.MTGCard;
import com.dbottillo.mtgsearchfree.resources.MTGSet;
import com.dbottillo.mtgsearchfree.search.SearchParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MTGCardDataSource {

    public static final int LIMIT = 400;

    public static final List<String> STANDARD = Arrays.asList("Khans of Tarkir", "Fate Reforged", "Dragons of Tarkir", "Magic Origins", "Battle for Zendikar");

    private MTGCardDataSource() {
    }

    public static ArrayList<MTGCard> getSet(SQLiteDatabase db, MTGSet set) {
        String query = "SELECT * FROM " + CardDataSource.TABLE + " WHERE " + CardDataSource.COLUMNS.SET_CODE.getName() + " = '" + set.getCode() + "';";
        LOG.d("[getSet] query: " + query + " with code: " + set.getCode());

        ArrayList<MTGCard> cards = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                MTGCard card = CardDataSource.fromCursor(cursor);
                card.setSetCode(set.getCode());
                card.setSetName(set.getName());
                card.setIdSet(set.getId());
                cards.add(card);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return cards;
    }

    public static ArrayList<MTGCard> searchCards(SQLiteDatabase db, SearchParams searchParams) {
        ArrayList<MTGCard> cards = new ArrayList<>();
        String query = "SELECT * FROM " + CardDataSource.TABLE + " WHERE ";
        ArrayList<String> selection = new ArrayList<>();

        boolean first = true;
        if (searchParams.getName().length() > 0) {
            query += composeQuery(true, CardDataSource.COLUMNS.NAME.getName());
            selection.add("%" + searchParams.getName().toLowerCase() + "%");
            first = false;
        }
        if (searchParams.getTypes().length() > 0) {
            String[] types = searchParams.getTypes().split(" ");
            if (types.length > 1) {
                if (!first) {
                    query += "AND ";
                }
                query += "(";
                first = true;
                for (String type : types) {
                    query += composeQuery(first, CardDataSource.COLUMNS.TYPE.getName());
                    first = false;
                    selection.add("%" + type.trim() + "%");
                }
                query += ")";
            } else {
                query += composeQuery(first, CardDataSource.COLUMNS.TYPE.getName());
                first = false;
                selection.add("%" + searchParams.getTypes().toLowerCase() + "%");
            }
        }
        if (searchParams.getText().length() > 0) {
            query += composeQuery(first, CardDataSource.COLUMNS.TEXT.getName());
            first = false;
            selection.add("%" + searchParams.getText().trim() + "%");
        }
        if (searchParams.getCmc() != null && searchParams.getCmc().getValue() > 0) {
            query += composeQuery(first, CardDataSource.COLUMNS.CMC.getName(), searchParams.getCmc().getOperator());
            first = false;
            selection.add("" + searchParams.getCmc().getValue());
        }
        if (searchParams.getPower() != null && searchParams.getPower().getValue() > 0) {
            query += composeQueryForInt(first, CardDataSource.COLUMNS.POWER.getName(), searchParams.getPower().getOperator());
            first = false;
            selection.add("" + searchParams.getPower().getValue());
        }
        if (searchParams.getTough() != null && searchParams.getTough().getValue() > 0) {
            query += composeQueryForInt(first, CardDataSource.COLUMNS.TOUGHNESS.getName(), searchParams.getTough().getOperator());
            first = false;
            selection.add("" + searchParams.getTough().getValue());
        }
        if (searchParams.isNomulti()) {
            if (!first) {
                query += "AND ";
            }
            first = false;
            query += CardDataSource.COLUMNS.MULTICOLOR.getName() + " == 0 ";
        }
        if (searchParams.onlyMulti()) {
            if (!first) {
                query += "AND ";
            }
            first = false;
            query += CardDataSource.COLUMNS.MULTICOLOR.getName() + " == 1 ";
        }
        if (searchParams.getSetId() > 0) {
            if (!first) {
                query += "AND ";
            }
            first = false;
            query += CardDataSource.COLUMNS.SET_ID.getName() + " == " + searchParams.getSetId() + " ";
        }
        if (searchParams.getSetId() == -2) {
            // special case for standard
            if (!first) {
                query += "AND ";
            }
            first = false;
            query += "(setId == 4 OR setId == 5 OR setId == 7 OR setId == 9 OR setId == 11) ";
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
                query += CardDataSource.COLUMNS.RARITY.getName() + "='Common' ";
                firstRarity = false;
            }
            if (searchParams.isUncommon()) {
                if (!firstRarity) {
                    query += "OR ";
                }
                query += CardDataSource.COLUMNS.RARITY.getName() + "='Uncommon' ";
                firstRarity = false;
            }
            if (searchParams.isRare()) {
                if (!firstRarity) {
                    query += "OR ";
                }
                query += CardDataSource.COLUMNS.RARITY.getName() + "='Rare' ";
                firstRarity = false;
            }
            if (searchParams.isMythic()) {
                if (!firstRarity) {
                    query += "OR ";
                }
                query += CardDataSource.COLUMNS.RARITY.getName() + "='Mythic Rare' ";
            }
            query += ")";
        }

        query += " ORDER BY " + CardDataSource.COLUMNS.MULTIVERSE_ID.getName() + " DESC LIMIT " + LIMIT;

        String[] sel = Arrays.copyOf(selection.toArray(), selection.size(), String[].class);

        LOG.d("[searchCards] query: " + query + " with selection: " + selection);

        Cursor cursor = db.rawQuery(query, sel);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                MTGCard card = CardDataSource.fromCursor(cursor);
                cards.add(card);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return cards;
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

    private static String composeQueryForInt(boolean first, String column, String operator) {
        //power != "" AND  CAST(power as integer)<1
        String query = "";
        if (!first) {
            query += "AND ";
        }
        query += "(";
        query += column + " != '' AND CAST(" + column + " as integer)" + operator + " ? ";
        query += ")";
        return query;
    }

    private static String composeQueryColor(boolean first, String operator) {
        String query = "";
        if (!first) {
            query += operator;
        }
        query += CardDataSource.COLUMNS.MANA_COST.getName() + " LIKE ? ";
        return query;
    }

    public static ArrayList<MTGCard> getRandomCard(SQLiteDatabase db, int number) {
        String query = "SELECT * FROM " + CardDataSource.TABLE + " ORDER BY RANDOM() LIMIT " + number;
        LOG.d("[getRandomCard] query: " + query);
        ArrayList<MTGCard> cards = new ArrayList<>(number);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                cards.add(CardDataSource.fromCursor(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return cards;
    }


}

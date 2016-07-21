package com.dbottillo.mtgsearchfree.model.database;

import android.database.Cursor;

import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MTGCardDataSource {

    private static final int LIMIT = 400;

    static final List<String> STANDARD = Arrays.asList("Dragons of Tarkir", "Magic Origins",
            "Battle for Zendikar", "Oath of the Gatewatch",
            "Shadows over Innistrad", "Eldritch Moon");

    private MTGDatabaseHelper mtgHelper;

    public MTGCardDataSource(MTGDatabaseHelper helper) {
        this.mtgHelper = helper;
    }

    public List<MTGCard> getSet(MTGSet set) {
        LOG.d("get set  " + set.toString());
        String query = "SELECT * FROM " + CardDataSource.TABLE + " WHERE " + CardDataSource.COLUMNS.SET_CODE.getName() + " = '" + set.getCode() + "';";
        LOG.query(query, set.getCode());

        ArrayList<MTGCard> cards = new ArrayList<>();
        Cursor cursor = mtgHelper.getReadableDatabase().rawQuery(query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                MTGCard card = CardDataSource.fromCursor(cursor);
                card.belongsTo(set);
                cards.add(card);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return cards;
    }

    public List<MTGCard> searchCards(SearchParams searchParams) {
        LOG.d("search cards  " + searchParams.toString());
        ArrayList<MTGCard> cards = new ArrayList<>();
        String query = "SELECT * FROM " + CardDataSource.TABLE + " WHERE ";
        ArrayList<String> selection = new ArrayList<>();

        boolean first = true;
        if (searchParams.getName().length() > 0) {
            query += composeQuery(true, CardDataSource.COLUMNS.NAME.getName());
            selection.add("%" + searchParams.getName().toLowerCase(Locale.getDefault()) + "%");
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
                selection.add("%" + searchParams.getTypes().toLowerCase(Locale.getDefault()) + "%");
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
        if (searchParams.isNoMulti()) {
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
            query += "(setId==1 OR setId == 3 OR setId == 5 OR setId == 8 OR setId == 10 OR setId == 12) ";
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

        LOG.query(query, sel);

        Cursor cursor = mtgHelper.getReadableDatabase().rawQuery(query, sel);
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

    private String composeQueryForInt(boolean first, String column, String operator) {
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

    private String composeQueryColor(boolean first, String operator) {
        String query = "";
        if (!first) {
            query += operator;
        }
        query += CardDataSource.COLUMNS.MANA_COST.getName() + " LIKE ? ";
        return query;
    }

    public List<MTGCard> getRandomCard(int number) {
        LOG.d("get random card  " + number);
        String query = "SELECT * FROM " + CardDataSource.TABLE + " ORDER BY RANDOM() LIMIT " + number;
        LOG.query(query);
        ArrayList<MTGCard> cards = new ArrayList<>(number);
        Cursor cursor = mtgHelper.getReadableDatabase().rawQuery(query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                cards.add(CardDataSource.fromCursor(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return cards;
    }

    public MTGCard searchCard(String name) {
        LOG.d("search card <" + name + ">");
        String query = "SELECT * FROM " + CardDataSource.TABLE + " WHERE "
                + CardDataSource.COLUMNS.NAME.getName() + "=?";
        String[] selection = new String[]{name};
        LOG.query(query);
        Cursor cursor = mtgHelper.getReadableDatabase().rawQuery(query, selection);
        MTGCard card = null;
        if (cursor.moveToFirst()) {
            card = CardDataSource.fromCursor(cursor);
        }
        cursor.close();
        return card;
    }
}

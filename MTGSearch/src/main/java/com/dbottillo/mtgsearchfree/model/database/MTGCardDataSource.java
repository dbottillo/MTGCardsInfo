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

    enum STANDARD {
        DRAGONS_TARKIR(12, "Dragons of Tarkir"),
        MAGIC_ORIGINS(10, "Magic Origins"),
        BATTLE_ZENDIKAR(8, "Battle for Zendikar"),
        OATH_GATEWATCH(5, "Oath of the Gatewatch"),
        SHADOWS_OVER_INNISTRAD(3, "Shadows over Innistrad"),
        ELDRITCH_MOON(1, "Eldritch Moon");

        public int setId;
        public String name;

        STANDARD(int setId, String name) {
            this.setId = setId;
            this.name = name;
        }

        public static String[] getSetIds() {
            String[] ids = new String[STANDARD.values().length];
            for (int i = 0; i < STANDARD.values().length; i++) {
                ids[i] = String.valueOf(STANDARD.values()[i].setId);
            }
            return ids;
        }

        public static List<String> getSetNames() {
            ArrayList<String> sets = new ArrayList<>();
            for (STANDARD standard : STANDARD.values()) {
                sets.add(standard.name);
            }
            return sets;
        }
    }

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
        QueryComposer queryComposer = new QueryComposer("SELECT * FROM " + CardDataSource.TABLE);
        queryComposer.addLikeParam(CardDataSource.COLUMNS.NAME.getName(), searchParams.getName().toLowerCase(Locale.getDefault()));
        if (searchParams.getTypes().length() > 0) {
            String[] types = searchParams.getTypes().split(" ");
            queryComposer.addMultipleParam(CardDataSource.COLUMNS.TYPE.getName(), "LIKE", "AND", types);
        }
        queryComposer.addLikeParam(CardDataSource.COLUMNS.TEXT.getName(), searchParams.getText().trim());
        queryComposer.addParam(CardDataSource.COLUMNS.CMC.getName(), searchParams.getCmc());
        queryComposer.addParam(CardDataSource.COLUMNS.POWER.getName(), searchParams.getPower());
        queryComposer.addParam(CardDataSource.COLUMNS.TOUGHNESS.getName(), searchParams.getTough());
        String colorsOperator = "AND";
        if (searchParams.isNoMulti()) {
            queryComposer.addParam(CardDataSource.COLUMNS.MULTICOLOR.getName(), "==", "0");
            colorsOperator = "OR";
        }
        if (searchParams.onlyMulti()) {
            queryComposer.addParam(CardDataSource.COLUMNS.MULTICOLOR.getName(), "==", "1");
        }
        if (searchParams.getSetId() > 0) {
            queryComposer.addParam(CardDataSource.COLUMNS.SET_ID.getName(), "==", searchParams.getSetId());
        }
        if (searchParams.getSetId() == -2) {
            queryComposer.addMultipleParam(CardDataSource.COLUMNS.SET_ID.getName(), "==", "OR", STANDARD.getSetIds());
        }
        if (searchParams.atLeastOneColor()) {
            List<String> colors = new ArrayList<>();
            if (searchParams.isWhite()) {
                colors.add("W");
            }
            if (searchParams.isBlue()) {
                colors.add("U");
            }
            if (searchParams.isBlack()) {
                colors.add("B");
            }
            if (searchParams.isRed()) {
                colors.add("R");
            }
            if (searchParams.isGreen()) {
                colors.add("G");
            }
            queryComposer.addMultipleParam(CardDataSource.COLUMNS.MANA_COST.getName(), "LIKE", colorsOperator, Arrays.copyOf(colors.toArray(), colors.size(), String[].class));
        }
        if (searchParams.atLeastOneRarity()) {
            List<String> rarities = new ArrayList<>();
            if (searchParams.isCommon()) {
                rarities.add("Common");
            }
            if (searchParams.isUncommon()) {
                rarities.add("Uncommon");
            }
            if (searchParams.isRare()) {
                rarities.add("Rare");
            }
            if (searchParams.isMythic()) {
                rarities.add("Mythic Rare");
            }
            queryComposer.addMultipleParam(CardDataSource.COLUMNS.RARITY.getName(), "==", "OR", Arrays.copyOf(rarities.toArray(), rarities.size(), String[].class));
        }
        if (searchParams.isLand()) {
            queryComposer.addParam(CardDataSource.COLUMNS.LAND.getName(), "==", 1);
        }
        queryComposer.append("ORDER BY " + CardDataSource.COLUMNS.MULTIVERSE_ID.getName() + " DESC LIMIT " + LIMIT);

        QueryComposer.Output output = queryComposer.build();
        String[] sel = Arrays.copyOf(output.selection.toArray(), output.selection.size(), String[].class);
        LOG.query(output.query, sel);

        Cursor cursor = mtgHelper.getReadableDatabase().rawQuery(output.query, sel);

        ArrayList<MTGCard> cards = new ArrayList<>();
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

    MTGCard searchCard(String name) {
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

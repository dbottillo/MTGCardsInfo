package com.dbottillo.mtgsearchfree.model.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
        HOUR_OF_DEVASTATION(2, "Hour of Devastation"),
        AMONKHET_INVOCATIONS(3, "Masterpiece Series: Amonkhet Invocations"),
        AMONKHET(4, "Amonkhet"),
        AETHER_REVOLT(8, "Aether Revolt"),
        KALADESH_INVENTIONS(11, "Kaladesh Inventions"),
        KALADESH(12, "Kaladesh"),
        ELDRITCH_MOON(15, "Eldritch Moon"),
        SHADOWS_OVER_INNISTRAD(18, "Shadows over Innistrad"),
        OATH_GATEWATCH(20, "Oath of the Gatewatch"),
        BATTLE_ZENDIKAR(23, "Battle for Zendikar");

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

    private SQLiteDatabase database;
    private CardDataSource cardDataSource;

    public MTGCardDataSource(SQLiteDatabase database, CardDataSource cardDataSource) {
        this.database = database;
        this.cardDataSource = cardDataSource;
    }

    public List<MTGCard> getSet(MTGSet set) {
        LOG.d("get set  " + set.toString());
        String query = "SELECT * FROM " + CardDataSource.TABLE + " WHERE " + CardDataSource.COLUMNS.SET_CODE.getName() + " = '" + set.getCode() + "';";
        LOG.query(query, set.getCode());

        ArrayList<MTGCard> cards = new ArrayList<>();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                MTGCard card = cardDataSource.fromCursor(cursor);
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
        queryComposer.addLikeParam(CardDataSource.COLUMNS.NAME.getName(), searchParams.getName().trim().toLowerCase(Locale.getDefault()));
        if (searchParams.getTypes().length() > 0) {
            String[] types = searchParams.getTypes().split(" ");
            queryComposer.addMultipleParam(CardDataSource.COLUMNS.TYPE.getName(), "LIKE", "AND", types);
        }
        queryComposer.addLikeParam(CardDataSource.COLUMNS.TEXT.getName(), searchParams.getText().trim());
        queryComposer.addParam(CardDataSource.COLUMNS.CMC.getName(), searchParams.getCmc());
        queryComposer.addParam(CardDataSource.COLUMNS.POWER.getName(), searchParams.getPower());
        queryComposer.addParam(CardDataSource.COLUMNS.TOUGHNESS.getName(), searchParams.getTough());
        String colorsOperator = "OR";
        if (searchParams.isNoMulti()) {
            queryComposer.addParam(CardDataSource.COLUMNS.MULTICOLOR.getName(), "==", "0");
            //colorsOperator = "OR";
        }
        if (searchParams.onlyMulti() || searchParams.isOnlyMultiNoOthers()) {
            queryComposer.addParam(CardDataSource.COLUMNS.MULTICOLOR.getName(), "==", "1");
        }
        if (searchParams.isOnlyMultiNoOthers()){
            colorsOperator = "AND";
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

        Cursor cursor = database.rawQuery(output.query, sel);

        ArrayList<MTGCard> cards = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                MTGCard card = cardDataSource.fromCursor(cursor);
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
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                cards.add(cardDataSource.fromCursor(cursor));
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
        Cursor cursor = database.rawQuery(query, selection);
        MTGCard card = null;
        if (cursor.moveToFirst()) {
            card = cardDataSource.fromCursor(cursor);
        }
        cursor.close();
        return card;
    }

    public MTGCard searchCard(int multiverseid) {
        LOG.d("search card <" + multiverseid + ">");
        String query = "SELECT * FROM " + CardDataSource.TABLE + " WHERE "
                + CardDataSource.COLUMNS.MULTIVERSE_ID.getName() + "=?";
        String[] selection = new String[]{String.valueOf(multiverseid)};
        LOG.query(query);
        Cursor cursor = database.rawQuery(query, selection);
        MTGCard card = null;
        if (cursor.moveToFirst()) {
            card = cardDataSource.fromCursor(cursor);
        }
        cursor.close();
        return card;
    }
}

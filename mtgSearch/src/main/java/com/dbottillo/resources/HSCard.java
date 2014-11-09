package com.dbottillo.resources;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;

import com.dbottillo.database.HSCardContract.HSCardEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HSCard extends GameCard implements Comparable<HSCard> {

    int id;
    String name;
    String type;
    int cost;
    String rarity;
    String faction;
    String text;
    ArrayList<String> mechanics;
    String attack;
    String health;
    boolean collectible;
    boolean elite;
    int idSet;
    String setName;
    String hearthstoneId;

    /*
    {"id":"XXX_048","name":"-1 Durability","type":"Spell","rarity":"Common","cost":0,"text":"Give a player's weapon -1 Durability."}
     */

    public HSCard() {
        this.mechanics = new ArrayList<String>();
    }

    public HSCard(int id) {
        this();
        this.id = id;
    }

    public HSCard(Parcel in) {
        this();
        readFromParcel(in);
    }

    public static ContentValues createContentValueFromJSON(JSONObject jsonObject, long setId, String setName) throws JSONException {
        ContentValues values = new ContentValues();

        values.put(HSCardEntry.COLUMN_NAME_NAME, jsonObject.getString("name"));
        values.put(HSCardEntry.COLUMN_NAME_TYPE, jsonObject.getString("type"));

        String cost = "";
        if (jsonObject.has("cost")) {
            cost = jsonObject.getString("cost");
        }
        values.put(HSCardEntry.COLUMN_NAME_COST, cost);

        String rarity = "";
        if (jsonObject.has("rarity")) {
            rarity = jsonObject.getString("rarity");
        }
        values.put(HSCardEntry.COLUMN_NAME_RARITY, rarity);

        String text = "";
        if (jsonObject.has("text")) {
            text = jsonObject.getString("text");
        }
        values.put(HSCardEntry.COLUMN_NAME_TEXT, text);

        String attack = "";
        if (jsonObject.has("attack")) {
            attack = jsonObject.getString("attack");
        }
        values.put(HSCardEntry.COLUMN_NAME_ATTACK, attack);

        String health = "";
        if (jsonObject.has("health")) {
            health = jsonObject.getString("health");
        }
        values.put(HSCardEntry.COLUMN_NAME_HEALTH, health);

        boolean collectible = false;
        if (jsonObject.has("collectible")) {
            collectible = jsonObject.getBoolean("collectible");
        }

        boolean elite = false;
        if (jsonObject.has("elite")) {
            elite = jsonObject.getBoolean("elite");
        }

        values.put(HSCardEntry.COLUMN_NAME_COLLECTIBLE, collectible);
        values.put(HSCardEntry.COLUMN_NAME_ELITE, elite);

        values.put(HSCardEntry.COLUMN_NAME_SET_ID, setId);
        values.put(HSCardEntry.COLUMN_NAME_SET_NAME, setName);

        if (jsonObject.has("id")) {
            values.put(HSCardEntry.COLUMN_NAME_HEARTHSTONE_ID, jsonObject.getString("id"));
        }

        return values;
    }

    public static HSCard createCardFromCursor(Cursor cursor) {
        HSCard card = new HSCard(cursor.getInt(cursor.getColumnIndex(HSCardEntry._ID)));
        card.setType(cursor.getString(cursor.getColumnIndex(HSCardEntry.COLUMN_NAME_TYPE)));
        card.setName(cursor.getString(cursor.getColumnIndex(HSCardEntry.COLUMN_NAME_NAME)));
        card.setText(cursor.getString(cursor.getColumnIndex(HSCardEntry.COLUMN_NAME_TEXT)));

        if (cursor.getColumnIndex(HSCardEntry.COLUMN_NAME_COST) != -1) {
            card.setCost(cursor.getInt(cursor.getColumnIndex(HSCardEntry.COLUMN_NAME_COST)));
        }

        card.setRarity(cursor.getString(cursor.getColumnIndex(HSCardEntry.COLUMN_NAME_RARITY)));
        card.setAttack(cursor.getString(cursor.getColumnIndex(HSCardEntry.COLUMN_NAME_ATTACK)));
        card.setHealth(cursor.getString(cursor.getColumnIndex(HSCardEntry.COLUMN_NAME_HEALTH)));

        card.setCollectible(cursor.getInt(cursor.getColumnIndex(HSCardEntry.COLUMN_NAME_COLLECTIBLE)) == 1);
        card.setElite(cursor.getInt(cursor.getColumnIndex(HSCardEntry.COLUMN_NAME_ELITE)) == 1);

        card.setIdSet(cursor.getInt(cursor.getColumnIndex(HSCardEntry.COLUMN_NAME_SET_ID)));
        card.setSetName(cursor.getString(cursor.getColumnIndex(HSCardEntry.COLUMN_NAME_SET_NAME)));

        card.setSetName(cursor.getString(cursor.getColumnIndex(HSCardEntry.COLUMN_NAME_SET_NAME)));

        if (cursor.getColumnIndex(HSCardEntry.COLUMN_NAME_HEARTHSTONE_ID) != -1) {
            card.setHearthstoneId(cursor.getString(cursor.getColumnIndex(HSCardEntry.COLUMN_NAME_HEARTHSTONE_ID)));
        }

        return card;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeInt(cost);
        dest.writeString(rarity);
        dest.writeString(faction);
        dest.writeString(text);
        dest.writeStringList(mechanics);
        dest.writeString(attack);
        dest.writeString(health);
        dest.writeInt(collectible ? 0 : 1);
        dest.writeInt(elite ? 0 : 1);
        dest.writeInt(idSet);
        dest.writeString(setName);
        dest.writeString(hearthstoneId);
    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        type = in.readString();
        cost = in.readInt();
        rarity = in.readString();
        faction = in.readString();
        text = in.readString();
        in.readStringList(mechanics);
        attack = in.readString();
        health = in.readString();
        collectible = in.readInt() == 1;
        elite = in.readInt() == 1;
        idSet = in.readInt();
        setName = in.readString();
        hearthstoneId = in.readString();
    }

    public static final Creator<HSCard> CREATOR = new Creator<HSCard>() {
        @Override
        public HSCard createFromParcel(Parcel source) {
            return new HSCard(source);
        }

        @Override
        public HSCard[] newArray(int size) {
            return new HSCard[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getRarity() {
        return rarity;
    }

    @Override
    public String getManaCost() {
        return cost + "";
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public int getIdSet() {
        return idSet;
    }

    public void setIdSet(int idSet) {
        this.idSet = idSet;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getFaction() {
        if (faction == null) {
            return "-";
        }
        return faction;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public String getMechanics() {
        String res = "";
        for (String mechanic : mechanics) {
            res += mechanic + " ";
        }
        return res;
    }

    public void setMechanics(ArrayList<String> mechanics) {
        this.mechanics = mechanics;
    }

    public String getAttack() {
        return attack;
    }

    public void setAttack(String attack) {
        this.attack = attack;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public boolean isCollectible() {
        return collectible;
    }

    public void setCollectible(boolean collectible) {
        this.collectible = collectible;
    }

    public boolean isElite() {
        return elite;
    }

    public void setElite(boolean elite) {
        this.elite = elite;
    }

    public String getHearthstoneId() {
        return hearthstoneId;
    }

    public void setHearthstoneId(String hearthstoneId) {
        this.hearthstoneId = hearthstoneId;
    }

    public String toString() {
        return "[HSCard] " + name;
    }

    @Override
    public String getImage() {
        if (getHearthstoneId() != null) {
            return "http://wow.zamimg.com/images/hearthstone/cards/enus/original/" + getHearthstoneId() + ".png";
        }
        return null;
    }

    @Override
    public int compareTo(HSCard card) {
        /*if (isALand && card.isALand) return 0;
        if (!isALand && card.isALand) return -1;
        if (isALand) return 1;
        if (isAnArtifact && card.isAnArtifact) return 0;
        if (!isAnArtifact && card.isAnArtifact) return -1;
        if (isAnArtifact) return 1;
        if (isMultiColor && card.isMultiColor) return 0;
        if (!isMultiColor && card.isMultiColor) return -1;
        if (isMultiColor) return 1;

        if (card.getSingleColor() == this.getSingleColor()) return 0;
        if (getSingleColor() < card.getSingleColor()) return -1;
*/
        return 1;
    }


}

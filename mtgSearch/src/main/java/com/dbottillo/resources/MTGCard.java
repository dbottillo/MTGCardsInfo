package com.dbottillo.resources;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.dbottillo.database.CardContract.*;
import com.dbottillo.database.SetContract.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;

public class MTGCard extends GameCard implements Comparable<MTGCard> {

    int id;
    String name;
    String type;
    ArrayList<String> types;
    ArrayList<String> subTypes;
    ArrayList<Integer> colors;
    int cmc;
    String rarity;
    String power;
    String toughness;
    String manaCost;
    String text;
    boolean isMultiColor;
    boolean isALand;
    boolean isAnArtifact;
    boolean isAnEldrazi;
    int multiVerseId;
    int idSet;
    String setName;

    public static final int WHITE = 0;
    public static final int BLUE = 1;
    public static final int BLACK = 2;
    public static final int RED = 3;
    public static final int GREEN = 4;

    public static final int CONDITIONAL_POWER = -2;
    public static final int ONE_PLUS_POWER = -3;

    /*{"layout":"normal","type":"Creature â€” Elemental","types":["Creature"],"colors":["Blue"],"multiverseid":94,
            "name":"Air Elemental","subtypes":["Elemental"],"cmc":5,"rarity":"Uncommon","artist":"Richard Thomas","power":"4",
            "toughness":"4","manaCost":"{3}{U}{U}","text":"Flying",
            "flavor":"These spirits of the air are winsome and wild and cannot be truly contained. Only marginally intelligent" +
            ", they often substitute whimsy for strategy, delighting in mischief and mayhem.","imageName":"air elemental"},
    */

    public MTGCard(){
        this.colors = new ArrayList<Integer>();
        this.types = new ArrayList<String>();
        this.subTypes = new ArrayList<String>();
    }

    public MTGCard(int id){
        this();
        this.id = id;
    }

    public MTGCard(Parcel in){
        this();
        readFromParcel(in);
    }

    public static ContentValues createContentValueFromJSON(JSONObject jsonObject, long setId, String setName) throws JSONException{
        ContentValues values = new ContentValues();

        boolean isASplit = false;
        if (jsonObject.getString("layout").equalsIgnoreCase("split")) isASplit = true;

        if (!isASplit) {
            values.put(CardEntry.COLUMN_NAME_NAME, jsonObject.getString("name"));
        }else{
            JSONArray namesJ = jsonObject.getJSONArray("names");
            String names = "";
            for (int k =0; k<namesJ.length(); k++){
                String name = namesJ.getString(k);
                names += name;
                if (k < namesJ.length()-1){
                    names +="/";
                }
            }
            values.put(CardEntry.COLUMN_NAME_NAME, names);
        }
        values.put(CardEntry.COLUMN_NAME_TYPE, jsonObject.getString("type"));

        values.put(CardEntry.COLUMN_NAME_SET_ID, setId);
        values.put(CardEntry.COLUMN_NAME_SET_NAME, setName);

        int multicolor = 0;
        int land = 0;
        int artifact = 0;

        if (jsonObject.has("colors")){
            JSONArray colorsJ = jsonObject.getJSONArray("colors");
            String colors = "";
            for (int k =0; k<colorsJ.length(); k++){
                String color = colorsJ.getString(k);
                colors += color;
                if (k < colorsJ.length()-1){
                    colors +=",";
                }
            }
            values.put(CardEntry.COLUMN_NAME_COLORS, colors);

            if (colorsJ.length()>1){
                multicolor = 1;
            }else{
                multicolor = 0;
            }
            land = 0;
        }else{
            multicolor = 0;
            land = 1;
        }

        if (jsonObject.has("types")){
            JSONArray typesJ = jsonObject.getJSONArray("types");
            String types = "";
            for (int k =0; k<typesJ.length(); k++){
                types += typesJ.getString(k);
                if (k < typesJ.length()-1){
                    types +=",";
                }
            }
            values.put(CardEntry.COLUMN_NAME_TYPES, types);
        }

        if (jsonObject.getString("type").contains("Artifact")){
            artifact = 1;
        }else{
            artifact = 0;
        }

        if (jsonObject.has("manaCost")){
            values.put(CardEntry.COLUMN_NAME_MANACOST, jsonObject.getString("manaCost"));
            land = 0;
        }
        values.put(CardEntry.COLUMN_NAME_RARITY, jsonObject.getString("rarity"));

        if (jsonObject.has("multiverseid")){
            values.put(CardEntry.COLUMN_NAME_MULTIVERSEID, jsonObject.getInt("multiverseid"));
        }

        String power = "";
        if (jsonObject.has("power")){
            power = jsonObject.getString("power");
        }
        values.put(CardEntry.COLUMN_NAME_POWER, power);

        String toughness = "";
        if (jsonObject.has("toughness")){
            toughness = jsonObject.getString("toughness");
        }
        values.put(CardEntry.COLUMN_NAME_TOUGHNESS, toughness);

        if (!isASplit && jsonObject.has("text")){
            values.put(CardEntry.COLUMN_NAME_TEXT, jsonObject.getString("text"));
        }

        if (isASplit && jsonObject.has("originalText")){
            values.put(CardEntry.COLUMN_NAME_TEXT, jsonObject.getString("originalText"));
        }

        int cmc = -1;
        if (jsonObject.has("cmc")){
            cmc = jsonObject.getInt("cmc");
        }
        values.put(CardEntry.COLUMN_NAME_CMC, cmc);

        values.put(CardEntry.COLUMN_NAME_MULTICOLOR, multicolor);
        values.put(CardEntry.COLUMN_NAME_LAND, land);
        values.put(CardEntry.COLUMN_NAME_ARTIFACT, artifact);

        return values;
    }

    public static MTGCard createCardFromCursor(Cursor cursor) {
        MTGCard card = new MTGCard(cursor.getInt(cursor.getColumnIndex(CardEntry._ID)));
        card.setType(cursor.getString(cursor.getColumnIndex(CardEntry.COLUMN_NAME_TYPE)));
        card.setName(cursor.getString(cursor.getColumnIndex(CardEntry.COLUMN_NAME_NAME)));

        card.setIdSet(cursor.getInt(cursor.getColumnIndex(CardEntry.COLUMN_NAME_SET_ID)));
        card.setSetName(cursor.getString(cursor.getColumnIndex(CardEntry.COLUMN_NAME_SET_NAME)));

        if (cursor.getColumnIndex(CardEntry.COLUMN_NAME_COLORS) != -1){
            String colors = cursor.getString(cursor.getColumnIndex(CardEntry.COLUMN_NAME_COLORS));
            if (colors != null){
                String[] splitted = colors.split(",");
                for (int i=0; i<splitted.length; i++){
                    card.addColor(MTGCard.mapIntColor(splitted[i]));
                }
            }
        }

        if (cursor.getColumnIndex(CardEntry.COLUMN_NAME_TYPES) != -1){
            String types = cursor.getString(cursor.getColumnIndex(CardEntry.COLUMN_NAME_TYPES));
            if (types != null) {
                String[] splitted = types.split(",");
                for (int i = 0; i < splitted.length; i++) {
                    card.addType(splitted[i]);
                }
            }
        }

        if (cursor.getColumnIndex(CardEntry.COLUMN_NAME_MANACOST) != -1){
            card.setManaCost(cursor.getString(cursor.getColumnIndex(CardEntry.COLUMN_NAME_MANACOST)));
        }

        card.setRarity(cursor.getString(cursor.getColumnIndex(CardEntry.COLUMN_NAME_RARITY)));

        if (cursor.getColumnIndex(CardEntry.COLUMN_NAME_MULTIVERSEID) != -1){
            card.setMultiVerseId(cursor.getInt(cursor.getColumnIndex(CardEntry.COLUMN_NAME_MULTIVERSEID)));
        }

        card.setPower(cursor.getString(cursor.getColumnIndex(CardEntry.COLUMN_NAME_POWER)));
        card.setToughness(cursor.getString(cursor.getColumnIndex(CardEntry.COLUMN_NAME_TOUGHNESS)));

        if (cursor.getColumnIndex(CardEntry.COLUMN_NAME_TEXT) != -1){
            card.setText(cursor.getString(cursor.getColumnIndex(CardEntry.COLUMN_NAME_TEXT)));
        }

        card.setCmc(cursor.getInt(cursor.getColumnIndex(CardEntry.COLUMN_NAME_CMC)));

        card.setMultiColor(cursor.getInt(cursor.getColumnIndex(CardEntry.COLUMN_NAME_MULTICOLOR)) == 1);
        card.setAsALand(cursor.getInt(cursor.getColumnIndex(CardEntry.COLUMN_NAME_LAND)) == 1);
        card.setAsArtifact(cursor.getInt(cursor.getColumnIndex(CardEntry.COLUMN_NAME_ARTIFACT)) == 1);

        card.setAsEldrazi(false);
        if (!card.isMultiColor() && !card.isALand() && !card.isAnArtifact() && card.getColors().size() == 0){
            card.setAsEldrazi(true);
        }

        return card;
    }

    /*public static MTGCard createCardFromJson(int id, JSONObject jsonObject) throws JSONException {
        MTGCard card = new MTGCard(id);
        card.setName(jsonObject.getString("name"));
        card.setType(jsonObject.getString("type"));

        if (jsonObject.has("colors")){
            JSONArray colorsJ = jsonObject.getJSONArray("colors");
            for (int k =0; k<colorsJ.length(); k++){
                String color = colorsJ.getString(k);
                card.addColor(MTGCard.mapIntColor(color));
            }

            if (colorsJ.length()>1){
                card.setMultiColor(true);
            }else{
                card.setMultiColor(false);
            }
            card.setAsALand(false);
        }else{
            card.setMultiColor(false);
            card.setAsALand(true);
        }

        if (jsonObject.has("types")){
            JSONArray typesJ = jsonObject.getJSONArray("types");
            for (int k =0; k<typesJ.length(); k++){
                card.addType(typesJ.getString(k));
            }
        }

        if (card.getType().contains("Artifact")){
            card.setAsArtifact(true);
        }else{
            card.setAsArtifact(false);
        }

        if (jsonObject.has("manaCost")){
            card.setManaCost(jsonObject.getString("manaCost"));
            card.setAsALand(false);
        }
        card.setRarity(jsonObject.getString("rarity"));

        if (jsonObject.has("multiverseid")){
            card.setMultiVerseId(jsonObject.getInt("multiverseid"));
        }

        card.setType(jsonObject.getString("type"));

        if (jsonObject.has("power")){
            card.setPower(jsonObject.getString("power"));
        }else{
            card.setPower("");
        }
        if (jsonObject.has("toughness")){
            card.setToughness(jsonObject.getString("toughness"));
        }else{
            card.setToughness("");
        }
        if (jsonObject.has("text")){
            card.setText(jsonObject.getString("text"));
        }
        if (jsonObject.has("cmc")){
            card.setCmc(jsonObject.getInt("cmc"));
        }else{
            card.setCmc(-1);
        }

        return card;
    }*/

    private static int mapIntColor(String color){
        if (color.equalsIgnoreCase("Black")) return BLACK;
        if (color.equalsIgnoreCase("Blue")) return BLUE;
        if (color.equalsIgnoreCase("White")) return WHITE;
        if (color.equalsIgnoreCase("Red")) return RED;
        if (color.equalsIgnoreCase("Green")) return GREEN;
        return -1;
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
        dest.writeStringList(types);
        dest.writeStringList(subTypes);
        dest.writeList(colors);
        dest.writeInt(cmc);
        dest.writeString(rarity);
        dest.writeString(power);
        dest.writeString(toughness);
        dest.writeString(manaCost);
        dest.writeString(text);
        dest.writeInt(isMultiColor ? 0 : 1);
        dest.writeInt(isALand ? 0 : 1);
        dest.writeInt(isAnArtifact ? 0 : 1);
        dest.writeInt(isAnEldrazi ? 0 : 1);
        dest.writeInt(multiVerseId);
        dest.writeInt(idSet);
        dest.writeString(setName);
    }

    private void readFromParcel(Parcel in){
        id = in.readInt();
        name = in.readString();
        type = in.readString();
        in.readStringList(types);
        in.readStringList(subTypes);
        in.readList(colors, Integer.class.getClassLoader());
        cmc = in.readInt();
        rarity = in.readString();
        power = in.readString();
        toughness = in.readString();
        manaCost = in.readString();
        text = in.readString();
        isMultiColor = in.readInt() == 1;
        isALand = in.readInt() == 1;
        isAnArtifact = in.readInt() == 1;
        isAnEldrazi = in.readInt() == 1;
        multiVerseId = in.readInt();
        idSet = in.readInt();
        setName = in.readString();
    }

    public static final Parcelable.Creator<MTGCard> CREATOR = new Parcelable.Creator<MTGCard>() {
        @Override
        public MTGCard createFromParcel(Parcel source) {
            return new MTGCard(source);
        }

        @Override
        public MTGCard[] newArray(int size) {
            return new MTGCard[size];
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

    public ArrayList<String> getTypes() {
        return types;
    }

    public void addType(String type) {
        this.types.add(type);
    }

    public ArrayList<String> getSubTypes() {
        return subTypes;
    }

    public void addSubType(String subType) {
        this.subTypes.add(subType);
    }

    public ArrayList<Integer> getColors() {
        return colors;
    }

    public void addColor(int color) {
        this.colors.add(color);
    }

    public int getCmc() {
        return cmc;
    }

    public void setCmc(int cmc) {
        this.cmc = cmc;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getToughness() {
        return toughness;
    }

    public void setToughness(String toughness) {
        this.toughness = toughness;
    }

    public String getManaCost() {
        return manaCost;
    }

    public void setManaCost(String manaCost) {
        this.manaCost = manaCost;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isMultiColor() {
        return isMultiColor;
    }

    public void setMultiColor(boolean isMultiColor) {
        this.isMultiColor = isMultiColor;
    }

    public boolean isALand() {
        return isALand;
    }

    public void setAsALand(boolean isALand) {
        this.isALand = isALand;
    }

    public boolean isAnArtifact() {
        return isAnArtifact;
    }

    public void setAsArtifact(boolean isAnArtifact) {
        this.isAnArtifact = isAnArtifact;
    }

    public boolean isAnEldrazi() {
        return isAnEldrazi;
    }

    public void setAsEldrazi(boolean isAnEldrazi) {
        this.isAnEldrazi = isAnEldrazi;
    }

    public int getMultiVerseId() {
        return multiVerseId;
    }

    public void setMultiVerseId(int multiVerseId) {
        this.multiVerseId = multiVerseId;
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

    public String toString(){
        return "[MTGCard] "+name;
    }

    @Override
    public String getImage() {
        if (getMultiVerseId() > 0) {
            return "http://mtgimage.com/multiverseid/" + getMultiVerseId() + ".jpg";
        }
        return null;
    }

    @Override
    public int compareTo(MTGCard card){
        if (isALand && card.isALand) return 0;
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

        return 1;
    }

    private int getSingleColor(){
        if (isMultiColor || getColors().size() == 0) return -1;
        return getColors().get(0);
    }

}

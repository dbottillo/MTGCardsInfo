package com.dbottillo.resources;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class MTGCard extends Object implements Parcelable, Comparable<MTGCard>{

    int id;
    String name;
    String type;
    ArrayList<String> types;
    ArrayList<String> subTypes;
    ArrayList<Integer> colors;
    int cmc;
    String rarity;
    int power;
    int toughness;
    String manaCost;
    String text;
    boolean isMultiColor;
    boolean isALand;
    boolean isAnArtifact;
    int multiVerseId;

    public static final int WHITE = 0;
    public static final int BLUE = 1;
    public static final int BLACK = 2;
    public static final int RED = 3;
    public static final int GREEN = 4;

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

    public static MTGCard createCardFromJson(int id, JSONObject jsonObject) throws JSONException {
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

        JSONArray typesJ = jsonObject.getJSONArray("types");
        for (int k =0; k<typesJ.length(); k++){
            card.addType(typesJ.getString(k));
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
        card.setMultiVerseId(jsonObject.getInt("multiverseid"));

        card.setType(jsonObject.getString("type"));

        if (jsonObject.has("power")){
            card.setPower(jsonObject.getInt("power"));
        }else{
            card.setPower(-1);
        }
        if (jsonObject.has("toughness")){
            card.setToughness(jsonObject.getInt("toughness"));
        }else{
            card.setToughness(-1);
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
    }

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
        dest.writeInt(power);
        dest.writeInt(toughness);
        dest.writeString(manaCost);
        dest.writeString(text);
        dest.writeInt(isMultiColor ? 0 : 1);
        dest.writeInt(isALand ? 0 : 1);
        dest.writeInt(isAnArtifact ? 0 : 1);
        dest.writeInt(multiVerseId);
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
        power = in.readInt();
        toughness = in.readInt();
        manaCost = in.readString();
        text = in.readString();
        isMultiColor = in.readInt() == 1;
        isALand = in.readInt() == 1;
        isAnArtifact = in.readInt() == 1;
        multiVerseId = in.readInt();
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

    public int getId() {
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

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getToughness() {
        return toughness;
    }

    public void setToughness(int toughness) {
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

    public int getMultiVerseId() {
        return multiVerseId;
    }

    public void setMultiVerseId(int multiVerseId) {
        this.multiVerseId = multiVerseId;
    }

    public String toString(){
        return "[MTGCard] "+name;
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
        if (isMultiColor) return -1;
        return getColors().get(0);
    }

}

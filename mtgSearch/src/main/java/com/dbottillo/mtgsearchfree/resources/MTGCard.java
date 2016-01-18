package com.dbottillo.mtgsearchfree.resources;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.database.CardContract.CardEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MTGCard implements Comparable<MTGCard>, Parcelable {

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
    boolean isAMultiColor;
    boolean isALand;
    boolean isAnArtifact;
    boolean isAnEldrazi;
    int multiVerseId;
    int idSet;
    String setName;
    String setCode;
    int quantity = 1;
    boolean sideboard = false;
    String layout;
    String number;

    private List<String> rulings;

    public static final int WHITE = 0;
    public static final int BLUE = 1;
    public static final int BLACK = 2;
    public static final int RED = 3;
    public static final int GREEN = 4;

    public MTGCard() {
        this.colors = new ArrayList<>();
        this.types = new ArrayList<>();
        this.subTypes = new ArrayList<>();
        this.rulings = new ArrayList<>();
        this.quantity = -1;
    }

    public MTGCard(int id) {
        this();
        this.id = id;
    }

    public MTGCard(Parcel in) {
        this();
        readFromParcel(in);
    }

    public static ContentValues createContentValueFromJSON(JSONObject jsonObject, MTGSet set) throws JSONException {
        ContentValues values = new ContentValues();

        boolean isASplit = false;
        if (jsonObject.getString("layout").equalsIgnoreCase("split")) {
            isASplit = true;
        }

        if (!isASplit) {
            values.put(CardEntry.COLUMN_NAME_NAME, jsonObject.getString("name"));
        } else {
            JSONArray namesJ = jsonObject.getJSONArray("names");
            StringBuilder names = new StringBuilder();
            for (int k = 0; k < namesJ.length(); k++) {
                String name = namesJ.getString(k);
                names.append(name);
                if (k < namesJ.length() - 1) {
                    names.append('/');
                }
            }
            values.put(CardEntry.COLUMN_NAME_NAME, names.toString());
        }
        values.put(CardEntry.COLUMN_NAME_TYPE, jsonObject.getString("type"));

        values.put(CardEntry.COLUMN_NAME_SET_ID, set.id);
        values.put(CardEntry.COLUMN_NAME_SET_NAME, set.name);
        values.put(CardEntry.COLUMN_NAME_SET_CODE, set.code);

        int multicolor;
        int land;
        int artifact;

        if (jsonObject.has("colors")) {
            JSONArray colorsJ = jsonObject.getJSONArray("colors");
            StringBuilder colors = new StringBuilder();
            for (int k = 0; k < colorsJ.length(); k++) {
                String color = colorsJ.getString(k);
                colors.append(color);
                if (k < colorsJ.length() - 1) {
                    colors.append(',');
                }
            }
            values.put(CardEntry.COLUMN_NAME_COLORS, colors.toString());

            if (colorsJ.length() > 1) {
                multicolor = 1;
            } else {
                multicolor = 0;
            }
            land = 0;
        } else {
            multicolor = 0;
            land = 1;
        }

        if (jsonObject.has("types")) {
            JSONArray typesJ = jsonObject.getJSONArray("types");
            StringBuilder types = new StringBuilder();
            for (int k = 0; k < typesJ.length(); k++) {
                types.append(typesJ.getString(k));
                if (k < typesJ.length() - 1) {
                    types.append(',');
                }
            }
            values.put(CardEntry.COLUMN_NAME_TYPES, types.toString());
        }

        if (jsonObject.getString("type").contains("Artifact")) {
            artifact = 1;
        } else {
            artifact = 0;
        }

        if (jsonObject.has("manaCost")) {
            values.put(CardEntry.COLUMN_NAME_MANACOST, jsonObject.getString("manaCost"));
            land = 0;
        }
        values.put(CardEntry.COLUMN_NAME_RARITY, jsonObject.getString("rarity"));

        if (jsonObject.has("multiverseid")) {
            values.put(CardEntry.COLUMN_NAME_MULTIVERSEID, jsonObject.getInt("multiverseid"));
        }

        String power = "";
        if (jsonObject.has("power")) {
            power = jsonObject.getString("power");
        }
        values.put(CardEntry.COLUMN_NAME_POWER, power);

        String toughness = "";
        if (jsonObject.has("toughness")) {
            toughness = jsonObject.getString("toughness");
        }
        values.put(CardEntry.COLUMN_NAME_TOUGHNESS, toughness);

        if (!isASplit && jsonObject.has("text")) {
            values.put(CardEntry.COLUMN_NAME_TEXT, jsonObject.getString("text"));
        }

        if (isASplit && jsonObject.has("originalText")) {
            values.put(CardEntry.COLUMN_NAME_TEXT, jsonObject.getString("originalText"));
        }

        int cmc = -1;
        if (jsonObject.has("cmc")) {
            cmc = jsonObject.getInt("cmc");
        }
        values.put(CardEntry.COLUMN_NAME_CMC, cmc);

        values.put(CardEntry.COLUMN_NAME_MULTICOLOR, multicolor);
        values.put(CardEntry.COLUMN_NAME_LAND, land);
        values.put(CardEntry.COLUMN_NAME_ARTIFACT, artifact);

        if (jsonObject.has("rulings")) {
            JSONArray rulingsJ = jsonObject.getJSONArray("rulings");
            values.put(CardEntry.COLUMN_NAME_RULINGS, rulingsJ.toString());
        }

        if (jsonObject.has("layout")) {
            values.put(CardEntry.COLUMN_NAME_LAYOUT, jsonObject.getString("layout"));
        }

        if (jsonObject.has("number")) {
            values.put(CardEntry.COLUMN_NAME_NUMBER, jsonObject.getString("number"));
        }

        return values;
    }

    public static int mapIntColor(String color) {
        if (color.equalsIgnoreCase("Black")) {
            return BLACK;
        }
        if (color.equalsIgnoreCase("Blue")) {
            return BLUE;
        }
        if (color.equalsIgnoreCase("White")) {
            return WHITE;
        }
        if (color.equalsIgnoreCase("Red")) {
            return RED;
        }
        if (color.equalsIgnoreCase("Green")) {
            return GREEN;
        }
        return -1;
    }

    public static String mapStringColor(int color) {
        if (color == BLACK) {
            return "Black";
        }
        if (color == BLUE) {
            return "Blue";
        }
        if (color == WHITE) {
            return "White";
        }
        if (color == RED) {
            return "Red";
        }
        if (color == GREEN) {
            return "Green";
        }
        return "";
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
        dest.writeInt(isAMultiColor ? 1 : 0);
        dest.writeInt(isALand ? 1 : 0);
        dest.writeInt(isAnArtifact ? 1 : 0);
        dest.writeInt(isAnEldrazi ? 1 : 0);
        dest.writeInt(multiVerseId);
        dest.writeInt(idSet);
        dest.writeString(setName);
        dest.writeString(setCode);
        dest.writeInt(quantity);
        dest.writeInt(sideboard ? 1 : 0);
        dest.writeStringList(rulings);
        dest.writeString(layout);
        dest.writeString(number);
    }

    private void readFromParcel(Parcel in) {
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
        isAMultiColor = in.readInt() == 1;
        isALand = in.readInt() == 1;
        isAnArtifact = in.readInt() == 1;
        isAnEldrazi = in.readInt() == 1;
        multiVerseId = in.readInt();
        idSet = in.readInt();
        setName = in.readString();
        setCode = in.readString();
        quantity = in.readInt();
        sideboard = in.readInt() == 1;
        in.readStringList(rulings);
        layout = in.readString();
        number = in.readString();
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


    @VisibleForTesting
    public void setId(int id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setCardName(String name) {
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
        return isAMultiColor;
    }

    public void setMultiColor(boolean isMultiColor) {
        this.isAMultiColor = isMultiColor;
    }

    public boolean isLand() {
        return isALand;
    }

    public void setAsALand(boolean isALand) {
        this.isALand = isALand;
    }

    public boolean isArtifact() {
        return isAnArtifact;
    }

    public void setAsArtifact(boolean isAnArtifact) {
        this.isAnArtifact = isAnArtifact;
    }

    public boolean isEldrazi() {
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

    public String getSetCode() {
        return setCode;
    }

    public void setSetCode(String setCode) {
        this.setCode = setCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isSideboard() {
        return sideboard;
    }

    public void setSideboard(boolean sideboard) {
        this.sideboard = sideboard;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String toString() {
        return "[MTGCard] " + name;
    }

    public void addRuling(String rule) {
        this.rulings.add(rule);
    }

    public String getImage() {
        if (number != null && number.length() > 0) {
            return "http://magiccards.info/scans/en/" + setCode.toLowerCase() + "/" + number + ".jpg";
        }
        return getImageFromGatherer();
    }

    public String getImageFromGatherer() {
        if (getMultiVerseId() > 0) {
            return "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=" + getMultiVerseId() + "&type=card";
        }
        return null;
    }

    public List<String> getRulings() {
        return rulings;
    }

    @Override
    public int compareTo(@NonNull MTGCard card) {
        if (isALand && card.isALand) {
            return 0;
        }
        if (!isALand && card.isALand) {
            return -1;
        }
        if (isALand) {
            return 1;
        }
        if (isAnArtifact && card.isAnArtifact) {
            return 0;
        }
        if (!isAnArtifact && card.isAnArtifact) {
            return -1;
        }
        if (isAnArtifact) {
            return 1;
        }
        if (isAMultiColor && card.isAMultiColor) {
            return 0;
        }
        if (!isAMultiColor && card.isAMultiColor) {
            return -1;
        }
        if (isAMultiColor) {
            return 1;
        }

        if (card.getSingleColor() == this.getSingleColor()) {
            return 0;
        }
        if (getSingleColor() < card.getSingleColor()) {
            return -1;
        }
        return 1;
    }

    private int getSingleColor() {
        if (isAMultiColor || getColors().size() == 0) {
            return -1;
        }
        return getColors().get(0);
    }

    public int getMtgColor(Context context) {
        int mtgColor = context.getResources().getColor(R.color.mtg_other);
        if (isMultiColor()) {
            mtgColor = context.getResources().getColor(R.color.mtg_multi);
        } else if (getColors().contains(MTGCard.WHITE)) {
            mtgColor = context.getResources().getColor(R.color.mtg_white);
        } else if (getColors().contains(MTGCard.BLUE)) {
            mtgColor = context.getResources().getColor(R.color.mtg_blue);
        } else if (getColors().contains(MTGCard.BLACK)) {
            mtgColor = context.getResources().getColor(R.color.mtg_black);
        } else if (getColors().contains(MTGCard.RED)) {
            mtgColor = context.getResources().getColor(R.color.mtg_red);
        } else if (getColors().contains(MTGCard.GREEN)) {
            mtgColor = context.getResources().getColor(R.color.mtg_green);
        }
        return mtgColor;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && getClass() == o.getClass() && multiVerseId == ((MTGCard) o).multiVerseId;
    }

    @Override
    public int hashCode() {
        return multiVerseId;
    }

    public boolean isWhite() {
        return manaCost != null && manaCost.contains("W");
    }

    public boolean isBlue() {
        return manaCost != null && manaCost.contains("U");
    }

    public boolean isBlack() {
        return manaCost != null && manaCost.contains("B");
    }

    public boolean isRed() {
        return manaCost != null && manaCost.contains("R");
    }

    public boolean isGreen() {
        return manaCost != null && manaCost.contains("G");
    }

    public boolean hasNoColor() {
        return manaCost == null || !manaCost.matches(".*[WUBRG].*");
    }


}

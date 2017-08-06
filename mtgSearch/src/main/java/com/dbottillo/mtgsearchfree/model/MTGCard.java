package com.dbottillo.mtgsearchfree.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.dbottillo.mtgsearchfree.R;

import java.util.ArrayList;
import java.util.List;

public class MTGCard implements Comparable<MTGCard>, Parcelable {

    private int id;
    private String name;
    private String type;
    private List<String> types;
    private List<String> subTypes;
    private List<Integer> colors;
    private int cmc;
    private String rarity;
    private String power;
    private String toughness;
    private String manaCost;
    private String text;
    private boolean isAMultiColor;
    private boolean isALand;
    private boolean isAnArtifact;
    private int multiVerseId;
    private MTGSet set;

    private int quantity;
    private boolean sideboard = false;
    private String layout;
    private String number;

    private List<String> rulings;

    private List<String> names;
    private List<String> superTypes;
    private String artist;
    private String flavor;
    private int loyalty;
    private List<String> printings;
    private String originalText;

    public MTGCard() {
        this.colors = new ArrayList<>();
        this.types = new ArrayList<>();
        this.subTypes = new ArrayList<>();
        this.rulings = new ArrayList<>();
        this.names = new ArrayList<>();
        this.superTypes = new ArrayList<>();
        this.printings = new ArrayList<>();
        this.quantity = 1;
        this.multiVerseId = -1;
    }

    public MTGCard(int id) {
        this();
        this.id = id;
    }

    public MTGCard(int id, int multiVerseId) {
        this(id);
        this.multiVerseId = multiVerseId;
    }

    public MTGCard(Parcel in) {
        this();
        readFromParcel(in);
    }

    public void addColor(String color) {
        addColor(CardProperties.COLOR.getNumberFromString(color));
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
        dest.writeInt(multiVerseId);
        dest.writeParcelable(set, flags);
        dest.writeInt(quantity);
        dest.writeInt(sideboard ? 1 : 0);
        dest.writeStringList(rulings);
        dest.writeString(layout);
        dest.writeString(number);
        dest.writeStringList(names);
        dest.writeStringList(superTypes);
        dest.writeString(artist);
        dest.writeString(flavor);
        dest.writeInt(loyalty);
        dest.writeStringList(printings);
        dest.writeString(originalText);
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
        multiVerseId = in.readInt();
        set = in.readParcelable(MTGSet.class.getClassLoader());
        quantity = in.readInt();
        sideboard = in.readInt() == 1;
        in.readStringList(rulings);
        layout = in.readString();
        number = in.readString();
        in.readStringList(names);
        in.readStringList(superTypes);
        artist = in.readString();
        flavor = in.readString();
        loyalty = in.readInt();
        in.readStringList(printings);
        originalText = in.readString();
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

    public List<String> getTypes() {
        return types;
    }

    public void addType(String type) {
        this.types.add(type);
    }

    public List<String> getSubTypes() {
        return subTypes;
    }

    public void addSubType(String subType) {
        this.subTypes.add(subType);
    }

    public List<Integer> getColors() {
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
        return !isMultiColor() && !isLand() && !isArtifact() && getColors().size() == 0;
    }

    public int getMultiVerseId() {
        return multiVerseId;
    }

    public void setMultiVerseId(int multiVerseId) {
        this.multiVerseId = multiVerseId;
    }

    public void belongsTo(MTGSet set) {
        this.set = set;
    }

    public MTGSet getSet() {
        return set;
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
        return "MTGCard: [" + name + "," + multiVerseId + "," + colors + "," + rarity + "]";
    }

    public void addRuling(String rule) {
        this.rulings.add(rule);
    }

    public void setColors(List<Integer> colors) {
        this.colors = colors;
    }

    public List<String> getNames() {
        return names;
    }

    public List<String> getSuperTypes() {
        return superTypes;
    }

    public String getFlavor() {
        return flavor;
    }

    public int getLoyalty() {
        return loyalty;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public void setSuperTypes(List<String> superTypes) {
        this.superTypes = superTypes;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    public void setLoyalty(int loyalty) {
        this.loyalty = loyalty;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public List<String> getPrintings() {
        return printings;
    }

    public void setPrintings(List<String> printings) {
        this.printings = printings;
    }

    public boolean isCommon() {
        return rarity.equalsIgnoreCase(CardFilter.FILTER_COMMON);
    }

    public boolean isUncommon() {
        return rarity.equalsIgnoreCase(CardFilter.FILTER_UNCOMMON);
    }

    public boolean isRare() {
        return rarity.equalsIgnoreCase(CardFilter.FILTER_RARE);
    }

    public boolean isMythicRare() {
        return rarity.equalsIgnoreCase(CardFilter.FILTER_MYHTIC);
    }

    public String getImage() {
        if (number != null && number.length() > 0
                && !set.getCode().equalsIgnoreCase("MM3")
                && !set.getCode().equalsIgnoreCase("MPS")
                && !set.getCode().equalsIgnoreCase("DDS")
                && !set.getCode().equalsIgnoreCase("AKH")
                && !set.getCode().equalsIgnoreCase("MPS_AKH")) {
            return "http://magiccards.info/scans/en/" + set.getMagicCardsInfoCode() + "/" + number + ".jpg";
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

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
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

    @VisibleForTesting
    public int getSingleColor() {
        if (isAMultiColor || getColors().size() == 0) {
            return -1;
        }
        return getColors().get(0);
    }

    public int getMtgColor(Context context) {
        int mtgColor = context.getResources().getColor(R.color.mtg_other);
        if (isMultiColor()) {
            mtgColor = context.getResources().getColor(R.color.mtg_multi);
        } else if (getColors().contains(CardProperties.COLOR.WHITE.getValue())) {
            mtgColor = context.getResources().getColor(R.color.mtg_white);
        } else if (getColors().contains(CardProperties.COLOR.BLUE.getValue())) {
            mtgColor = context.getResources().getColor(R.color.mtg_blue);
        } else if (getColors().contains(CardProperties.COLOR.BLACK.getValue())) {
            mtgColor = context.getResources().getColor(R.color.mtg_black);
        } else if (getColors().contains(CardProperties.COLOR.RED.getValue())) {
            mtgColor = context.getResources().getColor(R.color.mtg_red);
        } else if (getColors().contains(CardProperties.COLOR.GREEN.getValue())) {
            mtgColor = context.getResources().getColor(R.color.mtg_green);
        }
        return mtgColor;
    }

    @SuppressWarnings({"SimplifiableIfStatement", "RedundantIfStatement"})
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MTGCard other = (MTGCard) o;
        /*for (Field field : MTGCard.class.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                try {
                    if (field.get(this) != null && !field.get(this).equals(field.get(other))) {
                        Log.e("CUSTOM", "error on: " + field.getName() + " with values: " + field.get(this) + " vs " + field.get(other));
                    }
                } catch (IllegalAccessException e) {
                    LOG.e("impossible to read value");
                    e.printStackTrace();
                }
            }
        }*/

        if (!equalOrNull(name, other.name)) {
            return false;
        }
        if (multiVerseId > 0 && other.multiVerseId > 0 && !(multiVerseId == other.multiVerseId)) {
            return false;
        }
        if (!equalOrNull(type, other.type)) {
            return false;
        }
        if (!types.equals(other.types)) {
            return false;
        }
        if (!(subTypes == null && other.subTypes == null || (subTypes != null && other.subTypes != null && subTypes.equals(other.subTypes)))) {
            return false;
        }
        if (!(colors == null && other.colors == null || (colors != null && other.colors != null && colors.equals(other.colors)))) {
            return false;
        }
        if (!(cmc == other.cmc)) {
            return false;
        }
        if (!equalOrNull(rarity, other.rarity)) {
            return false;
        }
        if (!equalOrNull(power, other.power)) {
            return false;
        }
        if (!equalOrNull(toughness, other.toughness)) {
            return false;
        }
        if (!equalOrNull(manaCost, other.manaCost)) {
            return false;
        }
        if (!equalOrNull(text, other.text)) {
            return false;
        }
        if (!(isAMultiColor == other.isAMultiColor)) {
            return false;
        }
        if (!(isALand == other.isALand)) {
            return false;
        }
        if (!(set == null && other.set == null || (set != null && other.set != null && set.equals(other.set)))) {
            return false;
        }
        if (!equalOrNull(layout, other.layout)) {
            return false;
        }
        if (!equalOrNull(number, other.number)) {
            return false;
        }
        if (!(rulings == null && other.rulings == null || (rulings != null && other.rulings != null && rulings.equals(other.rulings)))) {
            return false;
        }
        if (!(names == null && other.names == null || (names != null && other.names != null && names.equals(other.names)))) {
            return false;
        }
        if (!(superTypes == null && other.superTypes == null || (superTypes != null && other.superTypes != null && superTypes.equals(other.superTypes)))) {
            return false;
        }
        if (!equalOrNull(flavor, other.flavor)) {
            return false;
        }
        if (!equalOrNull(artist, other.artist)) {
            return false;
        }
        if (!(loyalty == other.loyalty)) {
            return false;
        }
        if (!(printings == null && other.printings == null || (printings != null && other.printings != null && printings.equals(other.printings)))) {
            return false;
        }
        return true;
    }

    private boolean equalOrNull(String first, String second) {
        return (first == null && second == null || (first != null && second != null
                && first.equals(second)));
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

    public boolean isDoubleFaced() {
        return layout != null && layout.equalsIgnoreCase("double-faced");
    }

    public String bigToString() {
        return "MTGCard{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", type='" + type + '\''
                + ", types=" + types
                + ", subTypes=" + subTypes
                + ", colors=" + colors
                + ", cmc=" + cmc
                + ", rarity='" + rarity + '\''
                + ", power='" + power + '\''
                + ", toughness='" + toughness + '\''
                + ", manaCost='" + manaCost + '\''
                + ", text='" + text + '\''
                + ", isAMultiColor=" + isAMultiColor
                + ", isALand=" + isALand
                + ", isAnArtifact=" + isAnArtifact
                + ", multiVerseId=" + multiVerseId
                + ", set=" + set
                + ", quantity=" + quantity
                + ", sideboard=" + sideboard
                + ", layout='" + layout + '\''
                + ", number='" + number + '\''
                + ", rulings=" + rulings
                + ", names=" + names
                + ", superTypes=" + superTypes
                + ", artist='" + artist + '\''
                + ", flavor='" + flavor + '\''
                + ", loyalty=" + loyalty
                + ", printings=" + printings
                + ", originalText=" + originalText
                + '}';
    }

}

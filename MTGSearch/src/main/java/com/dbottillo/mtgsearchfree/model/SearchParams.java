package com.dbottillo.mtgsearchfree.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchParams implements Parcelable {

    private String name;
    private String types;
    private String text;
    private IntParam cmc;
    private IntParam power;
    private IntParam tough;
    private boolean white, blue, black, red, green, onlyMulti, noMulti, land;
    private boolean common, uncommon, rare, mythic;
    private int setId;

    public SearchParams(Parcel in) {
        readFromParcel(in);
    }

    public SearchParams() {
        this.name = "";
        this.types = "";
        this.text = "";
        this.cmc = null;
        this.power = null;
        this.tough = null;
        white = false;
        blue = false;
        black = false;
        red = false;
        green = false;
        onlyMulti = false;
        noMulti = false;
        common = false;
        uncommon = false;
        rare = false;
        mythic = false;
        land = false;
        setId = -1;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public SearchParams setName(String name) {
        this.name = name;
        return this;
    }

    public String getTypes() {
        return types;
    }

    public SearchParams setTypes(String types) {
        this.types = types;
        return this;
    }

    public IntParam getCmc() {
        return cmc;
    }

    public SearchParams setCmc(IntParam cmc) {
        this.cmc = cmc;
        return this;
    }

    public IntParam getPower() {
        return power;
    }

    public SearchParams setPower(IntParam power) {
        this.power = power;
        return this;
    }

    public IntParam getTough() {
        return tough;
    }

    public SearchParams setTough(IntParam tough) {
        this.tough = tough;
        return this;
    }

    public boolean isWhite() {
        return white;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }

    public boolean isBlue() {
        return blue;
    }

    public void setBlue(boolean blue) {
        this.blue = blue;
    }

    public boolean isBlack() {
        return black;
    }

    public void setBlack(boolean black) {
        this.black = black;
    }

    public boolean isRed() {
        return red;
    }

    public void setRed(boolean red) {
        this.red = red;
    }

    public boolean isGreen() {
        return green;
    }

    public void setGreen(boolean green) {
        this.green = green;
    }

    public boolean onlyMulti() {
        return onlyMulti;
    }

    public void setOnlyMulti(boolean onlyMulti) {
        this.onlyMulti = onlyMulti;
        if (onlyMulti) {
            this.noMulti = false;
        }
    }

    public boolean isNoMulti() {
        return noMulti;
    }

    public void setNoMulti(boolean noMulti) {
        this.noMulti = noMulti;
        if (noMulti) {
            this.onlyMulti = false;
        }
    }

    public boolean isCommon() {
        return common;
    }

    public void setCommon(boolean common) {
        this.common = common;
    }

    public boolean isUncommon() {
        return uncommon;
    }

    public void setUncommon(boolean uncommon) {
        this.uncommon = uncommon;
    }

    public boolean isRare() {
        return rare;
    }

    public void setRare(boolean rare) {
        this.rare = rare;
    }

    public boolean isMythic() {
        return mythic;
    }

    public void setMythic(boolean mythic) {
        this.mythic = mythic;
    }

    public int getSetId() {
        return setId;
    }

    public void setSetId(int setId) {
        this.setId = setId;
    }

    public boolean isLand() {
        return land;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(types);
        dest.writeString(text);
        dest.writeParcelable(cmc, flags);
        dest.writeParcelable(power, flags);
        dest.writeParcelable(tough, flags);
        dest.writeInt(white ? 1 : 0);
        dest.writeInt(blue ? 1 : 0);
        dest.writeInt(black ? 1 : 0);
        dest.writeInt(red ? 1 : 0);
        dest.writeInt(green ? 1 : 0);
        dest.writeInt(onlyMulti ? 1 : 0);
        dest.writeInt(noMulti ? 1 : 0);
        dest.writeInt(land ? 1 : 0);
        dest.writeInt(common ? 1 : 0);
        dest.writeInt(uncommon ? 1 : 0);
        dest.writeInt(rare ? 1 : 0);
        dest.writeInt(mythic ? 1 : 0);
        dest.writeInt(setId);
    }

    private void readFromParcel(Parcel in) {
        name = in.readString();
        types = in.readString();
        text = in.readString();
        cmc = in.readParcelable(IntParam.class.getClassLoader());
        power = in.readParcelable(IntParam.class.getClassLoader());
        tough = in.readParcelable(IntParam.class.getClassLoader());
        white = in.readInt() == 1;
        blue = in.readInt() == 1;
        black = in.readInt() == 1;
        red = in.readInt() == 1;
        green = in.readInt() == 1;
        onlyMulti = in.readInt() == 1;
        noMulti = in.readInt() == 1;
        land = in.readInt() == 1;
        common = in.readInt() == 1;
        uncommon = in.readInt() == 1;
        rare = in.readInt() == 1;
        mythic = in.readInt() == 1;
        setId = in.readInt();
    }

    public static final Parcelable.Creator<SearchParams> CREATOR = new Parcelable.Creator<SearchParams>() {
        @Override
        public SearchParams createFromParcel(Parcel source) {
            return new SearchParams(source);
        }

        @Override
        public SearchParams[] newArray(int size) {
            return new SearchParams[size];
        }
    };

    @Override
    public String toString() {
        return "SearchParams{"
                + "name='" + name + '\''
                + ", types='" + types + '\''
                + ", text='" + text + '\''
                + ", cmc=" + cmc
                + ", power=" + power
                + ", tough=" + tough
                + ", white=" + white
                + ", blue=" + blue
                + ", black=" + black
                + ", red=" + red
                + ", green=" + green
                + ", onlyMulti=" + onlyMulti
                + ", noMulti=" + noMulti
                + ", land=" + land
                + ", common=" + common
                + ", uncommon=" + uncommon
                + ", rare=" + rare
                + ", mythic=" + mythic
                + ", setId=" + setId
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchParams other = (SearchParams) o;
        return name.equalsIgnoreCase(other.name) && types.equalsIgnoreCase(other.types) && text.equalsIgnoreCase(other.text)
                && cmc.equals(other.cmc) && power.equals(other.power) && tough.equals(other.tough) && white == other.white
                && blue == other.blue && black == other.black && red == other.red && green == other.green
                && onlyMulti == other.onlyMulti && land == other.land
                && noMulti == other.noMulti && common == other.common && other.uncommon == uncommon && rare == other.rare
                && mythic == other.mythic && setId == other.setId;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (types != null ? types.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (cmc != null ? cmc.hashCode() : 0);
        result = 31 * result + (power != null ? power.hashCode() : 0);
        result = 31 * result + (tough != null ? tough.hashCode() : 0);
        result = 31 * result + (white ? 1 : 0);
        result = 31 * result + (blue ? 1 : 0);
        result = 31 * result + (black ? 1 : 0);
        result = 31 * result + (red ? 1 : 0);
        result = 31 * result + (green ? 1 : 0);
        result = 31 * result + (onlyMulti ? 1 : 0);
        result = 31 * result + (noMulti ? 1 : 0);
        result = 31 * result + (land ? 1 : 0);
        result = 31 * result + (common ? 1 : 0);
        result = 31 * result + (uncommon ? 1 : 0);
        result = 31 * result + (rare ? 1 : 0);
        result = 31 * result + (mythic ? 1 : 0);
        result = 31 * result + setId;
        return result;
    }

    public boolean atLeastOneColor() {
        return white || black || blue || red || green;
    }

    public boolean atLeastOneRarity() {
        return common || uncommon || rare || mythic;
    }

    public boolean isValid() {
        return ((name != null && name.length() > 0) || (types != null && types.length() > 0)
                || (cmc != null && cmc.getValue() > 0)
                || (power != null && power.getValue() > 0)
                || (tough != null && tough.getValue() > 0)
                || setId > 0
                || (text != null && text.length() > 0)
                || land
                || atLeastOneColor() || atLeastOneRarity());
    }

    public void setLand(boolean land) {
        this.land = land;
    }
}

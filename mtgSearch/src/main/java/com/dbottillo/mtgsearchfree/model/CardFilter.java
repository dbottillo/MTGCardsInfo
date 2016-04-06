package com.dbottillo.mtgsearchfree.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CardFilter implements Parcelable {

    public boolean white = true;
    public boolean blue = true;
    public boolean black = true;
    public boolean red = true;
    public boolean green = true;

    public boolean artifact = true;
    public boolean land = true;
    public boolean eldrazi = true;

    public boolean common = true;
    public boolean uncommon = true;
    public boolean rare = true;
    public boolean mythic = true;

    public enum TYPE {
        WHITE,
        BLUE,
        BLACK,
        RED,
        GREEN,
        ARTIFACT,
        LAND,
        ELDRAZI,
        COMMON,
        UNCOMMON,
        RARE,
        MYTHIC
    }

    public CardFilter(){

    }

    public CardFilter(Parcel in) {
        white = in.readByte() != 0;
        blue = in.readByte() != 0;
        black = in.readByte() != 0;
        red = in.readByte() != 0;
        green = in.readByte() != 0;
        artifact = in.readByte() != 0;
        land = in.readByte() != 0;
        eldrazi = in.readByte() != 0;
        common = in.readByte() != 0;
        uncommon = in.readByte() != 0;
        rare = in.readByte() != 0;
        mythic = in.readByte() != 0;
    }

    public static final Creator<CardFilter> CREATOR = new Creator<CardFilter>() {
        @Override
        public CardFilter createFromParcel(Parcel in) {
            return new CardFilter(in);
        }

        @Override
        public CardFilter[] newArray(int size) {
            return new CardFilter[size];
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(white ? 1 : 0);
        dest.writeInt(blue ? 1 : 0);
        dest.writeInt(black ? 1 : 0);
        dest.writeInt(red ? 1 : 0);
        dest.writeInt(green ? 1 : 0);

        dest.writeInt(artifact ? 1 : 0);
        dest.writeInt(land ? 1 : 0);
        dest.writeInt(eldrazi ? 1 : 0);

        dest.writeInt(common ? 1 : 0);
        dest.writeInt(uncommon ? 1 : 0);
        dest.writeInt(rare ? 1 : 0);
        dest.writeInt(mythic ? 1 : 0);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CardFilter other = (CardFilter) o;
        return white == other.white && blue == other.blue && black == other.black && red == other.red
                && green == other.green && artifact == other.artifact && land == other.land
                && common == other.common && uncommon == other.uncommon
                && rare == other.rare && mythic == other.mythic;
    }
}

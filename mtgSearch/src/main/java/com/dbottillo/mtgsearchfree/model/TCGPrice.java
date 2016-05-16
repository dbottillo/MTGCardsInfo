package com.dbottillo.mtgsearchfree.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TCGPrice implements Parcelable {

    private String hiPrice;
    private String lowprice;
    private String avgPrice;
    private String link;
    private String errorPrice;
    private boolean error;
    private boolean mNotFound;

    public TCGPrice() {

    }

    public TCGPrice(Parcel in) {
        readFromParcel(in);
    }

    public String getHiPrice() {
        return hiPrice;
    }

    public void setHiPrice(String hiPrice) {
        this.hiPrice = hiPrice;
    }

    public String getLowprice() {
        return lowprice;
    }

    public void setLowprice(String lowprice) {
        this.lowprice = lowprice;
    }

    public String getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(String avgPrice) {
        this.avgPrice = avgPrice;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isAnError() {
        return error;
    }

    public void setError(String errorPrice) {
        error = true;
        this.errorPrice = errorPrice;
    }

    public String getErrorPrice() {
        return errorPrice;
    }

    public String toString() {
        return "[TCGPrice] H:" + hiPrice + " - A:" + avgPrice + " - L:" + lowprice + " - " + link;
    }

    public String toDisplay(boolean isLandscape) {
        if (hiPrice.length() > 5 && !isLandscape) {
            return " A:" + avgPrice + "$  L:" + lowprice + "$";
        }
        return " H:" + hiPrice + "$   A:" + avgPrice + "$   L:" + lowprice + "$";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hiPrice);
        dest.writeString(avgPrice);
        dest.writeString(lowprice);
        dest.writeString(link);
        dest.writeString(errorPrice);
        dest.writeInt(error ? 1 : 0);
    }

    public void readFromParcel(Parcel in) {
        hiPrice = in.readString();
        avgPrice = in.readString();
        lowprice = in.readString();
        link = in.readString();
        errorPrice = in.readString();
        error = in.readInt() == 1;
    }

    public static final Parcelable.Creator<TCGPrice> CREATOR = new Parcelable.Creator<TCGPrice>() {
        @Override
        public TCGPrice createFromParcel(Parcel source) {
            return new TCGPrice(source);
        }

        @Override
        public TCGPrice[] newArray(int size) {
            return new TCGPrice[size];
        }
    };

    public void setNotFound(boolean notFound) {
        mNotFound = notFound;
    }

    public boolean isNotFound() {
        return mNotFound;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TCGPrice other = (TCGPrice) o;
        return hiPrice.equals(other.hiPrice) && avgPrice.equals(other.avgPrice) && lowprice.equals(other.lowprice)
                && link.equals(other.link) && errorPrice.equalsIgnoreCase(other.errorPrice) && error == other.error;
    }

    @Override
    public int hashCode() {
        int result = hiPrice != null ? hiPrice.hashCode() : 0;
        result = 31 * result + (lowprice != null ? lowprice.hashCode() : 0);
        result = 31 * result + (avgPrice != null ? avgPrice.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (errorPrice != null ? errorPrice.hashCode() : 0);
        result = 31 * result + (error ? 1 : 0);
        result = 31 * result + (mNotFound ? 1 : 0);
        return result;
    }
}
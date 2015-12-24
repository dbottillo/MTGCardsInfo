package com.dbottillo.mtgsearchfree.search;

import android.os.Parcel;
import android.os.Parcelable;

public class IntParam implements Parcelable {

    private String operator;
    private int value;

    public IntParam(Parcel in) {
        readFromParcel(in);
    }

    public IntParam(String operator, int value){
        this.operator = operator;
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(operator);
        dest.writeInt(value);
    }

    private void readFromParcel(Parcel in) {
        operator = in.readString();
        value = in.readInt();
    }

    public static final Parcelable.Creator<IntParam> CREATOR = new Parcelable.Creator<IntParam>() {
        @Override
        public IntParam createFromParcel(Parcel source) {
            return new IntParam(source);
        }

        @Override
        public IntParam[] newArray(int size) {
            return new IntParam[size];
        }
    };
}

package com.dbottillo.mtgsearchfree.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MTGSet implements Parcelable {

    int id;
    String code;
    String name;

    public MTGSet(int id) {
        this.id = id;
    }

    public MTGSet(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public MTGSet(Parcel in) {
        readFromParcel(in);
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(code);
    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        code = in.readString();
    }

    public static final Parcelable.Creator<MTGSet> CREATOR = new Parcelable.Creator<MTGSet>() {
        @Override
        public MTGSet createFromParcel(Parcel source) {
            return new MTGSet(source);
        }

        @Override
        public MTGSet[] newArray(int size) {
            return new MTGSet[size];
        }
    };

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MTGSet other = (MTGSet) o;
        return name.equals(other.getName()) && code.equals(other.getCode());
    }
}

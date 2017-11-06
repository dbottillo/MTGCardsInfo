package com.dbottillo.mtgsearchfree.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Deck implements Parcelable {

    private long id;
    private String name;
    private boolean archived;
    private int numberOfCards = 0;
    private int sizeOfSideboard = 0;

    public Deck(Parcel in) {
        readFromParcel(in);
    }

    public Deck(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public int getNumberOfCards() {
        return numberOfCards;
    }

    public void setNumberOfCards(int numberOfCards) {
        this.numberOfCards = numberOfCards;
    }

    public int getSizeOfSideboard() {
        return sizeOfSideboard;
    }

    public void setSizeOfSideboard(int sizeOfSideboard) {
        this.sizeOfSideboard = sizeOfSideboard;
    }

    public static final Parcelable.Creator<Deck> CREATOR = new Parcelable.Creator<Deck>() {
        @Override
        public Deck createFromParcel(Parcel source) {
            return new Deck(source);
        }

        @Override
        public Deck[] newArray(int size) {
            return new Deck[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.archived = in.readInt() == 1;
        this.numberOfCards = in.readInt();
        this.sizeOfSideboard = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeInt(archived ? 1 : 0);
        dest.writeInt(numberOfCards);
        dest.writeInt(sizeOfSideboard);
    }

    @Override
    public String toString() {
        return "[" + id + "," + name + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Deck other = (Deck) o;
        return id == other.id && name.equals(other.getName()) && archived == other.archived
                && numberOfCards == other.numberOfCards && sizeOfSideboard == other.sizeOfSideboard;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (archived ? 1 : 0);
        result = 31 * result + numberOfCards;
        result = 31 * result + sizeOfSideboard;
        return result;
    }
}

package com.dbottillo.resources;

import android.os.Parcel;
import android.os.Parcelable;

public class Deck implements Parcelable {

    int id;
    String name;
    boolean archived;
    int numberOfCards = 0;
    int sizeOfSideboard = 0;

    public Deck(Parcel in) {
        readFromParcel(in);
    }

    public Deck(int id) {
        this.id = id;
    }

    public int getId() {
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

    public void addNumberOfCards(int numberOfCards){
        this.numberOfCards += numberOfCards;
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
        this.id = in.readInt();
        this.name = in.readString();
        this.archived = in.readInt() == 1;
        this.numberOfCards = in.readInt();
        this.sizeOfSideboard = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(archived ? 1 : 0);
        dest.writeInt(numberOfCards);
        dest.writeInt(sizeOfSideboard);
    }

    @Override
    public String toString() {
        return name;
    }
}

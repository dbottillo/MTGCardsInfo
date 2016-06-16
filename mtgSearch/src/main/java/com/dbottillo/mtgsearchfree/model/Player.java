package com.dbottillo.mtgsearchfree.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Player implements Parcelable {

    int id;
    int life;
    int poisonCount;
    String name;
    int diceResult;

    public Player() {

    }

    public Player(int id, String name) {
        this.id = id;
        this.life = 20;
        this.poisonCount = 10;
        this.name = name;
        this.diceResult = 0;
    }

    public Player(Parcel in) {
        readFromParcel(in);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLife() {
        return life;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void changeLife(int value) {
        this.life += value;
    }

    public int getPoisonCount() {
        return poisonCount;
    }

    public void changePoisonCount(int value) {
        this.poisonCount += value;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public void setPoisonCount(int poisonCount) {
        this.poisonCount = poisonCount;
    }

    public int getDiceResult() {
        return diceResult;
    }

    public void setDiceResult(int diceResult) {
        this.diceResult = diceResult;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(life);
        dest.writeInt(poisonCount);
        dest.writeInt(diceResult);
    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        life = in.readInt();
        poisonCount = in.readInt();
        diceResult = in.readInt();
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel source) {
            return new Player(source);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    @Override
    public String toString() {
        return "[" + id + "," + name + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Player other = (Player) o;
        return id == other.id && name.equals(other.getName());
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + life;
        result = 31 * result + poisonCount;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + diceResult;
        return result;
    }
}

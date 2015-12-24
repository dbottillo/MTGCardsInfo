package com.dbottillo.mtgsearchfree.resources;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.dbottillo.mtgsearchfree.database.PlayerDataSource;

public class Player implements Parcelable {

    int id;
    int life;
    int poisonCount;
    String name;
    int diceResult;

    private Player() {

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

    private void setId(int id) {
        this.id = id;
    }

    public int getLife() {
        return life;
    }

    public String getName() {
        return name;
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

    public String toString() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ContentValues createContentValue() {
        ContentValues values = new ContentValues();
        values.put(PlayerDataSource.PlayerEntry._ID, id);
        values.put(PlayerDataSource.PlayerEntry.COLUMN_NAME_LIFE, life);
        values.put(PlayerDataSource.PlayerEntry.COLUMN_NAME_POISON, poisonCount);
        values.put(PlayerDataSource.PlayerEntry.COLUMN_NAME_NAME, name);
        return values;
    }

    public static Player fromCursor(Cursor cursor) {
        Player player = new Player();
        player.setId(cursor.getInt(cursor.getColumnIndex(PlayerDataSource.PlayerEntry._ID)));
        player.setLife(cursor.getInt(cursor.getColumnIndex(PlayerDataSource.PlayerEntry.COLUMN_NAME_LIFE)));
        player.setPoisonCount(cursor.getInt(cursor.getColumnIndex(PlayerDataSource.PlayerEntry.COLUMN_NAME_POISON)));
        player.setName(cursor.getString(cursor.getColumnIndex(PlayerDataSource.PlayerEntry.COLUMN_NAME_NAME)));
        return player;
    }
}

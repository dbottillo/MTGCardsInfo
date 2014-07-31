package com.dbottillo.resources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.dbottillo.database.SetContract.SetEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class Player implements Parcelable {

    int id;
    int life;
    int poisonCount;
    String name;

    public Player(int id, String name){
        this.id = id;
        this.life = 20;
        this.poisonCount = 10;
        this.name = name;
    }

    public Player(Parcel in){
        readFromParcel(in);
    }

    public int getId() {
        return id;
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
    }

    private void readFromParcel(Parcel in){
        id = in.readInt();
        name = in.readString();
        life = in.readInt();
        poisonCount = in.readInt();
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

    public String toString(){
        return name;
    }

}

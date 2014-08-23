package com.dbottillo.resources;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.dbottillo.database.HSSetContract;
import com.dbottillo.database.SetContract.SetEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class HSSet extends GameSet{

    int id;
    String name;
    ArrayList<HSCard> cards;

    public HSSet(int id){
        this.id = id;
        this.cards = new ArrayList<HSCard>();
    }

    public HSSet(Parcel in){
        this.cards = new ArrayList<HSCard>();
        readFromParcel(in);
    }

    public static HSSet createHearthstoneSetFromCursor(Cursor cursor) {
        HSSet set = new HSSet(cursor.getInt(cursor.getColumnIndex(HSSetContract.HSSetEntry._ID)));
        set.setName(cursor.getString(cursor.getColumnIndex(HSSetContract.HSSetEntry.COLUMN_NAME_NAME)));
        return set;
    }

    public static ContentValues createContentValueFromJSON(String name) throws JSONException{
        ContentValues values = new ContentValues();
        values.put(HSSetContract.HSSetEntry.COLUMN_NAME_NAME, name);
        return values;
    }

    public static HSSet createHearthstoneSetFromJson(int id, JSONObject object) throws JSONException{
        HSSet set = new HSSet(id);
        set.setName(object.getString("name"));

        return set;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getCode() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void addCard(GameCard card) {
        cards.add((HSCard) card);
    }

    @Override
    public void clear() {
        cards.clear();
    }

    @Override
    public ArrayList<GameCard> getCards() {
        ArrayList<GameCard> gameCards = new ArrayList<GameCard>();
        for (HSCard card : cards){
            gameCards.add(card);
        }
        return gameCards;
    }


    public void addCard(HSCard card) {
        this.cards.add(card);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeTypedList(cards);
    }

    private void readFromParcel(Parcel in){
        id = in.readInt();
        name = in.readString();
        in.readTypedList(cards, HSCard.CREATOR);
    }

    public static final Creator<HSSet> CREATOR = new Creator<HSSet>() {
        @Override
        public HSSet createFromParcel(Parcel source) {
            return new HSSet(source);
        }

        @Override
        public HSSet[] newArray(int size) {
            return new HSSet[size];
        }
    };


}

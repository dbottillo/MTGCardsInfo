package com.dbottillo.mtgsearchfree.resources;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.dbottillo.mtgsearchfree.database.SetDataSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MTGSet implements Parcelable {

    int id;
    String code;
    String name;
    ArrayList<MTGCard> cards;

    public MTGSet(int id) {
        this.id = id;
        this.cards = new ArrayList<>();
    }

    public MTGSet(int id, String name) {
        this.id = id;
        this.name = name;
        this.cards = new ArrayList<>();
    }

    public MTGSet(Parcel in) {
        this.cards = new ArrayList<>();
        readFromParcel(in);
    }

    public static ContentValues createContentValueFromJSON(JSONObject object) throws JSONException {
        ContentValues values = new ContentValues();
        values.put(SetDataSource.COLUMNS.CODE.getName(), object.getString("code"));
        values.put(SetDataSource.COLUMNS.NAME.getName(), object.getString("name"));
        return values;
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

    public void clear() {
        cards.clear();
    }

    public ArrayList<MTGCard> getCards() {
        ArrayList<MTGCard> gameCards = new ArrayList<MTGCard>();
        for (MTGCard card : cards) {
            gameCards.add(card);
        }
        return gameCards;
    }

    public void addCard(MTGCard card) {
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
        dest.writeString(code);
        dest.writeTypedList(cards);
    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        code = in.readString();
        in.readTypedList(cards, MTGCard.CREATOR);
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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MTGSet other = (MTGSet) o;
        return name.equals(other.getName()) && code.equals(other.getCode());
    }
}

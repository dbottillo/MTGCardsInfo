package com.dbottillo.resources;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class MTGSet implements Parcelable {

    int id;
    String code;
    String name;
    ArrayList<MTGCard> cards;

    public MTGSet(int id){
        this.id = id;
        this.cards = new ArrayList<MTGCard>();
    }

    public MTGSet(Parcel in){
        this.cards = new ArrayList<MTGCard>();
        readFromParcel(in);
    }

    public static MTGSet createMagicSetFromJson(int id, JSONObject object) throws JSONException{
        MTGSet set = new MTGSet(id);
        set.setCode(object.getString("code"));
        set.setName(object.getString("name"));

        //JSONArray cardsJ = object.getJSONArray("cards");
        //for (int i=0; i<cardsJ.length(); i++){
        //    set.addCard(MTGCard.createCardFromJson(i, cardsJ.getJSONObject(i)));
        //}

        return set;
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

    public ArrayList<MTGCard> getCards() {
        return cards;
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

    private void readFromParcel(Parcel in){
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
}

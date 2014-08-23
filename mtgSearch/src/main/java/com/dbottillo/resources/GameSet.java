package com.dbottillo.resources;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.dbottillo.database.HSSetContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public abstract class GameSet implements Parcelable{

    public abstract int getId();

    public abstract String getName();

    public abstract String getCode();

    public abstract void setName(String name);

    public abstract void addCard(GameCard card);

    public abstract void clear();

    public abstract ArrayList<GameCard> getCards();

}

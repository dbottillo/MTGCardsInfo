package com.dbottillo.resources;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.dbottillo.database.CardContract.CardEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public abstract class GameCard extends Object implements Parcelable{


    public abstract long getId();

    public abstract String getName();

    public abstract String getRarity();

    public abstract String getManaCost();

    public abstract String getSetName();

    public abstract String toString();
}

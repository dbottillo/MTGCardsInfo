package com.dbottillo.resources;

import android.os.Parcelable;

public abstract class GameCard extends Object implements Parcelable {


    public abstract long getId();

    public abstract String getName();

    public abstract String getRarity();

    public abstract String getManaCost();

    public abstract String getSetName();

    public abstract String toString();

    public abstract String getImage();
}

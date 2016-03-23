package com.dbottillo.mtgsearchfree.model.storage;

import android.content.Context;

import com.dbottillo.mtgsearchfree.model.database.MTGDatabaseHelper;
import com.dbottillo.mtgsearchfree.model.MTGSet;

import java.util.ArrayList;

public class SetsStorage {

    private Context context;

    public SetsStorage(Context context) {
        this.context = context;
    }


    public ArrayList<MTGSet> load() {
        return MTGDatabaseHelper.getInstance(context).getSets();
    }

}


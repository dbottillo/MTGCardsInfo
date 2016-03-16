package com.dbottillo.mtgsearchfree.model.storage;

import android.content.Context;

import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper;
import com.dbottillo.mtgsearchfree.resources.MTGSet;

import java.util.ArrayList;

public class SetsStorage {

    private Context context;

    SetsStorage(Context context) {
        this.context = context;
    }


    public ArrayList<MTGSet> load() {
        MTGDatabaseHelper helper = new MTGDatabaseHelper(context);
        return helper.getSets();
    }

}


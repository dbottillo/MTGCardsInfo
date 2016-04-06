package com.dbottillo.mtgsearchfree.model.storage;

import android.content.Context;

import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.database.MTGDatabaseHelper;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.ArrayList;
import java.util.List;

public class SetsStorage {

    private Context context;

    public SetsStorage(Context context) {
        LOG.d("created");
        this.context = context;
    }


    public List<MTGSet> load() {
        LOG.d();
        return MTGDatabaseHelper.getInstance(context).getSets();
    }

}


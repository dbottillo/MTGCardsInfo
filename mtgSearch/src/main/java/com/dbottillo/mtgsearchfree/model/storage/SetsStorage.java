package com.dbottillo.mtgsearchfree.model.storage;

import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.database.MTGDatabaseHelper;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.List;

public class SetsStorage {

    private MTGDatabaseHelper databaseHelper;

    public SetsStorage(MTGDatabaseHelper databaseHelper) {
        LOG.d("created");
        this.databaseHelper = databaseHelper;
    }


    public List<MTGSet> load() {
        LOG.d();
        return databaseHelper.getSets();
    }

}


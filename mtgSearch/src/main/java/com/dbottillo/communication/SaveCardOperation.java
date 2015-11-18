package com.dbottillo.communication;

import com.dbottillo.database.CardsDatabaseHelper;
import com.dbottillo.database.DB40Helper;
import com.dbottillo.resources.MTGCard;

class SaveCardOperation extends Operation {

    @Override
    protected void execute(CardsDatabaseHelper helper, Object... params) {
        DB40Helper.openDb();
        DB40Helper.storeCard((MTGCard) params[0]);
        DB40Helper.closeDb();
    }
}

package com.dbottillo.communication;

import com.dbottillo.database.CardsInfoDbHelper;
import com.dbottillo.database.MTGDatabaseHelper;

abstract class Operation {

    protected abstract void execute(MTGDatabaseHelper helper, CardsInfoDbHelper cardsInfoDbHelper, Object... params);

}

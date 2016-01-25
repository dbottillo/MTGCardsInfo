package com.dbottillo.mtgsearchfree.communication;

import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper;

abstract class Operation {

    protected abstract void execute(MTGDatabaseHelper helper, CardsInfoDbHelper cardsInfoDbHelper, Object... params);

}

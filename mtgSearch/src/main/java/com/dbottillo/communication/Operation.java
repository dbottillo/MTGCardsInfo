package com.dbottillo.communication;

import com.dbottillo.database.CardsDatabaseHelper;

abstract class Operation {

    protected abstract void execute(CardsDatabaseHelper helper, Object... params);

}

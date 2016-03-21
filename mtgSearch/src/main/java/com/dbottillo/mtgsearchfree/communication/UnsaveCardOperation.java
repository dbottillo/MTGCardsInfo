package com.dbottillo.mtgsearchfree.communication;

import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.database.FavouritesDataSource;
import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper;
import com.dbottillo.mtgsearchfree.model.MTGCard;

class UnsaveCardOperation extends Operation {

    @Override
    protected void execute(MTGDatabaseHelper helper, CardsInfoDbHelper cardsInfoDbHelper, Object... params) {
        MTGCard card = (MTGCard) params[0];
        FavouritesDataSource.removeFavourites(cardsInfoDbHelper.getWritableDatabase(), card);
    }
}

package com.dbottillo.communication;

import com.dbottillo.database.CardsInfoDbHelper;
import com.dbottillo.database.FavouritesDataSource;
import com.dbottillo.database.MTGDatabaseHelper;
import com.dbottillo.resources.MTGCard;

class SaveCardOperation extends Operation {

    @Override
    protected void execute(MTGDatabaseHelper helper, CardsInfoDbHelper cardsInfoDbHelper, Object... params) {
        MTGCard card = (MTGCard) params[0];
        FavouritesDataSource.saveFavourites(cardsInfoDbHelper.getWritableDatabase(), card);
    }
}

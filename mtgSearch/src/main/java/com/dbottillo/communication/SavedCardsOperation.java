package com.dbottillo.communication;

import com.dbottillo.communication.events.SavedCardsEvent;
import com.dbottillo.database.CardsInfoDbHelper;
import com.dbottillo.database.FavouritesDataSource;
import com.dbottillo.database.MTGDatabaseHelper;

import de.greenrobot.event.EventBus;

class SavedCardsOperation extends Operation {

    @Override
    protected void execute(MTGDatabaseHelper helper, CardsInfoDbHelper cardsInfoDbHelper, Object... params) {
        SavedCardsEvent cardsEvent = new SavedCardsEvent(FavouritesDataSource.getCards(cardsInfoDbHelper.getReadableDatabase(), (boolean)params[0]));
        EventBus.getDefault().postSticky(cardsEvent);
    }
}

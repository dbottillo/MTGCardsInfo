package com.dbottillo.mtgsearchfree.communication;

import com.dbottillo.mtgsearchfree.communication.events.SavedCardsEvent;
import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.database.FavouritesDataSource;
import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper;

import de.greenrobot.event.EventBus;

class SavedCardsOperation extends Operation {

    @Override
    protected void execute(MTGDatabaseHelper helper, CardsInfoDbHelper cardsInfoDbHelper, Object... params) {
        SavedCardsEvent cardsEvent = new SavedCardsEvent(FavouritesDataSource.getCards(cardsInfoDbHelper.getReadableDatabase(), (boolean)params[0]));
        EventBus.getDefault().postSticky(cardsEvent);
    }
}

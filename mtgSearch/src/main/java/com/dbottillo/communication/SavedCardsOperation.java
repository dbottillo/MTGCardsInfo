package com.dbottillo.communication;

import com.dbottillo.communication.events.SavedCardsEvent;
import com.dbottillo.database.CardsDatabaseHelper;
import com.dbottillo.database.DB40Helper;

import de.greenrobot.event.EventBus;

class SavedCardsOperation extends Operation {

    @Override
    protected void execute(CardsDatabaseHelper helper, Object... params) {
        DB40Helper.openDb();
        SavedCardsEvent cardsEvent = new SavedCardsEvent(DB40Helper.getCards());
        DB40Helper.closeDb();
        EventBus.getDefault().postSticky(cardsEvent);
    }
}

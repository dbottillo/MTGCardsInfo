package com.dbottillo.communication;

import com.dbottillo.communication.events.PlayersEvent;
import com.dbottillo.database.CardsDatabaseHelper;
import com.dbottillo.database.DB40Helper;

import de.greenrobot.event.EventBus;

class PlayersOperation extends Operation {

    @Override
    protected void execute(CardsDatabaseHelper helper, Object... params) {
        PlayersEvent cardsEvent = new PlayersEvent(DB40Helper.getPlayers());
        EventBus.getDefault().postSticky(cardsEvent);
    }
}

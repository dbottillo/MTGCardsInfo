package com.dbottillo.mtgsearchfree.communication;

import com.dbottillo.mtgsearchfree.communication.events.PlayersEvent;
import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper;
import com.dbottillo.mtgsearchfree.database.PlayerDataSource;

import de.greenrobot.event.EventBus;

class PlayersOperation extends Operation {

    @Override
    protected void execute(MTGDatabaseHelper helper, CardsInfoDbHelper cardsInfoDbHelper, Object... params) {
        PlayersEvent cardsEvent = new PlayersEvent(PlayerDataSource.getPlayers(cardsInfoDbHelper.getReadableDatabase()));
        EventBus.getDefault().postSticky(cardsEvent);
    }
}

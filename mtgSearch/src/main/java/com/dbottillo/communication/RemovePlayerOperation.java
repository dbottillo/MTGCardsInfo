package com.dbottillo.communication;

import com.dbottillo.communication.events.PlayersEvent;
import com.dbottillo.database.CardsInfoDbHelper;
import com.dbottillo.database.MTGDatabaseHelper;
import com.dbottillo.database.PlayerDataSource;
import com.dbottillo.resources.Player;

import de.greenrobot.event.EventBus;

class RemovePlayerOperation extends Operation {

    @Override
    protected void execute(MTGDatabaseHelper helper, CardsInfoDbHelper cardsInfoDbHelper, Object... params) {
        PlayerDataSource.removePlayer(cardsInfoDbHelper.getWritableDatabase(), (Player) params[0]);
        PlayersEvent playersEvent = new PlayersEvent(PlayerDataSource.getPlayers(cardsInfoDbHelper.getReadableDatabase()));
        EventBus.getDefault().postSticky(playersEvent);
    }
}

package com.dbottillo.mtgsearchfree.communication;

import com.dbottillo.mtgsearchfree.communication.events.PlayersEvent;
import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper;
import com.dbottillo.mtgsearchfree.database.PlayerDataSource;
import com.dbottillo.mtgsearchfree.resources.Player;

import de.greenrobot.event.EventBus;

class SavePlayerOperation extends Operation {

    @Override
    protected void execute(MTGDatabaseHelper helper, CardsInfoDbHelper cardsInfoDbHelper, Object... params) {
        PlayerDataSource.savePlayer(cardsInfoDbHelper.getWritableDatabase(), (Player) params[0]);
        PlayersEvent playersEvent = new PlayersEvent(PlayerDataSource.getPlayers(cardsInfoDbHelper.getReadableDatabase()));
        EventBus.getDefault().postSticky(playersEvent);
    }
}

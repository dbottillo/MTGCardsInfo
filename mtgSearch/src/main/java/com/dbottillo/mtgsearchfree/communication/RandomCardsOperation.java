package com.dbottillo.mtgsearchfree.communication;

import com.dbottillo.mtgsearchfree.communication.events.RandomCardsEvent;
import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper;
import com.dbottillo.mtgsearchfree.resources.MTGCard;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

class RandomCardsOperation extends Operation {

    @Override
    protected void execute(MTGDatabaseHelper helper, CardsInfoDbHelper cardsInfoDbHelper, Object... params) {
        ArrayList<MTGCard> result = helper.getRandomCard((Integer) params[0]);
        RandomCardsEvent randomCardsEvent = new RandomCardsEvent(result);
        EventBus.getDefault().postSticky(randomCardsEvent);
    }
}

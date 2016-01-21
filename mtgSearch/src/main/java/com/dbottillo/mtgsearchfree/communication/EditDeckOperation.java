package com.dbottillo.mtgsearchfree.communication;

import com.dbottillo.mtgsearchfree.communication.events.DeckEvent;
import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.database.DeckDataSource;
import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper;
import com.dbottillo.mtgsearchfree.resources.MTGCard;

import de.greenrobot.event.EventBus;

class EditDeckOperation extends Operation {

    @Override
    protected void execute(MTGDatabaseHelper helper, CardsInfoDbHelper cardsInfoDbHelper, Object... params) {
        boolean add = (boolean) params[0];
        if (add) {
            DeckDataSource.addCardToDeck(cardsInfoDbHelper.getWritableDatabase(),
                    (long) params[1], (MTGCard) params[2], (int) params[3], (boolean) params[4]);
        } else {
            DeckDataSource.removeCardFromDeck(cardsInfoDbHelper.getWritableDatabase(),
                    (long) params[1], (MTGCard) params[2], (boolean) params[3]);
        }
        EventBus.getDefault().postSticky(new DeckEvent());
    }
}

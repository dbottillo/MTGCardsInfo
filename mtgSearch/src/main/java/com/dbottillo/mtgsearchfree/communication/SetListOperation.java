package com.dbottillo.mtgsearchfree.communication;

import com.dbottillo.mtgsearchfree.communication.events.SetEvent;
import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper;
import com.dbottillo.mtgsearchfree.resources.MTGSet;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

class SetListOperation extends Operation {

    @Override
    protected void execute(MTGDatabaseHelper helper, CardsInfoDbHelper cardsInfoDbHelper, Object... params) {
        ArrayList<MTGSet> result = helper.getSets();
        SetEvent setEvent = new SetEvent(result);
        EventBus.getDefault().postSticky(setEvent);
    }
}

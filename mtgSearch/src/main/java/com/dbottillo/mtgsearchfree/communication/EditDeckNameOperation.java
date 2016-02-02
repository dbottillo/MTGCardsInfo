package com.dbottillo.mtgsearchfree.communication;

import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.mtgsearchfree.communication.events.DeckEvent;
import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.database.DeckDataSource;
import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper;

import de.greenrobot.event.EventBus;

class EditDeckNameOperation extends Operation {

    @Override
    protected void execute(MTGDatabaseHelper helper, CardsInfoDbHelper cardsInfoDbHelper, Object... params) {
        SQLiteDatabase db = cardsInfoDbHelper.getWritableDatabase();
        DeckDataSource.renameDeck(db, (long) params[0], (String) params[1]);
        EventBus.getDefault().postSticky(new DeckEvent());
    }
}

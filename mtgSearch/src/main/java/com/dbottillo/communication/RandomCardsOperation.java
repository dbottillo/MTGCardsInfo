package com.dbottillo.communication;

import android.database.Cursor;

import com.dbottillo.communication.events.RandomCardsEvent;
import com.dbottillo.database.CardsInfoDbHelper;
import com.dbottillo.database.MTGDatabaseHelper;
import com.dbottillo.resources.MTGCard;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

class RandomCardsOperation extends Operation {

    @Override
    protected void execute(MTGDatabaseHelper helper, CardsInfoDbHelper cardsInfoDbHelper, Object... params) {
        ArrayList<MTGCard> result = new ArrayList<>();
        Cursor cursor = helper.getRandomCard((Integer) params[0]);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                result.add(MTGCard.createCardFromCursor(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        RandomCardsEvent randomCardsEvent = new RandomCardsEvent(result);
        EventBus.getDefault().postSticky(randomCardsEvent);
    }
}

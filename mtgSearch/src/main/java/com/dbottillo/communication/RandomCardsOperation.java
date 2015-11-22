package com.dbottillo.communication;

import android.database.Cursor;

import com.dbottillo.communication.events.CardsEvent;
import com.dbottillo.communication.events.RandomCardsEvent;
import com.dbottillo.database.CardsDatabaseHelper;
import com.dbottillo.resources.MTGCard;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

class RandomCardsOperation extends Operation {

    @Override
    protected void execute(CardsDatabaseHelper helper, Object... params) {
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

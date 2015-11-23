package com.dbottillo.communication;

import android.database.Cursor;

import com.dbottillo.communication.events.CardsEvent;
import com.dbottillo.database.CardsInfoDbHelper;
import com.dbottillo.database.MTGDatabaseHelper;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.search.SearchParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.greenrobot.event.EventBus;

class SearchCardsOperation extends Operation {

    @Override
    protected void execute(MTGDatabaseHelper helper, CardsInfoDbHelper cardsInfoDbHelper, Object... params) {
        ArrayList<MTGCard> result = new ArrayList<>();
        SearchParams searchParams = (SearchParams) params[0];
        Cursor cursor = helper.searchCards(searchParams);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                result.add(MTGCard.createCardFromCursor(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();

        Collections.sort(result, new Comparator<Object>() {
            public int compare(Object o1, Object o2) {
                MTGCard card = (MTGCard) o1;
                MTGCard card2 = (MTGCard) o2;
                return card.compareTo(card2);
            }
        });
        CardsEvent cardsEvent = new CardsEvent(result);
        EventBus.getDefault().postSticky(cardsEvent);
    }
}

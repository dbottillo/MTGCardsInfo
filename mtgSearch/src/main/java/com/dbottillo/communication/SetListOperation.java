package com.dbottillo.communication;

import android.database.Cursor;

import com.dbottillo.communication.events.SetEvent;
import com.dbottillo.database.CardsDatabaseHelper;
import com.dbottillo.resources.MTGSet;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

class SetListOperation extends Operation {

    @Override
    protected void execute(CardsDatabaseHelper helper, Object... params) {
        ArrayList<MTGSet> result = new ArrayList<>();
        Cursor cursor = helper.getSets();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                result.add(MTGSet.createMagicSetFromCursor(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        SetEvent setEvent = new SetEvent(result);
        EventBus.getDefault().postSticky(setEvent);
    }
}

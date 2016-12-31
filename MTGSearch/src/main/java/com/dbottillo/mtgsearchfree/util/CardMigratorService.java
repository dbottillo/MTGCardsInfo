package com.dbottillo.mtgsearchfree.util;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.model.database.CardDataSource;
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource;
import com.dbottillo.mtgsearchfree.model.database.MTGDatabaseHelper;
import com.google.gson.Gson;

import java.util.List;

public class CardMigratorService extends IntentService {

    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;

    int id = 107;

    public CardMigratorService() {
        super("CardMigratorService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(getString(R.string.card_migrator_notification_title))
                .setSmallIcon(R.drawable.ic_stat_notification_generic);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LOG.e("started");

        CardDataSource cardDataSource = new CardDataSource(new CardsInfoDbHelper(getApplicationContext()).getWritableDatabase(), new Gson());
        MTGCardDataSource mtgCardDataSource = new MTGCardDataSource(new MTGDatabaseHelper(getApplicationContext()).getReadableDatabase(), cardDataSource);
        List<MTGCard> cards = cardDataSource.getCards();

        for (int i=0; i<cards.size(); i++){
            showNotification(i, cards.size());
            MTGCard card = cards.get(i);
            MTGCard fromMTG = mtgCardDataSource.searchCard(card.getMultiVerseId());
            if (fromMTG == null){
                List<MTGCard> searchCards = mtgCardDataSource.searchCards(new SearchParams().setName(card.getName()));
                if (searchCards != null && searchCards.size() > 0) {
                    fromMTG = searchCards.get(0);
                }
            }
            if (fromMTG != null) {
                cardDataSource.removeCard(card);
                cardDataSource.saveCard(fromMTG);
            }
        }

        mBuilder.setContentText(getString(R.string.card_migrator_finished))
                // Removes the progress bar
                .setProgress(0,0,false);
        mNotifyManager.notify(id, mBuilder.setOngoing(false).build());
    }

    private void showNotification(int current, int total){
        mBuilder.setProgress(total, current, false);
        // Displays the progress bar for the first time.
        mNotifyManager.notify(id, mBuilder.setOngoing(true).build());
    }

}

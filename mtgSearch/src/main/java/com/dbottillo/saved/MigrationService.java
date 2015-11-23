package com.dbottillo.saved;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.dbottillo.R;
import com.dbottillo.database.CardsInfoDbHelper;
import com.dbottillo.database.DB40Helper;
import com.dbottillo.database.FavouritesDataSource;
import com.dbottillo.database.PlayerDataSource;
import com.dbottillo.persistence.MigrationPreferences;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.resources.Player;

import java.util.ArrayList;

public class MigrationService extends IntentService {

    private static final int NOTIFICATION_MIGRATION_ID = 899;

    public MigrationService() {
        super("MigrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_stat_notification_generic)
                .setContentTitle(getString(R.string.favourite_migration_in_progress_title))
                .setContentText(getString(R.string.favourite_migration_in_progress))
                .setColor(getResources().getColor(R.color.color_primary_dark))
                .setOngoing(true);

        showNotification(mBuilder);

        DB40Helper.init(getApplicationContext());
        DB40Helper.openDb();

        // ******** fake loading favourites
        /*ArrayList<MTGCard> result = new ArrayList<>();
        MTGDatabaseHelper mtgDatabaseHelper = new MTGDatabaseHelper(getApplicationContext());
        Cursor cursor = mtgDatabaseHelper.getRandomCard(500);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                DB40Helper.storeCard(MTGCard.createCardFromCursor(cursor));
                //result.add(MTGCard.createCardFromCursor(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();*/
        //********  end fake loading favourites


        // ******** player migration
        ArrayList<Player> players = DB40Helper.getPlayers();
        CardsInfoDbHelper cardsInfoDbHelper = CardsInfoDbHelper.getInstance(getApplicationContext());
        for (Player player : players) {
            PlayerDataSource.savePlayer(cardsInfoDbHelper.getWritableDatabase(), player);
        }
        // ******** end player migration


        // ******** cards migration
        ArrayList<MTGCard> cards = DB40Helper.getCards();
        DB40Helper.closeDb();
        int count = 0;
        for (MTGCard card : cards) {
            mBuilder.setProgress(cards.size(), count, false);
            showNotification(mBuilder);
            FavouritesDataSource.saveFavourites(cardsInfoDbHelper.getWritableDatabase(), card);
            count++;
        }
        // ******** end cards migration

        cancelNotification();
        MigrationPreferences migrationPreferences = new MigrationPreferences(getApplicationContext());
        migrationPreferences.setFinished();
    }

    private void cancelNotification() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_MIGRATION_ID);
    }

    private void showNotification(NotificationCompat.Builder builder) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_MIGRATION_ID, builder.build());
    }

}

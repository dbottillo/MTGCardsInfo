package com.dbottillo.mtgsearchfree.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.cards.CardLuckyActivity;
import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper;
import com.dbottillo.mtgsearchfree.resources.MTGCard;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UpdateLuckyWidgetService extends Service {

    int[] allWidgetIds;
    MTGDatabaseHelper mtgDatabaseHelper;

    @Override
    public void onStart(Intent intent, int startId) {
        if (intent != null) {
            allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            mtgDatabaseHelper = new MTGDatabaseHelper(getApplicationContext());
            new LuckyAsyncTask().execute(allWidgetIds.length);
        }
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onTaskFinished(ArrayList<MTGCard> objects) {
        int index = 0;

        AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());

        ComponentName thisWidget = new ComponentName(getApplicationContext(), LuckyWidgetProvider.class);
        RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.lucky_widget_layout);

        for (int widgetId : allWidgetIds) {
            if (objects.size() > index) {

                MTGCard card = objects.get(index);

                if (card.getImage() != null) {
                    Picasso.with(getApplicationContext()).load(card.getImage()).into(remoteViews, R.id.image_card, new int[]{widgetId});
                }
                index++;

                Intent openIntent = new Intent(getApplicationContext(), CardLuckyActivity.class);
                openIntent.putExtra(CardLuckyActivity.CARD, card);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.image_card, pendingIntent);

                manager.updateAppWidget(thisWidget, remoteViews);
            }
        }
        stopSelf();
    }


    class LuckyAsyncTask extends AsyncTask<Integer, Void, ArrayList<MTGCard>> {

        @Override
        protected ArrayList<MTGCard> doInBackground(Integer... params) {
            return mtgDatabaseHelper.getRandomCard(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<MTGCard> result) {
            onTaskFinished(result);
        }
    }
}

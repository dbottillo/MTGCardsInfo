package com.dbottillo.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.widget.RemoteViews;

import com.dbottillo.R;
import com.dbottillo.cards.CardLuckyActivity;
import com.dbottillo.helper.DBAsyncTask;
import com.dbottillo.resources.MTGCard;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UpdateLuckyWidgetService extends Service implements DBAsyncTask.DBAsyncTaskListener {

    int[] allWidgetIds;


    @Override
    public void onStart(Intent intent, int startId) {
        allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        new DBAsyncTask(getApplicationContext(), this, DBAsyncTask.TASK_RANDOM_CARD).execute(allWidgetIds.length);
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskFinished(int type, ArrayList<?> objects) {
        int index = 0;

        AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());

        ComponentName thisWidget = new ComponentName(getApplicationContext(), LuckyWidgetProvider.class);
        RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.lucky_widget_layout);

        for (int widgetId : allWidgetIds) {
            if (objects.size() > index) {

                MTGCard card = (MTGCard) objects.get(index);

                Picasso.with(getApplicationContext()).load(card.getImage()).into(remoteViews, R.id.image_card, new int[]{widgetId});
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

    @Override
    public void onTaskEndWithError(int type, String error) {
        stopSelf();
    }
}

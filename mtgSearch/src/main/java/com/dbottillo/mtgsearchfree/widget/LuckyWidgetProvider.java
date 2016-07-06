package com.dbottillo.mtgsearchfree.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.util.TrackingManager;

public class LuckyWidgetProvider extends AppWidgetProvider {

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        TrackingManager.init(context);
        TrackingManager.trackDeleteWidget();
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        TrackingManager.init(context);
        TrackingManager.trackAddWidget();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Get all ids
        ComponentName thisWidget = new ComponentName(context, LuckyWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.lucky_widget_layout);

        for (int widgetId : allWidgetIds) {
            Intent updateIntent = new Intent(context, LuckyWidgetReceiver.class);
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int[] ids = {widgetId};
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, updateIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.lucky_widget_more, pendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.image_card, pendingIntent);
        }

        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(thisWidget, remoteViews);

        Intent intent = new Intent(context.getApplicationContext(), UpdateLuckyWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

        context.startService(intent);
    }
}

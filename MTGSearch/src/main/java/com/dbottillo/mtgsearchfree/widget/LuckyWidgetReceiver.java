package com.dbottillo.mtgsearchfree.widget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LuckyWidgetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent updateIntent = new Intent(context, LuckyWidgetProvider.class);
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intent.getExtras().getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS));
        context.sendBroadcast(updateIntent);
    }
}

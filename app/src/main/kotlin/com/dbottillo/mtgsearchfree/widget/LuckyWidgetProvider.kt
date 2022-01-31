package com.dbottillo.mtgsearchfree.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.bumptech.glide.request.target.AppWidgetTarget
import com.dbottillo.mtgsearchfree.Constants.RATIO_CARD
import com.dbottillo.mtgsearchfree.GlideApp
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.database.CardDataSource
import com.dbottillo.mtgsearchfree.database.MTGCardDataSource
import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper
import com.dbottillo.mtgsearchfree.lucky.CARD
import com.dbottillo.mtgsearchfree.lucky.CardLuckyActivity
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.google.gson.Gson

class LuckyWidgetProvider : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent?.action.equals(REFRESH_ACTION)) {
            val manager = AppWidgetManager.getInstance(context)
            val cn = ComponentName(context, LuckyWidgetProvider::class.java)
            val appWidgetIds = intent?.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )?.let { intArrayOf(it) }
                ?: manager.getAppWidgetIds(cn)
            onUpdate(context, manager, appWidgetIds)
        }

        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val database = MTGDatabaseHelper(context).readableDatabase
        val cardDataSource = MTGCardDataSource(database, CardDataSource(database, Gson()))

        for (widgetId in appWidgetIds) {
            val layout = buildLayout(context, widgetId, cardDataSource.getRandomCard(1)[0])
            appWidgetManager.updateAppWidget(widgetId, layout)
        }

        database.close()

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    private fun buildLayout(context: Context, appWidgetId: Int, card: MTGCard): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.lucky_widget_layout)
        val width = context.resources.getDimensionPixelSize(R.dimen.widget_min_width)
        GlideApp
            .with(context.applicationContext)
            .asBitmap()
            .load(card.scryfallImage)
            .override(width, (width * RATIO_CARD).toInt())
            .into(AppWidgetTarget(context, R.id.image_card, remoteViews, appWidgetId))

        // refresh
        val refreshIntent = Intent(context, LuckyWidgetProvider::class.java)
        refreshIntent.action = REFRESH_ACTION
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        val refreshPendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            refreshIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        remoteViews.setOnClickPendingIntent(R.id.lucky_widget_more, refreshPendingIntent)

        // tap on image
        val openIntent = Intent(context, CardLuckyActivity::class.java)
        openIntent.putExtra(CARD, card.id)
        val pendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        remoteViews.setOnClickPendingIntent(R.id.image_card, pendingIntent)

        return remoteViews
    }
}

const val REFRESH_ACTION = "com.dbottillo.mtgsearchfree.luckywidgetprovider.REFRESH"

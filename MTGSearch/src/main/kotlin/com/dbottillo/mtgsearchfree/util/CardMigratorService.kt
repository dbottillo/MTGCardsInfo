package com.dbottillo.mtgsearchfree.util

import android.app.IntentService
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat

import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.database.CardDataSource
import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper
import com.dbottillo.mtgsearchfree.database.MTGCardDataSource
import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper
import com.google.gson.Gson

class CardMigratorService : IntentService("CardMigratorService") {

    lateinit var mNotifyManager: NotificationManager
    lateinit var mBuilder: NotificationCompat.Builder

    internal var id = 107

    override fun onCreate() {
        super.onCreate()

        mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // TODO: before re-using this class it needs to create a channel
        mBuilder = NotificationCompat.Builder(this, "CHANNEL")
        mBuilder.setContentTitle(getString(R.string.card_migrator_notification_title))
                .setSmallIcon(R.drawable.ic_stat_notification_generic)
    }

    override fun onHandleIntent(intent: Intent?) {
        LOG.e("started")

        val cardDataSource = CardDataSource(CardsInfoDbHelper(applicationContext).writableDatabase, Gson())
        val mtgCardDataSource = MTGCardDataSource(MTGDatabaseHelper(applicationContext).readableDatabase, cardDataSource)
        val cards = cardDataSource.cards

        for (i in cards.indices) {
            showNotification(i, cards.size)
            val card = cards[i]
            var fromMTG = mtgCardDataSource.searchCard(card.multiVerseId)
            if (fromMTG == null) {
                val searchCards = mtgCardDataSource.searchCards(SearchParams(name = card.name))
                if (searchCards.isNotEmpty()) {
                    fromMTG = searchCards[0]
                }
            }
            if (fromMTG != null) {
                cardDataSource.removeCard(card)
                cardDataSource.saveCard(fromMTG)
            }
        }

        mBuilder.setContentText(getString(R.string.card_migrator_finished))
                // Removes the progress bar
                .setProgress(0, 0, false)
        mNotifyManager.notify(id, mBuilder.setOngoing(false).build())
    }

    private fun showNotification(current: Int, total: Int) {
        mBuilder.setProgress(total, current, false)
        // Displays the progress bar for the first time.
        mNotifyManager.notify(id, mBuilder.setOngoing(true).build())
    }
}

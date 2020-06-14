package com.dbottillo.mtgsearchfree.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Event.VIEW_ITEM
import com.google.firebase.crashlytics.FirebaseCrashlytics

class TrackingManagerImpl constructor(
    val context: Context
) : TrackingManager {

    private val UA_CATEGORY_UI = "ui"
    private val UA_CATEGORY_POPUP = "popup"
    private val UA_CATEGORY_SET = "set"
    private val UA_CATEGORY_CARD = "card"
    private val UA_CATEGORY_SEARCH = "search"
    private val UA_CATEGORY_FILTER = "filter"
    private val UA_CATEGORY_FAVOURITE = "favourite"
    private val UA_CATEGORY_DECK = "deck"
    private val UA_CATEGORY_LIFE_COUNTER = "lifeCounter"
    private val UA_CATEGORY_ERROR = "error"

    private val UA_CATEGORY_APP_WIDGET = "appWidget"
    private val UA_CATEGORY_RELEASE_NOTE = "releaseNote"

    private val UA_ACTION_TOGGLE = "toggle"
    private val UA_ACTION_SHARE = "share"
    private val UA_ACTION_SELECT = "select"
    private val UA_ACTION_OPEN = "open"
    private val UA_ACTION_CLOSE = "close"
    private val UA_ACTION_SAVE = "saved"
    private val UA_ACTION_ADD_CARD = "addCard"
    private val UA_ACTION_UNSAVED = "unsaved"
    private val UA_ACTION_LUCKY = "lucky"
    private val UA_ACTION_RATE = "rate"
    private val UA_ACTION_DELETE = "delete"
    private val UA_ACTION_EXTERNAL_LINK = "externalLink"
    private val UA_ACTION_ONE_MORE = "oneMore"
    private val UA_ACTION_REMOVE_ONE = "removeOne"
    private val UA_ACTION_REMOVE_ALL = "removeALL"
    private val UA_ACTION_MOVE_ONE = "moveOne"
    private val UA_ACTION_MOVE_ALL = "moveAll"
    private val UA_ACTION_EXPORT = "export"

    override fun trackCard(setName: String, position: Int) {
        trackEvent(UA_CATEGORY_CARD, UA_ACTION_SELECT, "$setName pos:$position")
    }

    @SuppressLint("MissingPermission")
    override fun trackPage(page: String?) {
        if (page != null) {
            val bundle = Bundle().also { it.putString(FirebaseAnalytics.Param.ITEM_NAME, page) }
            FirebaseAnalytics.getInstance(context).logEvent(VIEW_ITEM, bundle)
        }
    }

    private fun trackEvent(category: String?, action: String?, label: String? = "") {
        val bundle = Bundle().also {
            it.putString("category", category)
            it.putString("action", action)
            it.putString("label", label)
        }
        FirebaseAnalytics.getInstance(context).logEvent("event", bundle)
    }

    override fun trackImage(url: String?) {
        val bundle = Bundle().also {
            it.putString("type", if (url?.contains("gatherer") == true) "gatherer" else "cardsInfo")
            it.putString("url", url)
        }
        FirebaseAnalytics.getInstance(context).logEvent("Image", bundle)
    }

    /*override fun trackSet(gameSet: MTGSet, mtgSet: MTGSet) {
        trackEvent(UA_CATEGORY_SET, UA_ACTION_SELECT, mtgSet.code)
    }*/

    override fun trackShareApp() {
        trackEvent(UA_CATEGORY_UI, UA_ACTION_SHARE, "app")
    }

    override fun trackAboutLibrary(libraryLink: String?) {
        trackEvent(UA_CATEGORY_UI, UA_ACTION_EXTERNAL_LINK, libraryLink)
    }

    override fun trackPriceError(url: String?) {
        trackEvent(UA_CATEGORY_ERROR, "price", url)
    }

    override fun trackImageError(image: String?) {
        trackEvent(UA_CATEGORY_ERROR, "image", image)
    }

    override fun trackShareCard(cardName: String?) {
        if (cardName != null) {
            trackEvent(UA_CATEGORY_CARD, UA_ACTION_SHARE, cardName)
        }
    }

    override fun trackReleaseNote() {
        trackEvent(UA_CATEGORY_RELEASE_NOTE, UA_ACTION_OPEN, "update")
    }

    override fun trackSortCard(color: Boolean) {
        trackEvent(UA_CATEGORY_SET, UA_ACTION_TOGGLE, if (color) "wubrg" else "alphabetically")
    }

    override fun trackDatabaseExport() {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_EXPORT)
    }

    override fun trackDatabaseExportError(error: String) {
        trackEvent(UA_CATEGORY_ERROR, UA_ACTION_EXPORT, "[deck] " + error)
    }

    override fun trackOpenRateApp() {
        trackEvent(UA_CATEGORY_UI, UA_ACTION_RATE, "google")
    }

    override fun trackEditDeck() {
        trackEvent(UA_CATEGORY_DECK, "editName")
    }

    override fun trackDeckExport() {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_SHARE)
    }

    override fun trackDeckExportError() {
        trackEvent(UA_CATEGORY_ERROR, UA_ACTION_EXPORT, "[deck] impossible to create folder")
    }

    override fun trackAddCardToDeck() {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_ONE_MORE)
    }

    override fun trackAddCardToDeck(quantity: String) {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_ADD_CARD, quantity)
    }

    override fun trackRemoveCardFromDeck() {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_REMOVE_ONE)
    }

    override fun trackRemoveAllCardsFromDeck() {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_REMOVE_ALL)
    }

    override fun trackMoveOneCardFromDeck() {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_MOVE_ONE)
    }

    override fun trackMoveAllCardFromDeck() {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_MOVE_ALL)
    }

    override fun trackSearch(searchParams: String?) {
        trackEvent(UA_CATEGORY_SEARCH, "done", searchParams ?: "null")
    }

    override fun trackOpenFeedback() {
        trackEvent(UA_CATEGORY_UI, UA_ACTION_OPEN, "feedback")
    }

    override fun trackNewDeck(deck: String) {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_SAVE, deck)
    }

    override fun trackDeleteDeck(name: String) {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_DELETE, name)
    }

    override fun trackAddPlayer() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "addPlayer")
    }

    override fun trackResetLifeCounter() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "resetLifeCounter")
    }

    override fun trackLunchDice() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "launchDice")
    }

    override fun trackChangePoisonSetting() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "poisonSetting")
    }

    override fun trackHGLifeCounter() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "two_hg")
    }

    override fun trackOpenCard(position: Int) {
        trackEvent(UA_CATEGORY_CARD, UA_ACTION_OPEN, "saved pos:" + position)
    }

    override fun trackSearchError(message: String?) {
        trackEvent(UA_CATEGORY_ERROR, "saved-main", message)
    }

    override fun trackDeleteWidget() {
        trackEvent(UA_CATEGORY_APP_WIDGET, "deleted")
    }

    override fun trackAddWidget() {
        trackEvent(UA_CATEGORY_APP_WIDGET, "enabled")
    }

    override fun trackEditPlayer() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "editPlayer")
    }

    override fun trackLifeCountChanged() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "lifeCountChanged")
    }

    override fun trackPoisonCountChanged() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "poisonCountChange")
    }

    override fun trackScreenOn() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "screenOn")
    }

    override fun trackRemovePlayer() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "removePlayer")
    }

    override fun logOnCreate(message: String) {
        FirebaseCrashlytics.getInstance().log(message)
    }
}

package com.dbottillo.mtgsearchfree.util

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.crashlytics.android.answers.CustomEvent

object TrackingManager {

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

    fun trackCard(setName: String, position: Int) {
        trackEvent(UA_CATEGORY_CARD, UA_ACTION_SELECT, "$setName pos:$position")
    }

    fun trackPage(page: String?) {
        if (page != null) {
            Answers.getInstance().logContentView(ContentViewEvent().putContentName(page))
        }
    }

    private fun trackEvent(category: String?, action: String?, label: String? = "") {
        Answers.getInstance().logCustom(CustomEvent("event")
                .putCustomAttribute("category", category)
                .putCustomAttribute("action", action)
                .putCustomAttribute("label", label))
    }

    fun trackImage(url: String?) {
        Answers.getInstance().logCustom(CustomEvent("Image")
                .putCustomAttribute("type", if (url?.contains("gatherer") == true) "gatherer" else "cardsInfo")
                .putCustomAttribute("url", url))
    }

    /*fun trackSet(gameSet: MTGSet, mtgSet: MTGSet) {
        trackEvent(UA_CATEGORY_SET, UA_ACTION_SELECT, mtgSet.code)
    }*/

    fun trackShareApp() {
        trackEvent(UA_CATEGORY_UI, UA_ACTION_SHARE, "app")
    }

    fun trackAboutLibrary(libraryLink: String?) {
        trackEvent(UA_CATEGORY_UI, UA_ACTION_EXTERNAL_LINK, libraryLink)
    }

    fun trackPriceError(url: String?) {
        trackEvent(UA_CATEGORY_ERROR, "price", url)
    }

    fun trackImageError(image: String?) {
        trackEvent(UA_CATEGORY_ERROR, "image", image)
    }

    fun trackShareCard(cardName: String?) {
        if (cardName != null) {
            trackEvent(UA_CATEGORY_CARD, UA_ACTION_SHARE, cardName)
        }
    }

    fun trackReleaseNote() {
        trackEvent(UA_CATEGORY_RELEASE_NOTE, UA_ACTION_OPEN, "update")
    }

    fun trackSortCard(color: Boolean) {
        trackEvent(UA_CATEGORY_SET, UA_ACTION_TOGGLE, if (color) "wubrg" else "alphabetically")
    }

    fun trackDatabaseExport() {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_EXPORT)
    }

    fun trackDatabaseExportError(error: String) {
        trackEvent(UA_CATEGORY_ERROR, UA_ACTION_EXPORT, "[deck] " + error)
    }

    fun trackOpenRateApp() {
        trackEvent(UA_CATEGORY_UI, UA_ACTION_RATE, "google")
    }

    fun trackEditDeck() {
        trackEvent(UA_CATEGORY_DECK, "editName")
    }

    fun trackDeckExport() {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_SHARE)
    }

    fun trackDeckExportError() {
        trackEvent(UA_CATEGORY_ERROR, UA_ACTION_EXPORT, "[deck] impossible to create folder")
    }

    fun trackAddCardToDeck() {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_ONE_MORE)
    }

    fun trackAddCardToDeck(quantity: String) {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_ADD_CARD, quantity)
    }

    fun trackRemoveCardFromDeck() {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_REMOVE_ONE)
    }

    fun trackRemoveAllCardsFromDeck() {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_REMOVE_ALL)
    }

    fun trackMoveOneCardFromDeck() {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_MOVE_ONE)
    }

    fun trackMoveAllCardFromDeck() {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_MOVE_ALL)
    }

    fun trackSearch(searchParams: String?) {
        trackEvent(UA_CATEGORY_SEARCH, "done", searchParams ?: "null")
    }

    fun trackOpenFeedback() {
        trackEvent(UA_CATEGORY_UI, UA_ACTION_OPEN, "feedback")
    }

    fun trackNewDeck(deck: String) {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_SAVE, deck)
    }

    fun trackDeleteDeck(name: String) {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_DELETE, name)
    }

    fun trackAddPlayer() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "addPlayer")
    }

    fun trackResetLifeCounter() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "resetLifeCounter")
    }

    fun trackLunchDice() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "launchDice")
    }

    fun trackChangePoisonSetting() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "poisonSetting")
    }

    fun trackHGLifeCounter() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "two_hg")
    }

    fun trackOpenCard(position: Int) {
        trackEvent(UA_CATEGORY_CARD, UA_ACTION_OPEN, "saved pos:" + position)
    }

    fun trackSearchError(message: String?) {
        trackEvent(UA_CATEGORY_ERROR, "saved-main", message)
    }

    fun trackDeleteWidget() {
        trackEvent(UA_CATEGORY_APP_WIDGET, "deleted")
    }

    fun trackAddWidget() {
        trackEvent(UA_CATEGORY_APP_WIDGET, "enabled")
    }

    fun trackEditPlayer() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "editPlayer")
    }

    fun trackLifeCountChanged() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "lifeCountChanged")
    }

    fun trackPoisonCountChanged() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "poisonCountChange")
    }

    fun trackScreenOn() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "screenOn")
    }

    fun trackRemovePlayer() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "removePlayer")
    }

    fun logOnCreate(message: String) {
        Crashlytics.log(message)
    }
}

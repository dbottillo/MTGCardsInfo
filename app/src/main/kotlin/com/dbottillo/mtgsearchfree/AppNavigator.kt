package com.dbottillo.mtgsearchfree

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.dbottillo.mtgsearchfree.about.AboutActivity
import com.dbottillo.mtgsearchfree.decks.DecksFragment
import com.dbottillo.mtgsearchfree.decks.addToDeck.AddToDeckFragment
import com.dbottillo.mtgsearchfree.lifecounter.LifeCounterFragment
import com.dbottillo.mtgsearchfree.lucky.CardLuckyActivity
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.model.helper.AddFavouritesAsyncTask
import com.dbottillo.mtgsearchfree.model.helper.CreateDBAsyncTask
import com.dbottillo.mtgsearchfree.model.helper.CreateDecksAsyncTask
import com.dbottillo.mtgsearchfree.releasenote.ReleaseNoteActivity
import com.dbottillo.mtgsearchfree.ui.cards.CardsActivity
import com.dbottillo.mtgsearchfree.ui.cards.KEY_DECK
import com.dbottillo.mtgsearchfree.ui.cards.POSITION
import com.dbottillo.mtgsearchfree.ui.saved.SavedFragment
import com.dbottillo.mtgsearchfree.sets.SetsFragment
import com.dbottillo.mtgsearchfree.ui.cards.KEY_SET
import com.dbottillo.mtgsearchfree.search.SearchActivity
import com.dbottillo.mtgsearchfree.ui.cards.KEY_SEARCH

class AppNavigator : Navigator {
    override fun openAboutScreen(origin: Activity) = origin.startActivity(Intent(origin, AboutActivity::class.java))
    override fun openReleaseNoteScreen(origin: Activity) = origin.startActivity(Intent(origin, ReleaseNoteActivity::class.java))
    override fun openSearchScreen(origin: Activity) = origin.startActivity(Intent(origin, SearchActivity::class.java))
    override fun openCardsLuckyScreen(origin: Activity) = origin.startActivity(Intent(origin, CardLuckyActivity::class.java))
    override fun openCardsScreen(origin: Activity, deck: Deck, position: Int) {
        origin.startActivity(Intent(origin, CardsActivity::class.java).also {
            it.putExtra(POSITION, position)
            it.putExtra(KEY_DECK, deck.id)
        })
    }
    override fun openCardsScreen(origin: Activity, set: MTGSet, position: Int) {
        origin.startActivity(Intent(origin, CardsActivity::class.java).also {
            it.putExtra(POSITION, position)
            it.putExtra(KEY_SET, set)
        })
    }
    override fun openCardsScreen(origin: Activity, search: SearchParams, position: Int) {
        origin.startActivity(Intent(origin, CardsActivity::class.java).also {
            it.putExtra(POSITION, position)
            it.putExtra(KEY_SEARCH, search)
        })
    }
    override fun newLifeCounterFragment() = LifeCounterFragment()
    override fun newSetsCounterFragment() = SetsFragment()
    override fun newDecksFragment() = DecksFragment()
    override fun newSavedFragment() = SavedFragment()
    override fun newAddToDeckFragment(card: MTGCard) = AddToDeckFragment.newInstance(card)
    override fun isSetsFragment(fragment: Fragment?) = fragment is SetsFragment
    override fun createDecks(appContext: Context) {
        CreateDecksAsyncTask(appContext).execute()
    }
    override fun createFavourites(appContext: Context) {
        AddFavouritesAsyncTask(appContext).execute()
    }

    override fun createDatabase(applicationContext: Context, packageName: String) {
        CreateDBAsyncTask(applicationContext, packageName).execute()
    }
}
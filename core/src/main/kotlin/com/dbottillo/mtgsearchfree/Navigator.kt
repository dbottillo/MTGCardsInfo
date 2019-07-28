package com.dbottillo.mtgsearchfree

import android.app.Activity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.ui.BasicFragment

interface Navigator {
    fun openAboutScreen(origin: Activity)
    fun openReleaseNoteScreen(origin: Activity)
    fun openCardsScreen(origin: Activity, deck: Deck, position: Int)
    fun openCardsScreen(origin: Activity, set: MTGSet, position: Int)
    fun openCardsScreen(origin: Activity, search: SearchParams, position: Int)
    fun openCardsSavedScreen(origin: Activity, position: Int)
    fun openSearchScreen(origin: Activity)
    fun openCardsLuckyScreen(origin: Activity)
    fun newLifeCounterFragment(): BasicFragment
    fun newSetsCounterFragment(): BasicFragment
    fun newDecksFragment(): BasicFragment
    fun newSavedFragment(): BasicFragment
    fun newAddToDeckFragment(card: MTGCard): DialogFragment
    fun isSetsFragment(fragment: Fragment?): Boolean
    fun openDebugScreen(origin: Activity)
}
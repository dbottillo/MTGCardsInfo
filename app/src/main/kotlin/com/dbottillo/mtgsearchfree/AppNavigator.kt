package com.dbottillo.mtgsearchfree

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.dbottillo.mtgsearchfree.about.AboutActivity
import com.dbottillo.mtgsearchfree.lifecounter.LifeCounterFragment
import com.dbottillo.mtgsearchfree.releasenote.ReleaseNoteActivity
import com.dbottillo.mtgsearchfree.ui.decks.DecksFragment
import com.dbottillo.mtgsearchfree.ui.saved.SavedFragment
import com.dbottillo.mtgsearchfree.ui.sets.SetsFragment

class AppNavigator : Navigator {
    override fun openAboutScreen(origin: Activity) = origin.startActivity(Intent(origin, AboutActivity::class.java))
    override fun openReleaseNoteScreen(origin: Activity) = origin.startActivity(Intent(origin, ReleaseNoteActivity::class.java))
    override fun newLifeCounterFragment() = LifeCounterFragment()
    override fun newSetsCounterFragment() = SetsFragment()
    override fun newDecksFragment() = DecksFragment()
    override fun newSavedFragment() = SavedFragment()
    override fun isSetsFragment(fragment: Fragment?) = fragment is SetsFragment
}
package com.dbottillo.mtgsearchfree

import android.app.Activity
import androidx.fragment.app.Fragment
import com.dbottillo.mtgsearchfree.ui.BasicFragment

interface Navigator {
    fun openAboutScreen(origin: Activity)
    fun openReleaseNoteScreen(origin: Activity)
    fun newLifeCounterFragment(): BasicFragment
    fun newSetsCounterFragment(): BasicFragment
    fun newDecksFragment(): BasicFragment
    fun newSavedFragment(): BasicFragment
    fun isSetsFragment(fragment: Fragment?): Boolean
}
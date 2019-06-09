package com.dbottillo.mtgsearchfree

import android.app.Activity
import android.content.Intent
import com.dbottillo.mtgsearchfree.about.AboutActivity
import com.dbottillo.mtgsearchfree.releasenote.ReleaseNoteActivity

class AppNavigator : Navigator {

    override fun openAboutScreen(origin: Activity) {
        origin.startActivity(Intent(origin, AboutActivity::class.java))
    }

    override fun openReleaseNoteScreen(origin: Activity) = origin.startActivity(Intent(origin, ReleaseNoteActivity::class.java))
}
package com.dbottillo.mtgsearchfree

import android.app.Activity
import android.content.Intent
import com.dbottillo.mtgsearchfree.about.AboutActivity

class AppNavigator : Navigator {

    override fun openAboutScreen(origin: Activity) {
        origin.startActivity(Intent(origin, AboutActivity::class.java))
    }
}
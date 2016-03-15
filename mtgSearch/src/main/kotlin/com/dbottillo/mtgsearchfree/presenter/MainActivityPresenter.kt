package com.dbottillo.mtgsearchfree.presenter

import android.content.Intent
import com.dbottillo.mtgsearchfree.view.MainView

class MainActivityPresenter constructor(mainView: MainView) {

    val view = mainView;

    fun checkReleaseNote(intent: Intent) {
        if (intent.hasExtra(MTGApp.INTENT_RELEASE_NOTE_PUSH)) {
            if (intent.getBooleanExtra(MTGApp.INTENT_RELEASE_NOTE_PUSH, false)) {
                this.view.showReleaseNote();
            }
            intent.removeExtra(MTGApp.INTENT_RELEASE_NOTE_PUSH)
        }
    }

}
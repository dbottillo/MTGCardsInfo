package com.dbottillo.mtgsearchfree.presenter;

import android.content.Intent;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.view.MainView;

public class MainActivityPresenter {

    MainView view;

    public MainActivityPresenter(MainView view) {
        this.view = view;
    }

    public void checkReleaseNote(Intent intent) {
        if (intent.hasExtra(MTGApp.INTENT_RELEASE_NOTE_PUSH)) {
            if (intent.getBooleanExtra(MTGApp.INTENT_RELEASE_NOTE_PUSH, false)) {
                this.view.showReleaseNote();
            }
            intent.removeExtra(MTGApp.INTENT_RELEASE_NOTE_PUSH);
        }
    }

}
package com.dbottillo.mtgsearchfree.dagger;

import android.content.SharedPreferences;

import com.dbottillo.mtgsearchfree.base.MTGApp;
import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor;
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor;
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AndroidModule.class, DataModule.class, InteractorsModule.class})
public interface AppComponent {

    CardFilterInteractor getCardFilterInteractor();

    CardsInteractor getCardsInteractor();

    CardFilterStorage getCardFilterStorage();

    CardsStorage getCardsStorage();

    SetsInteractor getSetsInteractor();

    SetsStorage getSetsStorage();

    SharedPreferences getSharedPreferences();

    void inject(MTGApp app);

    void inject(BasicActivity mainActivity);
}

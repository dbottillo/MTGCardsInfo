package com.dbottillo.mtgsearchfree.dagger;

import com.dbottillo.mtgsearchfree.ActivityScope;
import com.dbottillo.mtgsearchfree.view.activities.CardLuckyActivity;
import com.dbottillo.mtgsearchfree.view.activities.CardsActivity;
import com.dbottillo.mtgsearchfree.view.activities.DeckActivity;
import com.dbottillo.mtgsearchfree.view.activities.MainActivity;
import com.dbottillo.mtgsearchfree.view.activities.SearchActivity;
import com.dbottillo.mtgsearchfree.view.fragments.AddToDeckFragment;
import com.dbottillo.mtgsearchfree.view.fragments.DecksFragment;
import com.dbottillo.mtgsearchfree.view.fragments.LifeCounterFragment;
import com.dbottillo.mtgsearchfree.view.fragments.MainFragment;
import com.dbottillo.mtgsearchfree.view.fragments.SavedFragment;

import dagger.Component;

@ActivityScope
@Component(modules = {PresentersModule.class}, dependencies = {AppComponent.class})
public interface DataComponent {
    void inject(MainActivity activity);

    void inject(SearchActivity searchActivity);

    void inject(CardsActivity cardsActivity);

    void inject(CardLuckyActivity luckyActivity);

    void inject(DeckActivity deckActivity);

    void inject(MainFragment mainFragment);

    void inject(SavedFragment savedFragment);

    void inject(LifeCounterFragment lifeCounterFragment);

    void inject(DecksFragment decksFragment);

    void inject(AddToDeckFragment addToDeckFragment);

}
package com.dbottillo.mtgsearchfree.dagger;

import com.dbottillo.mtgsearchfree.ActivityScope;
import com.dbottillo.mtgsearchfree.view.activities.CardLuckyActivity;
import com.dbottillo.mtgsearchfree.view.activities.CardsActivity;
import com.dbottillo.mtgsearchfree.view.fragments.MainFragment;

import dagger.Component;

@ActivityScope
@Component(modules = {PresentersModule.class}, dependencies = {AppComponent.class})
public interface DataComponent {
    void inject(MainActivity activity);

    void inject(CardsActivity cardsActivity);

    void inject(CardLuckyActivity luckyActivity);

    void inject(MainFragment mainFragment);

}
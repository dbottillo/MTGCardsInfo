package com.dbottillo.mtgsearchfree.dagger;

import com.dbottillo.mtgsearchfree.ActivityScope;
import com.dbottillo.mtgsearchfree.ui.BaseHomeFragment;
import com.dbottillo.mtgsearchfree.ui.decks.DecksFragment;
import com.dbottillo.mtgsearchfree.ui.lifecounter.LifeCounterFragment;
import com.dbottillo.mtgsearchfree.ui.saved.SavedFragment;
import com.dbottillo.mtgsearchfree.ui.sets.SetsFragment;
import com.dbottillo.mtgsearchfree.view.activities.BasicActivity;
import com.dbottillo.mtgsearchfree.view.activities.CardLuckyActivity;
import com.dbottillo.mtgsearchfree.view.activities.CardsActivity;
import com.dbottillo.mtgsearchfree.view.activities.CommonCardsActivity;
import com.dbottillo.mtgsearchfree.view.activities.DeckActivity;
import com.dbottillo.mtgsearchfree.view.activities.MainActivity;
import com.dbottillo.mtgsearchfree.view.activities.SearchActivity;
import com.dbottillo.mtgsearchfree.view.fragments.AboutFragment;
import com.dbottillo.mtgsearchfree.view.fragments.AddToDeckFragment;
import com.dbottillo.mtgsearchfree.view.fragments.MainFragment;
import com.dbottillo.mtgsearchfree.view.fragments.OldDecksFragment;
import com.dbottillo.mtgsearchfree.view.fragments.OldLifeCounterFragment;
import com.dbottillo.mtgsearchfree.view.fragments.OldSavedFragment;
import com.dbottillo.mtgsearchfree.view.fragments.SortDialogFragment;
import com.dbottillo.mtgsearchfree.view.views.MTGCardView;

import dagger.Component;

@ActivityScope
@Component(modules = {PresentersModule.class, InteractorsModule.class}, dependencies = {AppComponent.class})
public interface UiComponent {

    void inject(CommonCardsActivity activity);

    void inject(BasicActivity activity);

    void inject(MainActivity activity);

    void inject(SearchActivity searchActivity);

    void inject(CardsActivity cardsActivity);

    void inject(CardLuckyActivity luckyActivity);

    void inject(DeckActivity deckActivity);

    void inject(MainFragment mainFragment);

    void inject(OldSavedFragment oldSavedFragment);

    void inject(OldLifeCounterFragment oldLifeCounterFragment);

    void inject(OldDecksFragment oldDecksFragment);

    void inject(AddToDeckFragment addToDeckFragment);

    void inject(AboutFragment aboutFragment);

    void inject(SortDialogFragment sortDialogFragment);

    void inject(MTGCardView mtgCardView);

    void inject(LifeCounterFragment lifeCounterFragment);

    void inject(DecksFragment decksFragment);

    void inject(SavedFragment savedFragment);

    void inject(SetsFragment setsFragment);
}

package com.dbottillo.mtgsearchfree.dagger;

import com.dbottillo.mtgsearchfree.ActivityScope;
import com.dbottillo.mtgsearchfree.ui.about.AboutActivity;
import com.dbottillo.mtgsearchfree.ui.cardsConfigurator.CardsConfiguratorFragment;
import com.dbottillo.mtgsearchfree.ui.decks.DecksFragment;
import com.dbottillo.mtgsearchfree.ui.lifecounter.LifeCounterFragment;
import com.dbottillo.mtgsearchfree.ui.saved.SavedFragment;
import com.dbottillo.mtgsearchfree.ui.sets.SetPickerActivity;
import com.dbottillo.mtgsearchfree.ui.sets.SetsFragment;
import com.dbottillo.mtgsearchfree.ui.BasicActivity;
import com.dbottillo.mtgsearchfree.ui.lucky.CardLuckyActivity;
import com.dbottillo.mtgsearchfree.ui.cards.CardsActivity;
import com.dbottillo.mtgsearchfree.ui.CommonCardsActivity;
import com.dbottillo.mtgsearchfree.ui.decks.DeckActivity;
import com.dbottillo.mtgsearchfree.ui.search.SearchActivity;
import com.dbottillo.mtgsearchfree.ui.decks.AddToDeckFragment;
import com.dbottillo.mtgsearchfree.ui.views.MTGCardView;

import org.jetbrains.annotations.NotNull;

import dagger.Component;

@ActivityScope
@Component(modules = {PresentersModule.class, InteractorsModule.class}, dependencies = {AppComponent.class})
public interface UiComponent {

    void inject(CommonCardsActivity activity);

    void inject(BasicActivity activity);

    void inject(AboutActivity aboutActivity);

    void inject(SearchActivity searchActivity);

    void inject(CardsActivity cardsActivity);

    void inject(CardLuckyActivity luckyActivity);

    void inject(DeckActivity deckActivity);

    void inject(AddToDeckFragment addToDeckFragment);

    void inject(LifeCounterFragment lifeCounterFragment);

    void inject(DecksFragment decksFragment);

    void inject(SavedFragment savedFragment);

    void inject(SetsFragment setsFragment);

    void inject(@NotNull CardsConfiguratorFragment cardsConfiguratorFragment);

    void inject(@NotNull SetPickerActivity setPickerActivity);

    void inject(MTGCardView mtgCardView);
}

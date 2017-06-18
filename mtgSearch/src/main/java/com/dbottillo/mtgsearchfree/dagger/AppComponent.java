package com.dbottillo.mtgsearchfree.dagger;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.model.database.SetDataSource;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences;
import com.dbottillo.mtgsearchfree.model.storage.CardsStorage;
import com.dbottillo.mtgsearchfree.model.storage.DecksStorage;
import com.dbottillo.mtgsearchfree.model.storage.GeneralData;
import com.dbottillo.mtgsearchfree.model.storage.PlayersStorage;
import com.dbottillo.mtgsearchfree.model.storage.SavedCardsStorage;
import com.dbottillo.mtgsearchfree.presenter.RunnerFactory;
import com.dbottillo.mtgsearchfree.ui.BasicActivity;
import com.dbottillo.mtgsearchfree.ui.lifecounter.LifeCounterPresenterImpl;
import com.dbottillo.mtgsearchfree.util.DialogUtil;
import com.dbottillo.mtgsearchfree.util.FileUtil;
import com.dbottillo.mtgsearchfree.util.Logger;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AndroidModule.class, DataModule.class})
public interface AppComponent {

    CardsStorage getCardsStorage();

    SavedCardsStorage getSavedCardsStorage();

    SetDataSource getSetDataSource();

    PlayersStorage getPlayerStorage();

    DecksStorage getDecksStorage();

    RunnerFactory getRxWrapperFactory();

    CardsPreferences getCardsPreferences();

    GeneralData getGeneralPreferences();

    FileUtil getFileUtil();

    Logger getLogger();

    DialogUtil getDialogUtil();

    void inject(MTGApp app);

    void inject(BasicActivity mainActivity);

    void inject(LifeCounterPresenterImpl playerPresenter);
}

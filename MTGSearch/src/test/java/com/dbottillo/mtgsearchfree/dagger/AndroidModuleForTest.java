package com.dbottillo.mtgsearchfree.dagger;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.presenter.RxWrapper;
import com.dbottillo.mtgsearchfree.presenter.TestRxWrapper;

import java.util.List;

import dagger.Provides;

public class AndroidModuleForTest extends AndroidModule{
    public AndroidModuleForTest(MTGApp app) {
        super(app);
    }

    @Provides
    RxWrapper<List<MTGSet>> provideListMTGSetWrapper() {
        return new TestRxWrapper<>();
    }

    @Provides
    RxWrapper<CardFilter> provideCardFilterWrapper() {
        return new TestRxWrapper<>();
    }

    @Provides
    RxWrapper<List<Player>> provideLisPlayerWrapper() {
        return new TestRxWrapper<>();
    }

    @Provides
    RxWrapper<List<MTGCard>> provideListCardWrapper() {
        return new TestRxWrapper<>();
    }

    @Provides
    RxWrapper<List<Deck>> provideListDeckWrapper() {
        return new TestRxWrapper<>();
    }

    @Provides
    RxWrapper<int[]> provideFavWrapper() {
        return new TestRxWrapper<>();
    }
}

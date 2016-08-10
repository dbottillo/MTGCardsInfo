package com.dbottillo.mtgsearchfree.dagger;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.presenter.RxWrapperFactory;
import com.dbottillo.mtgsearchfree.presenter.TestRxWrapperFactory;

import dagger.Provides;

public class AndroidModuleForTest extends AndroidModule {
    public AndroidModuleForTest(MTGApp app) {
        super(app);
    }

    @Provides
    RxWrapperFactory provideRxWrapperFactory() {
        return new TestRxWrapperFactory();
    }

}

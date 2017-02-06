package com.dbottillo.mtgsearchfree.dagger;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.presenter.RunnerFactory;
import com.dbottillo.mtgsearchfree.presenter.TestRunnerFactory;

import dagger.Module;
import dagger.Provides;

public class AndroidModuleForTest extends AndroidModule {

    public AndroidModuleForTest(MTGApp app) {
        super(app);
    }

    RunnerFactory provideRxWrapperFactory() {
        return new TestRunnerFactory();
    }

}

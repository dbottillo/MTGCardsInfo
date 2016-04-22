package com.dbottillo.mtgsearchfree.dagger;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.presenter.RxWrapper;
import com.dbottillo.mtgsearchfree.presenter.TestRxWrapper;

public class AndroidModuleForTest extends AndroidModule{
    public AndroidModuleForTest(MTGApp app) {
        super(app);
    }

    RxWrapper provideWrapper() {
        return new TestRxWrapper();
    }
}

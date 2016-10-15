package com.dbottillo.mtgsearchfree;

import com.dbottillo.mtgsearchfree.dagger.DataModule;
import com.dbottillo.mtgsearchfree.dagger.DataModuleForTest;

public class TestMTGApp extends MTGApp {

    @Override
    protected DataModule generateDataModule() {
        return new DataModuleForTest();
    }
}

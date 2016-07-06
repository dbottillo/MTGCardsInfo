package com.dbottillo.mtgsearchfree;

import com.dbottillo.mtgsearchfree.dagger.AndroidModule;
import com.dbottillo.mtgsearchfree.dagger.AndroidModuleForTest;

import org.robolectric.TestLifecycleApplication;

import java.lang.reflect.Method;

public class MTGAppTest extends MTGApp implements TestLifecycleApplication {

    @Override
    public void onCreate() {
        isUnitTesting = true;
        super.onCreate();
    }

    @Override
    public void beforeTest(Method method) {

    }

    @Override
    public void prepareTest(Object test) {

    }

    @Override
    public void afterTest(Method method) {

    }

    @Override
    protected AndroidModule generateAndroidModule() {
        return new AndroidModuleForTest(this);
    }
}

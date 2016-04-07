package com.dbottillo.mtgsearchfree;

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
}

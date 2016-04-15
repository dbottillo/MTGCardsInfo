package com.dbottillo.mtgsearchfree;

import android.content.Context;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@Ignore
public class BaseTest {

    protected Context mContext;

    public BaseTest() {
        mContext = RuntimeEnvironment.application.getApplicationContext();
    }

    protected void setupRxJava() {
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
    }

    @After
    public void tearDown() {
        RxAndroidPlugins.getInstance().reset();
    }
}

package com.dbottillo.mtgsearchfree;

import android.content.Context;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@Ignore
public class BaseTest {

    protected Context mContext;

    public BaseTest() {
        mContext = RuntimeEnvironment.application.getApplicationContext();
    }

}

package com.dbottillo.mtgsearchfree;

import android.content.Context;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@SmallTest
@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@Ignore
public class BaseTest {

    protected Context mContext;

    public BaseTest() {
        mContext = RuntimeEnvironment.application.getApplicationContext();
    }
}

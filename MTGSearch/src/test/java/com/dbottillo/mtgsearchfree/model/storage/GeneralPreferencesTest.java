package com.dbottillo.mtgsearchfree.model.storage;

import com.dbottillo.mtgsearchfree.BaseTest;
import com.dbottillo.mtgsearchfree.BuildConfig;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GeneralPreferencesTest extends BaseTest{

    @Test
    public void testSaveAndLoadDebugFlag(){
        GeneralPreferences generalPreference = GeneralPreferences.with(mContext);
        if (!BuildConfig.DEBUG) {
            assertFalse(generalPreference.isDebugEnabled());
        }
        generalPreference.setDebug();
        assertTrue(generalPreference.isDebugEnabled());
    }

}
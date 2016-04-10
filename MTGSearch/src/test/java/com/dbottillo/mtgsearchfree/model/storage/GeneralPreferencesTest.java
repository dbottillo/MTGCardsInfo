package com.dbottillo.mtgsearchfree.model.storage;

import com.dbottillo.mtgsearchfree.BaseTest;

import org.junit.Test;

import static org.junit.Assert.*;

public class GeneralPreferencesTest extends BaseTest{

    @Test
    public void testSaveAndLoadDebugFlag(){
        GeneralPreferences generalPreference = GeneralPreferences.with(mContext);
        assertFalse(generalPreference.isDebugEnabled());
        generalPreference.setDebug();
        assertTrue(generalPreference.isDebugEnabled());
    }

}
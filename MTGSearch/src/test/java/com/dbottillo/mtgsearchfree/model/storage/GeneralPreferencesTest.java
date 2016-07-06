package com.dbottillo.mtgsearchfree.model.storage;

import com.dbottillo.mtgsearchfree.BaseTest;
import com.dbottillo.mtgsearchfree.BuildConfig;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GeneralPreferencesTest extends BaseTest {

    GeneralPreferences underTest;

    @Before
    public void setup() {
        underTest = GeneralPreferences.with(mContext);
        underTest.clear();
    }

    @Test
    public void testSaveAndLoadDebugFlag() {
        underTest = GeneralPreferences.with(mContext);
        if (!BuildConfig.DEBUG) {
            assertFalse(underTest.isDebugEnabled());
        }
        underTest.setDebug();
        assertTrue(underTest.isDebugEnabled());
    }

    @Test
    public void testShowTooltip() {
        assertTrue(underTest.isTooltipMainToShow());
        underTest.setTooltipMainHide();
        assertFalse(underTest.isTooltipMainToShow());
    }

}
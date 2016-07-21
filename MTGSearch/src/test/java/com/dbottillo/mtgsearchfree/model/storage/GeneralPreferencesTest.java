package com.dbottillo.mtgsearchfree.model.storage;

import com.dbottillo.mtgsearchfree.BaseTest;
import com.dbottillo.mtgsearchfree.BuildConfig;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GeneralPreferencesTest extends BaseTest {

    private GeneralPreferences underTest;

    @Before
    public void setup() {
        underTest = new GeneralPreferences(mContext);
        underTest.clear();
    }

    @Test
    public void testSaveAndLoadDebugFlag() {
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
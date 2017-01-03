package com.dbottillo.mtgsearchfree.model.storage;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import com.dbottillo.mtgsearchfree.util.AppInfo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static com.dbottillo.mtgsearchfree.model.storage.GeneralPreferences.CARDS_SHOW_TYPE;
import static com.dbottillo.mtgsearchfree.model.storage.GeneralPreferences.CARD_MIGRATION_REQUIRED;
import static com.dbottillo.mtgsearchfree.model.storage.GeneralPreferences.DEBUG;
import static com.dbottillo.mtgsearchfree.model.storage.GeneralPreferences.TOOLTIP_MAIN_SHOWN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressLint("CommitPrefEdits")
public class GeneralPreferencesTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private GeneralPreferences underTest;

    @Mock
    AppInfo appInfo;

    @Mock
    SharedPreferences sharedPreferences;

    @Mock
    SharedPreferences.Editor editor;

    @Before
    public void setup() {
        underTest = new GeneralPreferences(sharedPreferences, appInfo);
        when(sharedPreferences.edit()).thenReturn(editor);
        when(editor.putBoolean(any(String.class), any(Boolean.class))).thenReturn(editor);
        when(editor.putString(any(String.class), any(String.class))).thenReturn(editor);
    }

    @Test
    public void setDebug_shouldUpdateSharedPreferences() {
        underTest.setDebug();

        verify(sharedPreferences).edit();
        verify(editor).putBoolean(DEBUG, true);
        verify(editor).apply();
    }

    @Test
    public void setCardsShowTypeList_shouldUpdateSharedPreferences() {
        underTest.setCardsShowTypeList();

        verify(sharedPreferences).edit();
        verify(editor).putString(CARDS_SHOW_TYPE, "List");
        verify(editor).apply();
    }

    @Test
    public void setCardsShowTypeGrid_shouldUpdateSharedPreferences() {
        underTest.setCardsShowTypeGrid();

        verify(sharedPreferences).edit();
        verify(editor).putString(CARDS_SHOW_TYPE, "Grid");
        verify(editor).apply();
    }

    @Test
    public void setTooltipMainHide_shouldUpdateSharedPreferences() {
        underTest.setTooltipMainHide();

        verify(sharedPreferences).edit();
        verify(editor).putBoolean(TOOLTIP_MAIN_SHOWN, false);
        verify(editor).apply();
    }

    @Test
    public void isTooltipMainToShow_shouldReturnTrueIfItsNotAFreshInstall_AndSharedPreferencesIsTrue() {
        when(sharedPreferences.getBoolean(TOOLTIP_MAIN_SHOWN, true)).thenReturn(true);
        when(appInfo.getFirstInstallTime()).thenReturn(200L);
        when(appInfo.getLastUpdateTime()).thenReturn(400L);

        assertTrue(underTest.isTooltipMainToShow());
    }

    @Test
    public void isTooltipMainToShow_shouldReturnFalseIfItsAFreshInstall() {
        when(appInfo.getFirstInstallTime()).thenReturn(200L);
        when(appInfo.getLastUpdateTime()).thenReturn(200L);

        assertFalse(underTest.isTooltipMainToShow());
    }

    @Test
    public void isTooltipMainToShow_shouldReturnFalseIfItsNotAFreshInstall_AnsSharedPreferencesIsFalse() {
        when(sharedPreferences.getBoolean(TOOLTIP_MAIN_SHOWN, true)).thenReturn(false);
        when(appInfo.getFirstInstallTime()).thenReturn(200L);
        when(appInfo.getLastUpdateTime()).thenReturn(400L);

        assertFalse(underTest.isTooltipMainToShow());
    }

    @Test
    public void markCardMigrationStarted_shouldUpdateSharedPreferences() {
        underTest.markCardMigrationStarted();

        verify(sharedPreferences).edit();
        verify(editor).putBoolean(CARD_MIGRATION_REQUIRED, false);
        verify(editor).apply();
    }

}
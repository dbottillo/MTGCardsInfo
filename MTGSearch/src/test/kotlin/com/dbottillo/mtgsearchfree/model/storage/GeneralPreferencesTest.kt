package com.dbottillo.mtgsearchfree.model.storage

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.dbottillo.mtgsearchfree.model.storage.GeneralPreferences.*
import com.dbottillo.mtgsearchfree.util.AppInfo
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit

@SuppressLint("CommitPrefEdits")
class GeneralPreferencesTest {

    @Rule
    @JvmField
    var mockitoRule = MockitoJUnit.rule()

    lateinit var underTest: GeneralPreferences

    @Mock
    lateinit var appInfo: AppInfo

    @Mock
    lateinit var sharedPreferences: SharedPreferences

    @Mock
    lateinit var editor: Editor

    @Before
    fun setup() {
        underTest = GeneralPreferences(sharedPreferences, appInfo)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        `when`(editor.putBoolean(anyString(), anyBoolean())).thenReturn(editor)
        `when`(editor.putString(anyString(), anyString())).thenReturn(editor)
    }

    @Test
    fun setDebug_shouldUpdateSharedPreferences() {
        underTest.setDebug()

        verify(sharedPreferences).edit()
        verify(editor).putBoolean(DEBUG, true)
        verify(editor).apply()
    }

    @Test
    fun setCardsShowTypeList_shouldUpdateSharedPreferences() {
        underTest.setCardsShowTypeList()

        verify(sharedPreferences).edit()
        verify(editor).putString(CARDS_SHOW_TYPE, "List")
        verify(editor).apply()
    }

    @Test
    fun setCardsShowTypeGrid_shouldUpdateSharedPreferences() {
        underTest.setCardsShowTypeGrid()

        verify(sharedPreferences).edit()
        verify(editor).putString(CARDS_SHOW_TYPE, "Grid")
        verify(editor).apply()
    }

    @Test
    fun setTooltipMainHide_shouldUpdateSharedPreferences() {
        underTest.setTooltipMainHide()

        verify(sharedPreferences).edit()
        verify(editor).putBoolean(TOOLTIP_MAIN_SHOWN, false)
        verify(editor).apply()
    }

    @Test
    fun isTooltipMainToShow_shouldReturnTrueIfItsNotAFreshInstall_AndSharedPreferencesIsTrue() {
        `when`(sharedPreferences.getBoolean(TOOLTIP_MAIN_SHOWN, true)).thenReturn(true)
        `when`(appInfo.firstInstallTime).thenReturn(200L)
        `when`(appInfo.lastUpdateTime).thenReturn(400L)

        assertTrue(underTest.isTooltipMainToShow)
    }

    @Test
    fun isTooltipMainToShow_shouldReturnFalseIfItsAFreshInstall() {
        `when`(appInfo.firstInstallTime).thenReturn(200L)
        `when`(appInfo.lastUpdateTime).thenReturn(200L)

        assertFalse(underTest.isTooltipMainToShow)
    }

    @Test
    fun isTooltipMainToShow_shouldReturnFalseIfItsNotAFreshInstall_AnsSharedPreferencesIsFalse() {
        `when`(sharedPreferences.getBoolean(TOOLTIP_MAIN_SHOWN, true)).thenReturn(false)
        `when`(appInfo.firstInstallTime).thenReturn(200L)
        `when`(appInfo.lastUpdateTime).thenReturn(400L)

        assertFalse(underTest.isTooltipMainToShow)
    }

    @Test
    fun markCardMigrationStarted_shouldUpdateSharedPreferences() {
        underTest.markCardMigrationStarted()

        verify(sharedPreferences).edit()
        verify(editor).putBoolean(CARD_MIGRATION_REQUIRED, false)
        verify(editor).apply()
    }

}
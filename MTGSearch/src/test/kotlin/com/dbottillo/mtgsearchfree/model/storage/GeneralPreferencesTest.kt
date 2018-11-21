package com.dbottillo.mtgsearchfree.model.storage

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.dbottillo.mtgsearchfree.util.AppInfo
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

@SuppressLint("CommitPrefEdits")
class GeneralPreferencesTest {

    @Rule @JvmField var mockitoRule = MockitoJUnit.rule()!!

    lateinit var underTest: GeneralPreferences

    @Mock lateinit var appInfo: AppInfo
    @Mock lateinit var sharedPreferences: SharedPreferences
    @Mock lateinit var appContext: Context
    @Mock lateinit var editor: Editor

    @Before
    fun setup() {
        whenever(appContext.getSharedPreferences("General", Context.MODE_PRIVATE)).thenReturn(sharedPreferences)
        underTest = GeneralPreferences(appContext, appInfo)
        whenever(sharedPreferences.edit()).thenReturn(editor)
        whenever(editor.putBoolean(anyString(), anyBoolean())).thenReturn(editor)
        whenever(editor.putString(anyString(), anyString())).thenReturn(editor)
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
        whenever(sharedPreferences.getBoolean(TOOLTIP_MAIN_SHOWN, true)).thenReturn(true)
        whenever(appInfo.firstInstallTime).thenReturn(200L)
        whenever(appInfo.lastUpdateTime).thenReturn(400L)

        assertTrue(underTest.isTooltipMainToShow())
    }

    @Test
    fun isTooltipMainToShow_shouldReturnFalseIfItsAFreshInstall() {
        whenever(appInfo.firstInstallTime).thenReturn(200L)
        whenever(appInfo.lastUpdateTime).thenReturn(200L)

        assertFalse(underTest.isTooltipMainToShow())
    }

    @Test
    fun isTooltipMainToShow_shouldReturnFalseIfItsNotAFreshInstall_AnsSharedPreferencesIsFalse() {
        whenever(sharedPreferences.getBoolean(TOOLTIP_MAIN_SHOWN, true)).thenReturn(false)
        whenever(appInfo.firstInstallTime).thenReturn(200L)
        whenever(appInfo.lastUpdateTime).thenReturn(400L)

        assertFalse(underTest.isTooltipMainToShow())
    }

    @Test
    fun markCardMigrationStarted_shouldUpdateSharedPreferences() {
        underTest.markCardMigrationStarted()

        verify(sharedPreferences).edit()
        verify(editor).putBoolean(CARD_MIGRATION_REQUIRED, false)
        verify(editor).apply()
    }

    @Test
    fun `should return last selected deck from shared pref`() {
        whenever(sharedPreferences.getLong(LAST_DECK_SELECTED, -1)).thenReturn(4)

        val result = underTest.lastDeckSelected

        assertThat(result, `is`(4L))
        verify(appContext).getSharedPreferences("General", Context.MODE_PRIVATE)
        verify(sharedPreferences).getLong(LAST_DECK_SELECTED, -1)
        verifyNoMoreInteractions(sharedPreferences, appContext, appInfo)
    }

    @Test
    fun `should save last selected deck in shared pref`() {
        whenever(editor.putLong(LAST_DECK_SELECTED, 4L)).thenReturn(editor)

        underTest.lastDeckSelected = 4L

        verify(appContext).getSharedPreferences("General", Context.MODE_PRIVATE)
        verify(sharedPreferences).edit()
        verify(editor).putLong(LAST_DECK_SELECTED, 4L)
        verify(editor).apply()
        verifyNoMoreInteractions(sharedPreferences, appContext, appInfo)
    }
}
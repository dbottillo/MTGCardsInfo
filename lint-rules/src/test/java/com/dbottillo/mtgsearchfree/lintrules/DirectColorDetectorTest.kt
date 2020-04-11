package com.dbottillo.mtgsearchfree.lintrules

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import org.junit.Test

class DirectColorDetectorTest {

    @Test
    fun `should not report color references or attributes`() {
        val contentFile = """<?xml version="1.0" encoding="utf-8"?>
            <View 
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    android:id="@+id/toolbar"
                    android:background="@color/background"
                    android:foreground="?attr/foreground"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content" />"""
        TestLintTask.lint()
                .files(TestFiles.xml("res/layout/toolbar.xml", contentFile).indented())
                .issues(DIRECT_COLOR_ISSUE)
                .run()
                .expectClean()
    }

    @Test
    fun `should report a direct color`() {
        val contentFile = """<?xml version="1.0" encoding="utf-8"?>
            <View 
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    android:id="@+id/toolbar"
                    android:background="#453344"
                    android:foreground="#667788"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content" />"""
        TestLintTask.lint()
                .files(TestFiles.xml("res/layout/toolbar.xml", contentFile).indented())
                .issues(DIRECT_COLOR_ISSUE)
                .run()
                .expect("""
                |res/layout/toolbar.xml:5: Error: Avoid direct use of colors in XML files. This will cause issues with different theme (eg. dark) support [DirectColorUse]
                |                    android:background="#453344"
                |                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                |res/layout/toolbar.xml:6: Error: Avoid direct use of colors in XML files. This will cause issues with different theme (eg. dark) support [DirectColorUse]
                |                    android:foreground="#667788"
                |                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                |2 errors, 0 warnings""".trimMargin())
    }

    @Test
    fun `should ignore vector when detecting a direct color`() {
        val contentFile = """<vector android:height="24dp" android:tint="#FFFFFF"
                android:viewportHeight="24.0" android:viewportWidth="24.0"
                android:width="24dp" xmlns:android="http://schemas.android.com/apk/res/android">
            <path android:fillColor="#FF000000" android:pathData="M15.5,14h-0.79l-0.2"/>
            </vector>
        """
        TestLintTask.lint()
                .files(TestFiles.xml("res/drawable/ic_star.xml", contentFile).indented())
                .issues(DIRECT_COLOR_ISSUE)
                .run()
                .expectClean()
    }
}
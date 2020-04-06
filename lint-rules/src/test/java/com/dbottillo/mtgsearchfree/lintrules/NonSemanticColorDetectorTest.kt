package com.dbottillo.mtgsearchfree.lintrules

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import org.junit.Test

class NonSemanticColorDetectorTest {

    @Test
    fun `should report a non semantic color usage`() {
        val contentFile = """<?xml version="1.0" encoding="utf-8"?>
            <View 
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    android:id="@+id/toolbar"
                    android:background="@color/white"
                    android:foreground="@color/red"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content" />"""
        TestLintTask.lint()
                .files(TestFiles.xml("res/layout/toolbar.xml", contentFile).indented())
                .issues(NON_SEMANTIC_COLOR_ISSUE)
                .run()
                .expect("""
                |res/layout/toolbar.xml:5: Error: Avoid non semantic use of colors in XML files. This will cause issues with different theme (eg. dark) support. For example, use primary instead of black. [NonSemanticColorUse]
                |                    android:background="@color/white"
                |                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                |res/layout/toolbar.xml:6: Error: Avoid non semantic use of colors in XML files. This will cause issues with different theme (eg. dark) support. For example, use primary instead of black. [NonSemanticColorUse]
                |                    android:foreground="@color/red"
                |                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                |2 errors, 0 warnings""".trimMargin())
    }

}
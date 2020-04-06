package com.dbottillo.mtgsearchfree.lintrules

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import org.junit.Test

class MissingNightColorDetectorTest {

    @Test
    fun `should not report if all normal colors files are also in night`() {
        val colorFile = TestFiles.xml("res/values/colors.xml",
                """<?xml version="1.0" encoding="utf-8"?>
            <resources> 
                <color name="color_primary">#00a7f7</color>
                <color name="color_primary_slightly_dark">#0193e8</color>
            </resources>""").indented()
        val colorNightFile = TestFiles.xml("res/values-night/colors.xml",
                """<?xml version="1.0" encoding="utf-8"?>
            <resources> 
                <color name="color_primary">#224411</color>
                <color name="color_primary_slightly_dark">#886644</color>
            </resources>""").indented()
        TestLintTask.lint()
                .files(colorFile, colorNightFile)
                .issues(MISSING_NIGHT_COLOR_ISSUE)
                .run()
                .expectClean()
    }

    @Test
    fun `should report a missing night color`() {
        val colorFile = TestFiles.xml("res/values/colors.xml",
                """<?xml version="1.0" encoding="utf-8"?>
            <resources> 
                <color name="color_primary">#00a7f7</color>
                <color name="color_primary_slightly_dark">#0193e8</color>
            </resources>""").indented()
        val colorNightFile = TestFiles.xml("res/values-night/colors.xml",
                """<?xml version="1.0" encoding="utf-8"?>
            <resources> 
                <color name="color_primary">#224411</color>
            </resources>""").indented()
        TestLintTask.lint()
                .files(colorFile, colorNightFile)
                .issues(MISSING_NIGHT_COLOR_ISSUE)
                .run()
                .expect("""
                |res/values/colors.xml:4: Error: Night color value for this color resource seems to be missing. If your app supports dark theme, then you should add an equivalent color resource for it in the night values folder. [MissingNightColor]
                |                <color name="color_primary_slightly_dark">#0193e8</color>
                |                ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                |1 errors, 0 warnings""".trimMargin())
    }
}
package com.dbottillo.mtgsearchfree.lintrules

import com.android.tools.lint.detector.api.TextFormat
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class IssueRegistryTest {

    @Test
    fun `check explanation for issues is correct`() {
        val output = IssueRegistry().issues
                .joinToString(separator = "\n") { "- **${it.id}** - ${it.getExplanation(TextFormat.RAW)}" }

        assertThat("""
        - **DirectColorUse** - Avoid direct use of colors in XML files. This will cause issues with different theme (eg. dark) support
        - **MissingNightColor** - Night color value for this color resource seems to be missing. If your app supports dark theme, then you should add an equivalent color resource for it in the night values folder.
        - **NonSemanticColorUse** - Avoid non semantic use of colors in XML files. This will cause issues with different theme (eg. dark) support. For example, use primary instead of black.
        """.trimIndent()).isEqualTo(output)
    }
}

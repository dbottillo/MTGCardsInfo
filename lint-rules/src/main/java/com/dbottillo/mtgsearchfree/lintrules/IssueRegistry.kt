package com.dbottillo.mtgsearchfree.lintrules

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Category.Companion.CORRECTNESS
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity

class IssueRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(DIRECT_COLOR_ISSUE, MISSING_NIGHT_COLOR_ISSUE, NON_SEMANTIC_COLOR_ISSUE)

    override val api: Int = com.android.tools.lint.detector.api.CURRENT_API
}

const val PRIORITY_6 = 6

val DIRECT_COLOR_ISSUE = Issue.create("DirectColorUse",
        "Direct color used",
        "Avoid direct use of colors in XML files. This will cause issues with different theme (eg. dark) support",
        CORRECTNESS,
        PRIORITY_6,
        Severity.ERROR,
        Implementation(DirectColorDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
)

@Suppress("MaxLineLength")
val MISSING_NIGHT_COLOR_ISSUE = Issue.create("MissingNightColor",
        "Night color missing",
        "Night color value for this color resource seems to be missing. If your app supports dark theme, then you should add an" +
                " equivalent color resource for it in the night values folder.",
        CORRECTNESS,
        PRIORITY_6,
        Severity.ERROR,
        Implementation(MissingNightColorDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
)

val NON_SEMANTIC_COLOR_ISSUE = Issue.create("NonSemanticColorUse",
        "Non semantic color used",
        "Avoid non semantic use of colors in XML files. This will cause issues with different theme (eg. dark) support. " +
                "For example, use primary instead of black.",
        CORRECTNESS,
        PRIORITY_6,
        Severity.ERROR,
        Implementation(NonSemanticColorDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
)

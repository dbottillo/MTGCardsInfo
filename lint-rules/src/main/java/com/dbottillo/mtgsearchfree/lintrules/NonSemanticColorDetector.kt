package com.dbottillo.mtgsearchfree.lintrules

import com.android.tools.lint.detector.api.ResourceXmlDetector
import com.android.tools.lint.detector.api.TextFormat
import com.android.tools.lint.detector.api.XmlContext
import org.w3c.dom.Attr

class NonSemanticColorDetector : ResourceXmlDetector() {

    override fun getApplicableAttributes(): Collection<String>? = LintHelper.applicableColorAttributes

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        if (checkName(attribute.value)) {
            context.report(
                    NON_SEMANTIC_COLOR_ISSUE,
                    context.getLocation(attribute),
                    NON_SEMANTIC_COLOR_ISSUE.getExplanation(TextFormat.RAW))
        }
    }

    private fun checkName(input: String): Boolean {
        return NON_SEMANTIC_COLORS.any {
            input.contains(it)
        }
    }
}

private val NON_SEMANTIC_COLORS = listOf(
        "black", "blue", "green", "orange", "teal", "white", "orange", "red")

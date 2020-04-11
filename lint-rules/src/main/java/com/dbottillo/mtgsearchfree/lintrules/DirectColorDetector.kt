package com.dbottillo.mtgsearchfree.lintrules

import com.android.tools.lint.detector.api.ResourceXmlDetector
import com.android.tools.lint.detector.api.TextFormat
import com.android.tools.lint.detector.api.XmlContext
import org.w3c.dom.Attr
import org.w3c.dom.Element

class DirectColorDetector : ResourceXmlDetector() {

    override fun getApplicableAttributes(): Collection<String>? = LintHelper.applicableColorAttributes

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        if (attribute.value.startsWith("#") && attribute.ownerElement.tagName != "vector") {
            context.report(
                    DIRECT_COLOR_ISSUE,
                    context.getLocation(attribute),
                    DIRECT_COLOR_ISSUE.getExplanation(TextFormat.RAW))
        }
    }
}

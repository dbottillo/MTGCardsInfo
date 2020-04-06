package com.dbottillo.mtgsearchfree.lintrules

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.Context
import com.android.tools.lint.detector.api.Location
import com.android.tools.lint.detector.api.ResourceXmlDetector
import com.android.tools.lint.detector.api.TextFormat
import com.android.tools.lint.detector.api.XmlContext
import org.w3c.dom.Element

class MissingNightColorDetector : ResourceXmlDetector() {

    private val nightModeColors = mutableListOf<String>()
    private val regularColors = mutableMapOf<String, Location>()

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return folderType == ResourceFolderType.VALUES
    }

    override fun getApplicableElements(): Collection<String>? {
        return listOf("color")
    }

    override fun afterCheckEachProject(context: Context) {
        regularColors.forEach { (color, location) ->
            if (!nightModeColors.contains(color))
                context.report(
                        MISSING_NIGHT_COLOR_ISSUE,
                        location,
                        MISSING_NIGHT_COLOR_ISSUE.getExplanation(TextFormat.RAW)
                )
        }
    }

    override fun visitElement(context: XmlContext, element: Element) {
        if (context.getFolderConfiguration()!!.isDefault)
            regularColors[element.getAttribute("name")] = context.getLocation(element)
        else if (context.getFolderConfiguration()!!.nightModeQualifier.isValid)
            nightModeColors.add(element.getAttribute("name"))
    }
}

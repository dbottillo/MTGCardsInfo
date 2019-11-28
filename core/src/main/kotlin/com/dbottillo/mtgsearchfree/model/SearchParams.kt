package com.dbottillo.mtgsearchfree.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchParams(
    var name: String = "",
    var types: String = "",
    var text: String = "",
    var cmc: CMCParam? = null,
    var power: PTParam? = null,
    var tough: PTParam? = null,
    var isWhite: Boolean = false,
    var isBlue: Boolean = false,
    var isBlack: Boolean = false,
    var isRed: Boolean = false,
    var isGreen: Boolean = false,
    var isLand: Boolean = false,
    var exactlyColors: Boolean = true,
    var includingColors: Boolean = false,
    var atMostColors: Boolean = false,
    var isCommon: Boolean = false,
    var isUncommon: Boolean = false,
    var isRare: Boolean = false,
    var isMythic: Boolean = false,
    var setId: Int = -1,
    var colorless: Boolean = false,
    var duplicates: Boolean = true
) : Parcelable {

    val isValid: Boolean
        get() = (name.isNotEmpty() || types.isNotEmpty() ||
                cmc != null || power != null || tough != null ||
                setId > 0 || text.isNotEmpty() ||
                isLand || atLeastOneColor || atLeastOneRarity || colorless)

    val atLeastOneColor: Boolean
        get() = isWhite || isBlack || isBlue || isRed || isGreen

    val atLeastOneRarity: Boolean
        get() = isCommon || isUncommon || isRare || isMythic

    val colors: List<String>
        get() {
            val colors = mutableListOf<String>()
            if (isBlack) {
                colors.add("B")
            }
            if (isGreen) {
                colors.add("G")
            }
            if (isRed) {
                colors.add("R")
            }
            if (isBlue) {
                colors.add("U")
            }
            if (isWhite) {
                colors.add("W")
            }
            return colors.toList()
        }
}

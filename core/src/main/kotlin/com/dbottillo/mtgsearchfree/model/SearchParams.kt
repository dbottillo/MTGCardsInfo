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
    private var onlyMulti: Boolean = false,
    private var noMulti: Boolean = false,
    var isLand: Boolean = false,
    var isOnlyMultiNoOthers: Boolean = false,
    var isCommon: Boolean = false,
    var isUncommon: Boolean = false,
    var isRare: Boolean = false,
    var isMythic: Boolean = false,
    var setId: Int = -1
) : Parcelable {

    var isNoMulti: Boolean
        get() = noMulti
        set(noMulti) {
            this.noMulti = noMulti
            if (noMulti) {
                this.onlyMulti = false
            }
        }

    val isValid: Boolean
        get() = (name.isNotEmpty() || types.isNotEmpty() ||
                cmc != null || power != null || tough != null ||
                setId > 0 || text.isNotEmpty() ||
                isLand || atLeastOneColor() || atLeastOneRarity())

    fun onlyMulti(): Boolean {
        return onlyMulti
    }

    fun setOnlyMulti(onlyMulti: Boolean) {
        this.onlyMulti = onlyMulti
        if (onlyMulti) {
            this.noMulti = false
        }
    }

    fun atLeastOneColor(): Boolean {
        return isWhite || isBlack || isBlue || isRed || isGreen
    }

    fun atLeastOneRarity(): Boolean {
        return isCommon || isUncommon || isRare || isMythic
    }
}

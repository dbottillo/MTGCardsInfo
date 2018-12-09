package com.dbottillo.mtgsearchfree.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CardFilter(
    var white: Boolean = true,
    var blue: Boolean = true,
    var black: Boolean = true,
    var red: Boolean = true,
    var green: Boolean = true,
    var artifact: Boolean = true,
    var land: Boolean = true,
    var eldrazi: Boolean = true,
    var common: Boolean = true,
    var uncommon: Boolean = true,
    var rare: Boolean = true,
    var mythic: Boolean = true,
    var sortWUBGR: Boolean = true
) : Parcelable {

    enum class TYPE {
        WHITE,
        BLUE,
        BLACK,
        RED,
        GREEN,
        ARTIFACT,
        LAND,
        ELDRAZI,
        COMMON,
        UNCOMMON,
        RARE,
        MYTHIC,
        SORT_WUBGR
    }
}

enum class Rarity(val value: String) {
    COMMON("common"),
    UNCOMMON("uncommon"),
    RARE("rare"),
    MYTHIC("mythic")
}
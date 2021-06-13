package com.dbottillo.mtgsearchfree.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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
    var sortSetNumber: Boolean = true
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
        SORT_SET_NUMBER
    }
}

enum class Rarity(val value: String) {
    COMMON("common"),
    UNCOMMON("uncommon"),
    RARE("rare"),
    MYTHIC("mythic")
}
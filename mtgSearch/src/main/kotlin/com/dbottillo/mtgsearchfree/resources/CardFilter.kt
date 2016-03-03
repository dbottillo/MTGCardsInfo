package com.dbottillo.mtgsearchfree.resources

class CardFilter {

    enum class TYPE{
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
        MYTHIC
    }

    var white: Boolean = true
    var blue: Boolean = true
    var black: Boolean = true
    var red: Boolean = true
    var green: Boolean = true

    var artifact: Boolean = true
    var land: Boolean = true
    var eldrazi: Boolean = true

    var common: Boolean = true
    var uncommon: Boolean = true
    var rare: Boolean = true
    var mythic: Boolean = true
}


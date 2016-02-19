package com.dbottillo.mtgsearchfree.resources

enum class CardFilter(var on: Boolean = true) {
    WHITE(),
    BLUE(),
    BLACK(),
    RED(),
    GREEN(),

    ARTIFACT(),
    LAND(),
    ELDRAZI(),

    COMMON(),
    UNCOMMON(),
    RARE(),
    MYTHIC()
}


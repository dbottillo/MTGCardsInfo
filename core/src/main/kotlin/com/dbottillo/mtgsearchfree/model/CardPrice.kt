package com.dbottillo.mtgsearchfree.model

data class TCGCardPrice(var hiPrice: String, var lowprice: String, var avgPrice: String) :
    CardPrice {

    override fun toDisplay(isLandscape: Boolean): String {
        return if (hiPrice.length > 5 && !isLandscape) {
            " A:$avgPrice$  L:$lowprice$"
        } else " H:$hiPrice$   A:$avgPrice$   L:$lowprice$"
    }
}

data class MKMCardPrice(
    var low: String,
    var trend: String,
    var url: String,
    val exact: Boolean
) : CardPrice {

    override fun toDisplay(isLandscape: Boolean): String {
        return "L:$low€ T:$trend€"
    }
}

interface CardPrice {
    fun toDisplay(isLandscape: Boolean): String
}
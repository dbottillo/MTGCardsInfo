package com.dbottillo.mtgsearchfree.model

data class TCGPrice(var hiPrice: String, var lowprice: String, var avgPrice: String) {

    fun toDisplay(isLandscape: Boolean): String {
        return if (hiPrice.length > 5 && !isLandscape) {
            " A:$avgPrice$  L:$lowprice$"
        } else " H:$hiPrice$   A:$avgPrice$   L:$lowprice$"
    }
}
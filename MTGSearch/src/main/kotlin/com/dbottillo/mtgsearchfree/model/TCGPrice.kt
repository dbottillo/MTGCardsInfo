package com.dbottillo.mtgsearchfree.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TCGPrice(var hiPrice: String? = null,
               var lowprice: String? = null,
               var avgPrice: String? = null,
               var link: String? = null,
               var errorPrice: String? = null,
               var isAnError: Boolean = false,
               var isNotFound: Boolean = false) : Parcelable {


    fun setError(errorPrice: String) {
        isAnError = true
        this.errorPrice = errorPrice
    }

    fun toDisplay(isLandscape: Boolean): String {
        return if (hiPrice!!.length > 5 && !isLandscape) {
            " A:$avgPrice$  L:$lowprice$"
        } else " H:$hiPrice$   A:$avgPrice$   L:$lowprice$"
    }
}
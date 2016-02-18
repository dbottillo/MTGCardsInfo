package com.dbottillo.mtgsearchfree.resources

import android.os.Parcel
import android.os.Parcelable

class TCGPrice : Parcelable {

    var hiPrice: String? = null
    var lowprice: String? = null
    var avgPrice: String? = null
    var link: String? = null
    var errorPrice: String? = null
    var isAnError: Boolean = false
    var isNotFound: Boolean = false

    constructor() {

    }

    constructor(parcel: Parcel) {
        readFromParcel(parcel)
    }

    fun setError(errorPrice: String) {
        isAnError = true
        this.errorPrice = errorPrice
    }

    override fun toString(): String {
        return "[TCGPrice] H:$hiPrice - A:$avgPrice - L:$lowprice - $link"
    }

    fun toDisplay(isLandscape: Boolean): String {
        if (hiPrice!!.length > 5 && !isLandscape) {
            return " A:$avgPrice$  L:$lowprice$"
        }
        return " H:$hiPrice$   A:$avgPrice$   L:$lowprice$"
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(hiPrice)
        dest.writeString(avgPrice)
        dest.writeString(lowprice)
        dest.writeString(link)
        dest.writeString(errorPrice)
        dest.writeInt(if (isAnError) 1 else 0)
        dest.writeInt(if (isNotFound) 1 else 0)
    }

    fun readFromParcel(parcel: Parcel) {
        hiPrice = parcel.readString()
        avgPrice = parcel.readString()
        lowprice = parcel.readString()
        link = parcel.readString()
        errorPrice = parcel.readString()
        isAnError = parcel.readInt() == 1
        isNotFound = parcel.readInt() == 1
    }

    companion object {

        val CREATOR: Parcelable.Creator<TCGPrice> = object : Parcelable.Creator<TCGPrice> {
            override fun createFromParcel(source: Parcel): TCGPrice {
                return TCGPrice(source)
            }

            override fun newArray(size: Int): Array<TCGPrice> {
                return Array(size, { i -> TCGPrice() })
            }
        }
    }

}

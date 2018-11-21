package com.dbottillo.mtgsearchfree.model

import android.os.Parcel
import android.os.Parcelable

data class Deck constructor(
    var id: Long,
    var name: String = "",
    var isArchived: Boolean = false,
    var numberOfCards: Int = 0,
    var sizeOfSideboard: Int = 0
) : Parcelable {

    constructor(parcelIn: Parcel) :
            this(parcelIn.readLong(), parcelIn.readString(),
                    parcelIn.readInt() == 1, parcelIn.readInt(), parcelIn.readInt())

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(name)
        dest.writeInt(if (isArchived) 1 else 0)
        dest.writeInt(numberOfCards)
        dest.writeInt(sizeOfSideboard)
    }

    override fun toString(): String {
        return "[$id,$name]"
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Deck> = object : Parcelable.Creator<Deck> {
            override fun createFromParcel(source: Parcel): Deck {
                return Deck(source)
            }

            override fun newArray(size: Int): Array<Deck?> {
                return arrayOfNulls(size)
            }
        }
    }
}

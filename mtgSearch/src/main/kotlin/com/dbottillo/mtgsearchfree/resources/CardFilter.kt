package com.dbottillo.mtgsearchfree.resources

import android.os.Parcel
import android.os.Parcelable

class CardFilter() : Parcelable {

    constructor(parcel: Parcel) : this() {
        readFromParcel(parcel)
    }

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

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(if (white) 1 else 0)
        dest.writeInt(if (blue) 1 else 0)
        dest.writeInt(if (black) 1 else 0)
        dest.writeInt(if (red) 1 else 0)
        dest.writeInt(if (green) 1 else 0)

        dest.writeInt(if (artifact) 1 else 0)
        dest.writeInt(if (land) 1 else 0)
        dest.writeInt(if (eldrazi) 1 else 0)

        dest.writeInt(if (common) 1 else 0)
        dest.writeInt(if (uncommon) 1 else 0)
        dest.writeInt(if (rare) 1 else 0)
        dest.writeInt(if (mythic) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0;
    }

    fun readFromParcel(parcel: Parcel) {
        white = parcel.readInt() == 1
        blue = parcel.readInt() == 1
        black = parcel.readInt() == 1
        red = parcel.readInt() == 1
        green = parcel.readInt() == 1

        artifact = parcel.readInt() == 1
        land = parcel.readInt() == 1
        eldrazi = parcel.readInt() == 1

        common = parcel.readInt() == 1
        uncommon = parcel.readInt() == 1
        rare = parcel.readInt() == 1
        mythic = parcel.readInt() == 1
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<CardFilter> = object : Parcelable.Creator<CardFilter> {
            override fun createFromParcel(parcelIn: Parcel): CardFilter {
                return CardFilter(parcelIn)
            }

            override fun newArray(size: Int): Array<CardFilter> {
                return Array(size, { i -> CardFilter() })
            }
        }
    }

}


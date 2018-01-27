package com.dbottillo.mtgsearchfree.model

import android.os.Parcel
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TCGPriceTest {

    lateinit var price: TCGPrice

    @Before
    fun setup() {
        price = TCGPrice()
        price.avgPrice = "avg"
        price.hiPrice = "hi"
        price.lowprice = "low"
        price.link = "link"
        price.setError("error")
        price.isNotFound = false
    }

    @Test
    fun tcgPrice_ParcelableWriteRead() {
        val parcel = Parcel.obtain()
        price.writeToParcel(parcel, price.describeContents())
        parcel.setDataPosition(0)

        val createdFromParcel = TCGPrice.CREATOR.createFromParcel(parcel)
        assertThat(createdFromParcel, `is`(price))
    }
}
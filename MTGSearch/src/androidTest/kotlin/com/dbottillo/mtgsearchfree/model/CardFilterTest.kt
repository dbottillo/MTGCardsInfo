package com.dbottillo.mtgsearchfree.model

import android.os.Parcel
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CardFilterTest {

    private lateinit var cardFilter: CardFilter

    @Before
    fun setup() {
        cardFilter = CardFilter()

        cardFilter.white = true
        cardFilter.blue = false
        cardFilter.black = true
        cardFilter.red = false
        cardFilter.green = true

        cardFilter.artifact = true
        cardFilter.eldrazi = false
        cardFilter.land = true

        cardFilter.common = true
        cardFilter.uncommon = false
        cardFilter.rare = false
        cardFilter.mythic = true
    }


    @Test
    fun should_parcel_properly_a_card_filter_object() {
        val parcel = Parcel.obtain()
        cardFilter.writeToParcel(parcel, cardFilter.describeContents())
        parcel.setDataPosition(0)

        val createdFromParcel = CardFilter.CREATOR.createFromParcel(parcel)
        assertThat(createdFromParcel, `is`<CardFilter>(cardFilter))
    }

}
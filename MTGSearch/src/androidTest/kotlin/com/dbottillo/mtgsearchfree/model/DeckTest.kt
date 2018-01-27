package com.dbottillo.mtgsearchfree.model

import android.os.Parcel
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeckTest {

    lateinit var deck: Deck

    @Before
    fun setup() {
        deck = Deck(100)
        deck.isArchived = false
        deck.name = "Standard"
        deck.numberOfCards = 20
        deck.sizeOfSideboard = 10
    }

    @Test
    fun deck_ParcelableWriteRead() {
        val parcel = Parcel.obtain()
        deck.writeToParcel(parcel, deck.describeContents())
        parcel.setDataPosition(0)

        val createdFromParcel = Deck.CREATOR.createFromParcel(parcel)
        assertThat(createdFromParcel, `is`(deck))
    }

}
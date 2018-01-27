package com.dbottillo.mtgsearchfree.model

import android.os.Parcel
import android.support.test.runner.AndroidJUnit4

import org.junit.Before
import org.junit.Test

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlayerTest {

    lateinit var player: Player

    @Before
    fun setup() {
        player = Player(2, "Jayce")
        player.setLife(13)
        player.setPoisonCount(7)
        player.setDiceResult(11)
    }

    @Test
    fun deck_ParcelableWriteRead() {
        val parcel = Parcel.obtain()
        player.writeToParcel(parcel, player.describeContents())
        parcel.setDataPosition(0)

        val createdFromParcel = Player.CREATOR.createFromParcel(parcel)
        assertThat(createdFromParcel, `is`(player))
        assertThat(createdFromParcel.getLife(), `is`(13))
        assertThat(createdFromParcel.getPoisonCount(), `is`(7))
        assertThat(createdFromParcel.getDiceResult(), `is`(11))
    }

}
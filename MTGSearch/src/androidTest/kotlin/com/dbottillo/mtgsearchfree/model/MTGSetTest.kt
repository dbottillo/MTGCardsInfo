package com.dbottillo.mtgsearchfree.model

import android.os.Parcel
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MTGSetTest {

    lateinit var set: MTGSet

    @Before
    fun setup() {
        set = MTGSet(100)
        set.setName("Zendikar")
        set.setCode("ZEN")
    }

    @Test
    fun mtgSet_ParcelableWriteRead() {
        val parcel = Parcel.obtain()
        set.writeToParcel(parcel, set.describeContents())
        parcel.setDataPosition(0)

        val createdFromParcel = MTGSet.CREATOR.createFromParcel(parcel)
        assertThat(createdFromParcel, `is`(set))
    }
}
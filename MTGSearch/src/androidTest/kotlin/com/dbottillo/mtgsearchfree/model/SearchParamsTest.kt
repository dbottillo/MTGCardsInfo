package com.dbottillo.mtgsearchfree.model

import android.os.Parcel
import android.support.test.runner.AndroidJUnit4

import org.junit.Before
import org.junit.Test

import java.util.Collections

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchParamsTest {

    lateinit var searchParams: SearchParams

    @Before
    fun setup() {
        searchParams = SearchParams()
        searchParams.name = "jayce"
        searchParams.types = "creatures"
        searchParams.text = "text"
        searchParams.cmc = CMCParam("=", 4, listOf("4"))
        searchParams.power = PTParam(">", 3)
        searchParams.tough = PTParam("<", 1)
        searchParams.isWhite = false
        searchParams.isBlack = false
        searchParams.isBlue = true
        searchParams.isRed = true
        searchParams.isGreen = false
        searchParams.setOnlyMulti(true)
        searchParams.isCommon = true
        searchParams.isUncommon = true
        searchParams.isRare = false
        searchParams.isMythic = false
        searchParams.setId = 3
    }

    @Test
    fun searchParams_ParcelableWriteRead() {
        val parcel = Parcel.obtain()
        searchParams.writeToParcel(parcel, searchParams.describeContents())
        parcel.setDataPosition(0)

        val createdFromParcel = SearchParams.CREATOR.createFromParcel(parcel)
        assertThat(createdFromParcel, `is`(searchParams))
    }

    @Test
    fun searchParams_willDetectOneColor() {
        assertTrue(searchParams.atLeastOneColor())
        searchParams.isBlue = false
        searchParams.isRed = false
        assertFalse(searchParams.atLeastOneColor())
    }

    @Test
    fun searchParams_willDetectOneRarity() {
        assertTrue(searchParams.atLeastOneRarity())
        searchParams.isCommon = false
        searchParams.isUncommon = false
        assertFalse(searchParams.atLeastOneRarity())
    }

    @Test
    fun searchParams_validation() {
        var searchParams = SearchParams()
        assertFalse(searchParams.isValid)

        searchParams = SearchParams()
        searchParams.text = "text"
        assertTrue(searchParams.isValid)

        searchParams = SearchParams()
        searchParams.types = "types"
        assertTrue(searchParams.isValid)

        searchParams = SearchParams()
        searchParams.name = "search"
        assertTrue(searchParams.isValid)

        searchParams = SearchParams()
        searchParams.cmc = CMCParam("=", 4, listOf("4"))
        assertTrue(searchParams.isValid)

        searchParams = SearchParams()
        searchParams.power = PTParam("=", 4)
        assertTrue(searchParams.isValid)

        searchParams = SearchParams()
        searchParams.tough = PTParam("=", 4)
        assertTrue(searchParams.isValid)

        searchParams = SearchParams()
        searchParams.isGreen = true
        assertTrue(searchParams.isValid)

        searchParams = SearchParams()
        searchParams.isRare = true
        assertTrue(searchParams.isValid)

        searchParams = SearchParams()
        searchParams.setId = 2
        assertTrue(searchParams.isValid)
    }

    @Test
    fun isNotValid() {
        val searchParams = SearchParams()
        assertThat(searchParams.isValid, `is`(false))

        searchParams.power = null
        assertThat(searchParams.isValid, `is`(false))
    }

    @Test
    fun searchParams_willHandleMulticolorOption() {
        val searchParams = SearchParams()
        assertFalse(searchParams.isNoMulti)
        assertFalse(searchParams.onlyMulti())
        searchParams.isNoMulti = true
        assertTrue(searchParams.isNoMulti)
        assertFalse(searchParams.onlyMulti())
        searchParams.setOnlyMulti(true)
        assertFalse(searchParams.isNoMulti)
        assertTrue(searchParams.onlyMulti())
    }

    @Test
    fun returnValidWithOnlyLandSet() {
        val searchParams = SearchParams()
        searchParams.isLand = true
        assertTrue(searchParams.isValid)
    }
}
package com.dbottillo.mtgsearchfree.model.storage

import android.support.test.runner.AndroidJUnit4
import com.dbottillo.mtgsearchfree.util.BaseContextTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CardsPreferencesImplTest : BaseContextTest() {

    lateinit var underTest: CardsPreferencesImpl

    @Before
    fun setup() {
        underTest = CardsPreferencesImpl(context)
        underTest.clear()
    }

    @Test
    fun retainsSetPosition() {
        assertThat(underTest.setPosition, `is`(0))
        underTest.saveSetPosition(2)
        assertThat(underTest.setPosition, `is`(2))
    }

    @Test
    fun retainsShowImage() {
        assertTrue(underTest.showImage())
        underTest.setShowImage(false)
        assertFalse(underTest.showImage())
    }

}
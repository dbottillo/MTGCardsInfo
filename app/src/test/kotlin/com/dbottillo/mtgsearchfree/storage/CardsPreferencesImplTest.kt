package com.dbottillo.mtgsearchfree.storage

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class CardsPreferencesImplTest {

    lateinit var underTest: CardsPreferencesImpl

    @Before
    fun setup() {
        underTest = CardsPreferencesImpl(RuntimeEnvironment.application)
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
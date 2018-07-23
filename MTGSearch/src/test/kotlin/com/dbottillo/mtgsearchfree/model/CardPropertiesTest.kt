package com.dbottillo.mtgsearchfree.model

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit

class CardPropertiesTest {

    @Rule @JvmField var mockitoRule = MockitoJUnit.rule()!!

    @Test
    fun `conversion from string to color and vice versa is correct`() {
        assertThat("White".toColorInt(), `is`(0))
        assertThat("Blue".toColorInt(), `is`(1))
        assertThat("Black".toColorInt(), `is`(2))
        assertThat("Red".toColorInt(), `is`(3))
        assertThat("Green".toColorInt(), `is`(4))

        assertThat(0.toColor(), `is`("White"))
        assertThat(1.toColor(), `is`("Blue"))
        assertThat(2.toColor(), `is`("Black"))
        assertThat(3.toColor(), `is`("Red"))
        assertThat(4.toColor(), `is`("Green"))
    }

}
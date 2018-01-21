package com.dbottillo.mtgsearchfree.model

import org.junit.Test

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat

class CardPropertiesTest {

    @Test
    fun CardProperties_areCorrect() {
        assertThat(CardProperties.COLOR.getNumberFromString("White"), `is`(0))
        assertThat(CardProperties.COLOR.getNumberFromString("Blue"), `is`(1))
        assertThat(CardProperties.COLOR.getNumberFromString("Black"), `is`(2))
        assertThat(CardProperties.COLOR.getNumberFromString("Red"), `is`(3))
        assertThat(CardProperties.COLOR.getNumberFromString("Green"), `is`(4))

        assertThat(CardProperties.COLOR.getStringFromNumber(0), `is`("White"))
        assertThat(CardProperties.COLOR.getStringFromNumber(1), `is`("Blue"))
        assertThat(CardProperties.COLOR.getStringFromNumber(2), `is`("Black"))
        assertThat(CardProperties.COLOR.getStringFromNumber(3), `is`("Red"))
        assertThat(CardProperties.COLOR.getStringFromNumber(4), `is`("Green"))
    }

}
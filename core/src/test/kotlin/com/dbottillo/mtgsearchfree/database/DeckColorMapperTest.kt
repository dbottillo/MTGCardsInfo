package com.dbottillo.mtgsearchfree.database

import com.dbottillo.mtgsearchfree.model.Color
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit

class DeckColorMapperTest {

    @JvmField @Rule val mockitoRule = MockitoJUnit.rule()!!

    lateinit var underTest: DeckColorMapper

    @Before
    fun setup() {
        underTest = DeckColorMapper(Gson())
    }

    @Test
    fun `should convert an empty list of colors`() {
        val result = underTest.convert(emptyList())

        assertThat(result).isEmpty()
    }

    @Test
    fun `should convert a list with one color`() {
        val result = underTest.convert(listOf("[W]"))

        assertThat(result).isEqualTo(listOf(Color.WHITE))
    }

    @Test
    fun `should convert a list with two colors`() {
        val result = underTest.convert(listOf("[W,U]"))

        assertThat(result).isEqualTo(listOf(Color.WHITE, Color.BLUE))
    }

    @Test
    fun `should convert a list with three colors`() {
        val result = underTest.convert(listOf("[W,U,G]"))

        assertThat(result).isEqualTo(listOf(Color.WHITE, Color.BLUE, Color.GREEN))
    }

    @Test
    fun `should convert a list with all colors`() {
        val result = underTest.convert(listOf("[W,U,B,R,G]"))

        assertThat(result).isEqualTo(listOf(Color.WHITE, Color.BLUE, Color.BLACK, Color.RED, Color.GREEN))
    }

    @Test
    fun `should convert a list with multiple colors across identities`() {
        val result = underTest.convert(listOf("[W]", "[U]", "[R]", "[G]", "[B]"))

        assertThat(result).isEqualTo(listOf(Color.WHITE, Color.BLUE, Color.BLACK, Color.RED, Color.GREEN))
    }

    @Test
    fun `should convert a list with multiple colors across identities and with duplicates`() {
        val result = underTest.convert(listOf("[W,U]", "[U,B]", "[B,R]"))

        assertThat(result).isEqualTo(listOf(Color.WHITE, Color.BLUE, Color.BLACK, Color.RED))
    }
}
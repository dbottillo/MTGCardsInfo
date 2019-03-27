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
    fun `should convert an empty list of identity colors`() {
        val result = underTest.convert(emptyList())

        assertThat(result).isEmpty()
    }

    @Test
    fun `should convert a list with one identity color`() {
        val result = underTest.convert(listOf(ColorMapperType.Identity("[W]")))

        assertThat(result).isEqualTo(listOf(Color.WHITE))
    }

    @Test
    fun `should convert a list with two identity colors`() {
        val result = underTest.convert(listOf(ColorMapperType.Identity("[W,U]")))

        assertThat(result).isEqualTo(listOf(Color.WHITE, Color.BLUE))
    }

    @Test
    fun `should convert a list with three identity colors`() {
        val result = underTest.convert(listOf(ColorMapperType.Identity("[W,U,G]")))

        assertThat(result).isEqualTo(listOf(Color.WHITE, Color.BLUE, Color.GREEN))
    }

    @Test
    fun `should convert a list with all identity colors`() {
        val result = underTest.convert(listOf(ColorMapperType.Identity("[W,U,B,R,G]")))

        assertThat(result).isEqualTo(listOf(Color.WHITE, Color.BLUE, Color.BLACK, Color.RED, Color.GREEN))
    }

    @Test
    fun `should convert a list with one display color`() {
        val result = underTest.convert(listOf(ColorMapperType.Display("W")))

        assertThat(result).isEqualTo(listOf(Color.WHITE))
    }

    @Test
    fun `should convert a list with two display colors`() {
        val result = underTest.convert(listOf(ColorMapperType.Display("W,U")))

        assertThat(result).isEqualTo(listOf(Color.WHITE, Color.BLUE))
    }

    @Test
    fun `should convert a list with three display colors`() {
        val result = underTest.convert(listOf(ColorMapperType.Display("W,U,G")))

        assertThat(result).isEqualTo(listOf(Color.WHITE, Color.BLUE, Color.GREEN))
    }

    @Test
    fun `should convert a list with all display colors`() {
        val result = underTest.convert(listOf(ColorMapperType.Display("W,U,B,R,G")))

        assertThat(result).isEqualTo(listOf(Color.WHITE, Color.BLUE, Color.BLACK, Color.RED, Color.GREEN))
    }

    @Test
    fun `should convert a list with multiple identity colors across elements`() {
        val result = underTest.convert(listOf(ColorMapperType.Identity("[W]"),
            ColorMapperType.Identity("[U]"),
            ColorMapperType.Identity("[R]"),
            ColorMapperType.Identity("[G]"),
            ColorMapperType.Identity("[B]")))

        assertThat(result).isEqualTo(listOf(Color.WHITE, Color.BLUE, Color.BLACK, Color.RED, Color.GREEN))
    }

    @Test
    fun `should convert a list with multiple display colors across elements`() {
        val result = underTest.convert(listOf(ColorMapperType.Display("W"),
            ColorMapperType.Display("U"),
            ColorMapperType.Display("R"),
            ColorMapperType.Display("G"),
            ColorMapperType.Display("B")))

        assertThat(result).isEqualTo(listOf(Color.WHITE, Color.BLUE, Color.BLACK, Color.RED, Color.GREEN))
    }

    @Test
    fun `should convert a list with multiple identity colors across elements and with duplicates`() {
        val result = underTest.convert(listOf(ColorMapperType.Identity("[W,U]"),
            ColorMapperType.Identity("[U,B]"),
            ColorMapperType.Identity("[B,R]")))

        assertThat(result).isEqualTo(listOf(Color.WHITE, Color.BLUE, Color.BLACK, Color.RED))
    }

    @Test
    fun `should convert a list with multiple display colors across elements and with duplicates`() {
        val result = underTest.convert(listOf(ColorMapperType.Display("W,U"),
            ColorMapperType.Display("U,B"),
            ColorMapperType.Display("B,R")))

        assertThat(result).isEqualTo(listOf(Color.WHITE, Color.BLUE, Color.BLACK, Color.RED))
    }

    @Test
    fun `should convert a list with multiple display and identities colors across elements`() {
        val result = underTest.convert(listOf(ColorMapperType.Identity("[W,U]"),
            ColorMapperType.Display("U"),
            ColorMapperType.Display("R"),
            ColorMapperType.Identity("[G,B]"),
            ColorMapperType.Display("B")))

        assertThat(result).isEqualTo(listOf(Color.WHITE, Color.BLUE, Color.BLACK, Color.RED, Color.GREEN))
    }
}
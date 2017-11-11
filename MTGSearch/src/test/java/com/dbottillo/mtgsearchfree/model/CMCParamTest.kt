package com.dbottillo.mtgsearchfree.model

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Test

class CMCParamTest {

    @Test
    fun `should create cmc param null if value is null`() {
        assertNull(cmcParamCreator("=", null))
    }

    @Test
    fun `should create cmc param for = 5`() {
        val result = cmcParamCreator("=", "5")

        assertNotNull(result)
        result?.let {
            assertThat(it.operator, `is`("="))
            assertThat(it.numericValue, `is`(5))
            assertThat(it.stringValues, `is`(listOf("5")))
        }
    }

    @Test
    fun `should create cmc param for less or equal than 5`() {
        val result = cmcParamCreator("<=", "5")

        assertNotNull(result)
        result?.let {
            assertThat(it.operator, `is`("<="))
            assertThat(it.numericValue, `is`(5))
            assertThat(it.stringValues, `is`(listOf("5")))
        }
    }

    @Test
    fun `should create cmc param for = 2WU`() {
        val result = cmcParamCreator("=", "2wu")

        assertNotNull(result)
        result?.let {
            assertThat(it.operator, `is`("="))
            assertThat(it.numericValue, `is`(4))
            assertThat(it.stringValues, `is`(listOf("2", "W", "U")))
        }
    }

    @Test
    fun `should create cmc param for greater than 2WU`() {
        val result = cmcParamCreator(">", "2WU")

        assertNotNull(result)
        result?.let {
            assertThat(it.operator, `is`(">"))
            assertThat(it.numericValue, `is`(4))
            assertThat(it.stringValues, `is`(listOf("2", "W", "U")))
        }
    }

    @Test
    fun `should create cmc param for greater or equal than 2WU`() {
        val result = cmcParamCreator(">=", "20WU")

        assertNotNull(result)
        result?.let {
            assertThat(it.operator, `is`(">="))
            assertThat(it.numericValue, `is`(22))
            assertThat(it.stringValues, `is`(listOf("20", "W", "U")))
        }
    }

    @Test
    fun `should create cmc param for greater or equal than 2WWU`() {
        val result = cmcParamCreator(">=", "2WWU")

        assertNotNull(result)
        result?.let {
            assertThat(it.operator, `is`(">="))
            assertThat(it.numericValue, `is`(5))
            assertThat(it.stringValues, `is`(listOf("2", "WW", "U")))
        }
    }

    @Test
    fun `should create cmc param for equal than X2U`() {
        val result = cmcParamCreator("=", "X2U")

        assertNotNull(result)
        result?.let {
            assertThat(it.operator, `is`("="))
            assertThat(it.numericValue, `is`(3))
            assertThat(it.stringValues, `is`(listOf("X", "2", "U")))
        }
    }

    @Test
    fun `should create cmc param for greater or equal than X2U`() {
        val result = cmcParamCreator(">=", "X2U")

        assertNotNull(result)
        result?.let {
            assertThat(it.operator, `is`(">="))
            assertThat(it.numericValue, `is`(3))
            assertThat(it.stringValues, `is`(listOf("X", "2", "U")))
        }
    }
}
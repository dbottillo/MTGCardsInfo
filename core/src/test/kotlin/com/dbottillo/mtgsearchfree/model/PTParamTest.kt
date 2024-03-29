package com.dbottillo.mtgsearchfree.model

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit

class PTParamTest {

    @Rule
    @JvmField
    var mockitoRule = MockitoJUnit.rule()

    @Test
    fun `should create pt param null if value is null`() {
        val result = ptParamCreator("=", null)

        assertNull(result)
    }

    @Test
    fun `should create pt param with IS operator and value -1 is input value is *`() {
        val result = ptParamCreator(">", "*")

        assertNotNull(result)
        result?.let {
            assertThat(it.operator, `is`("IS"))
            assertThat(it.value, `is`(-1))
        }
    }

    @Test
    fun `should create pt param with = operator and value 5 is input value is =5`() {
        val result = ptParamCreator(">", "5")

        assertNotNull(result)
        result?.let {
            assertThat(it.operator, `is`(">"))
            assertThat(it.value, `is`(5))
        }
    }

    @Test
    fun `should create pt param with GR= operator and value 5 is input value is GR=5`() {
        val result = ptParamCreator(">=", "5")

        assertNotNull(result)
        result?.let {
            assertThat(it.operator, `is`(">="))
            assertThat(it.value, `is`(5))
        }
    }

    @Test
    fun `should create pt param with LS= operator and value 5 is input value is LS=5`() {
        val result = ptParamCreator("<=", "5")

        assertNotNull(result)
        result?.let {
            assertThat(it.operator, `is`("<="))
            assertThat(it.value, `is`(5))
        }
    }

    @Test
    fun `should create pt param with LS operator and value 5 is input value is LS5`() {
        val result = ptParamCreator("<", "5")

        assertNotNull(result)
        result?.let {
            assertThat(it.operator, `is`("<"))
            assertThat(it.value, `is`(5))
        }
    }

    @Test
    fun `should create pt param with GR operator and value 5 is input value is GR5`() {
        val result = ptParamCreator(">", "5")

        assertNotNull(result)
        result?.let {
            assertThat(it.operator, `is`(">"))
            assertThat(it.value, `is`(5))
        }
    }
}
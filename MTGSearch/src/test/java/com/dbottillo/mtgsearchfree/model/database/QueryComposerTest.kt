package com.dbottillo.mtgsearchfree.model.database

import com.dbottillo.mtgsearchfree.model.CMCParam
import com.dbottillo.mtgsearchfree.model.PTParam
import junit.framework.Assert.assertNotNull
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Test

class QueryComposerTest {

    @Test
    fun generateQueryFromConstructor() {
        val queryComposer = QueryComposer("SELECT * from TABLE")
        val output = queryComposer.build()
        assertNotNull(output.query)
        assertThat(output.query, `is`("SELECT * from TABLE"))
        assertTrue(output.selection.isEmpty())
    }

    @Test
    fun appendQueryManually() {
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.append("ORDER BY NAME LIMIT 400")

        val output = queryComposer.build()
        assertNotNull(output.query)

        assertThat(output.query, `is`("SELECT * from TABLE ORDER BY NAME LIMIT 400"))
        assertTrue(output.selection.isEmpty())
    }

    @Test
    fun generateQueryWithOneParameter() {
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addParam("NAME", "=", "island")

        val output = queryComposer.build()

        assertThat(output.query, `is`("SELECT * from TABLE WHERE NAME = ?"))
        assertThat(output.selection.size, `is`(1))
        assertThat(output.selection[0], `is`("island"))
    }

    @Test
    fun `should generate power-toughness param`() {
        val intParam = PTParam(">", 3)
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addPTParam("NAME", intParam)

        val output = queryComposer.build()

        assertThat(output.query, `is`("SELECT * from TABLE WHERE (CAST(NAME as integer) > ? AND CAST(NAME as integer) > 0)"))
        assertThat(output.selection.size, `is`(1))
        assertThat(output.selection[0], `is`("3"))
    }

    @Test
    fun generateQueryWithLikeParameter() {
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addLikeParam("NAME", "island")

        val output = queryComposer.build()

        assertThat(output.query, `is`("SELECT * from TABLE WHERE NAME LIKE ?"))
        assertThat(output.selection.size, `is`(1))
        assertThat(output.selection[0], `is`("%island%"))
    }

    @Test
    fun generateQueryWithTwoParameters() {
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addParam("NAME", "=", "island")
        queryComposer.addParam("CMC", "<=", 0)

        val output = queryComposer.build()

        assertThat(output.query, `is`("SELECT * from TABLE WHERE NAME = ? AND (CAST(CMC as integer) <= ? AND CMC != '')"))
        assertThat(output.selection.size, `is`(2))
        assertThat(output.selection[0], `is`("island"))
        assertThat(output.selection[1], `is`("0"))
    }

    @Test
    fun generateQueryWithIntParamBeforeStringParam() {
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addParam("CMC", ">", 0)
        queryComposer.addParam("NAME", "=", "island")

        val output = queryComposer.build()

        assertThat(output.query, `is`("SELECT * from TABLE WHERE (CAST(CMC as integer) > ? AND CMC != '') AND NAME = ?"))
        assertThat(output.selection.size, `is`(2))
        assertThat(output.selection[0], `is`("0"))
        assertThat(output.selection[1], `is`("island"))
    }

    @Test
    fun generateQueryWithMultipleParamValues() {
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addMultipleParam("rarity", "=", "OR", "Uncommon", "Rare")

        val output = queryComposer.build()

        assertThat(output.query, `is`("SELECT * from TABLE WHERE (rarity = ? OR rarity = ?)"))
        assertThat(output.selection.size, `is`(2))
        assertThat(output.selection[0], `is`("Uncommon"))
        assertThat(output.selection[1], `is`("Rare"))
    }

    @Test
    fun generateQueryWithMultipleLikeParamValues() {
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addMultipleParam("types", "LIKE", "OR", "Creature", "Dragon")

        val output = queryComposer.build()

        assertThat(output.query, `is`("SELECT * from TABLE WHERE (types LIKE ? OR types LIKE ?)"))
        assertThat(output.selection.size, `is`(2))
        assertThat(output.selection[0], `is`("%Creature%"))
        assertThat(output.selection[1], `is`("%Dragon%"))
    }

    @Test
    fun ignoreEmptyOrNullColumnName() {
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addParam(null, "=", "island")
        queryComposer.addParam("", "=", "island")

        val output = queryComposer.build()

        assertThat(output.query, `is`("SELECT * from TABLE"))
        assertThat(output.selection.size, `is`(0))
    }

    @Test
    fun ignoreEmptyParam() {
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addParam("name", "=", "")

        val output = queryComposer.build()

        assertThat(output.query, `is`("SELECT * from TABLE"))
        assertThat(output.selection.size, `is`(0))
    }

    @Test
    fun ignoreEmptyIntParam() {
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addPTParam("name", null)

        val output = queryComposer.build()

        assertThat(output.query, `is`("SELECT * from TABLE"))
        assertThat(output.selection.size, `is`(0))
    }

    @Test
    fun ignoresEmptyMultipleParams() {
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addMultipleParam("rarity", "OR", "LIKE")

        val output = queryComposer.build()

        assertThat(output.query, `is`("SELECT * from TABLE"))
        assertThat(output.selection.size, `is`(0))
    }

    @Test
    fun generateQueryWithNameAndMultipleParamValues() {
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addParam("NAME", "LIKE", "island")
        queryComposer.addMultipleParam("rarity", "=", "OR", "Uncommon", "Rare")

        val output = queryComposer.build()

        assertThat(output.query, `is`("SELECT * from TABLE WHERE NAME LIKE ? AND (rarity = ? OR rarity = ?)"))
        assertThat(output.selection.size, `is`(3))
        assertThat(output.selection[0], `is`("%island%"))
        assertThat(output.selection[1], `is`("Uncommon"))
        assertThat(output.selection[2], `is`("Rare"))
    }

    @Test
    fun `should generate query for * power`() {
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addPTParam(name = "power", ptParam = PTParam(operator = "IS", value = -1))

        val output = queryComposer.build()
        assertThat(output.query, `is`("SELECT * from TABLE WHERE power LIKE ?"))
        assertThat(output.selection.size, `is`(1))
        assertThat(output.selection[0], `is`("%*%"))
    }

    @Test
    fun `should generate query for * toughness`() {
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addPTParam(name = "toughness", ptParam = PTParam(operator = "IS", value = -1))

        val output = queryComposer.build()
        assertThat(output.query, `is`("SELECT * from TABLE WHERE toughness LIKE ?"))
        assertThat(output.selection.size, `is`(1))
        assertThat(output.selection[0], `is`("%*%"))
    }

    @Test
    fun `should generate query for * power and toughness`() {
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addPTParam(name = "power", ptParam = PTParam(operator = "IS", value = -1))
        queryComposer.addPTParam(name = "toughness", ptParam = PTParam(operator = "IS", value = -1))

        val output = queryComposer.build()
        assertThat(output.query, `is`("SELECT * from TABLE WHERE power LIKE ? AND toughness LIKE ?"))
        assertThat(output.selection.size, `is`(2))
        assertThat(output.selection[0], `is`("%*%"))
        assertThat(output.selection[1], `is`("%*%"))
    }

    @Test
    fun `should generate cmc param for = 5`() {
        val intParam = CMCParam("=", 5, listOf("5"))
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addCMCParam(intParam)

        val output = queryComposer.build()

        assertThat(output.query, `is`("SELECT * from TABLE WHERE cmc=?"))
        assertThat(output.selection.size, `is`(1))
        assertThat(output.selection[0], `is`("5"))
    }

    @Test
    fun `should generate cmc param for LESS OR EQUAL than 5`() {
        val intParam = CMCParam("<=", 5, listOf("5"))
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addCMCParam(intParam)

        val output = queryComposer.build()

        assertThat(output.query, `is`("SELECT * from TABLE WHERE cmc<=?"))
        assertThat(output.selection.size, `is`(1))
        assertThat(output.selection[0], `is`("5"))
    }

    @Test
    fun `should generate cmc param for = 2WU`() {
        val intParam = CMCParam("=", 4, listOf("2", "W", "U"))
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addCMCParam(intParam)

        val output = queryComposer.build()

        assertThat(output.query, `is`("SELECT * from TABLE WHERE manaCost = ?"))
        assertThat(output.selection.size, `is`(1))
        assertThat(output.selection[0], `is`("{2}{W}{U}"))
    }

    @Test
    fun `should generate cmc param for GREATER THAN 2WU`() {
        val intParam = CMCParam(">", 4, listOf("2", "W", "U"))
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addCMCParam(intParam)

        val output = queryComposer.build()

        assertThat(output.query, `is`("SELECT * from TABLE WHERE cmc>? AND manaCost LIKE ? AND manaCost LIKE ?"))
        assertThat(output.selection.size, `is`(3))
        assertThat(output.selection[0], `is`("4"))
        assertThat(output.selection[1], `is`("%{W}%"))
        assertThat(output.selection[2], `is`("%{U}%"))
    }

    @Test
    fun `should generate cmc param for LESS OR EQUAL THAN 2WWU`() {
        val intParam = CMCParam("<=", 5, listOf("2", "WW", "U"))
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addCMCParam(intParam)

        val output = queryComposer.build()

        assertThat(output.query, `is`("SELECT * from TABLE WHERE cmc<=? AND manaCost LIKE ? AND manaCost LIKE ?"))
        assertThat(output.selection.size, `is`(3))
        assertThat(output.selection[0], `is`("5"))
        assertThat(output.selection[1], `is`("%{W}{W}%"))
        assertThat(output.selection[2], `is`("%{U}%"))
    }

    @Test
    fun `should generate cmc param for EQUAL to X2U`() {
        val intParam = CMCParam("=", 3, listOf("X", "2", "U"))
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addCMCParam(intParam)

        val output = queryComposer.build()

        assertThat(output.query, `is`("SELECT * from TABLE WHERE manaCost = ?"))
        assertThat(output.selection.size, `is`(1))
        assertThat(output.selection[0], `is`("{X}{2}{U}"))
    }

    @Test
    fun `should generate cmc param for GREATER OR EQUAL to X2U`() {
        val intParam = CMCParam(">=", 3, listOf("X", "2", "U"))
        val queryComposer = QueryComposer("SELECT * from TABLE")
        queryComposer.addCMCParam(intParam)

        val output = queryComposer.build()

        assertThat(output.query, `is`("SELECT * from TABLE WHERE cmc>=? AND manaCost LIKE ? AND manaCost LIKE ? AND manaCost LIKE ?"))
        assertThat(output.selection.size, `is`(4))
        assertThat(output.selection[0], `is`("3"))
        assertThat(output.selection[1], `is`("%{X}%"))
        assertThat(output.selection[2], `is`("%{2}%"))
        assertThat(output.selection[3], `is`("%{U}%"))
    }
}
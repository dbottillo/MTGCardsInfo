package com.dbottillo.mtgsearchfree.model

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*

class MTGCardTest{
    
    private lateinit var card: MTGCard

    @Before
    fun setup() {
        val set = MTGSet(2, "Zendikar")
        set.setCode("ZEN")
        card = MTGCard(1)
        card.isSideboard = true
        card.multiVerseId = 200
        card.isLand = false
        card.isArtifact = true
        card.setCardName("Name")
        card.cmc = 2
        card.colors = Arrays.asList(1, 3)
        card.belongsTo(set)
        card.layout = "layout"
        card.manaCost = "3UW"
        card.isMultiColor = true
        card.number = "23"
        card.power = "2"
        card.quantity = 23
        card.rarity = CardProperties.RARITY.MYTHIC.key
        card.text = "text"
        card.toughness = "4"
        card.type = "Creature"
        card.names = Arrays.asList("one", "two")
        card.superTypes = Arrays.asList("legendary", "creature")
        card.flavor = "flavor"
        card.artist = "artist"
        card.loyalty = 2
        card.printings = Arrays.asList("C16", "C15")
    }

    @Test
    fun `should parse color properly`() {
        val other = MTGCard(2)
        other.addColor(CardProperties.COLOR.WHITE.key)
        assertTrue(other.colors.contains(CardProperties.COLOR.WHITE.value))
        other.addColor(CardProperties.COLOR.BLUE.key)
        assertTrue(other.colors.contains(CardProperties.COLOR.BLUE.value))
        other.addColor(CardProperties.COLOR.BLACK.key)
        assertTrue(other.colors.contains(CardProperties.COLOR.BLACK.value))
        other.addColor(CardProperties.COLOR.RED.key)
        assertTrue(other.colors.contains(CardProperties.COLOR.RED.value))
        other.addColor(CardProperties.COLOR.GREEN.key)
        assertTrue(other.colors.contains(CardProperties.COLOR.GREEN.value))
    }

    @Test
    fun `should detect eldrazi`() {
        var other = MTGCard(1)
        other.addColor(CardProperties.COLOR.WHITE.key)
        assertFalse(other.isEldrazi)
        other = MTGCard(1)
        other.isMultiColor = true
        assertFalse(other.isEldrazi)
        other = MTGCard(1)
        other.isLand = true
        assertFalse(other.isEldrazi)
        other = MTGCard(1)
        other.isArtifact = true
        assertFalse(other.isEldrazi)
        other = MTGCard(1)
        assertTrue(other.isEldrazi)
    }

    @Test
    fun `should retrieve single color`() {
        var card = MTGCard(1)
        card.addColor(CardProperties.COLOR.WHITE.key)
        card.addColor(CardProperties.COLOR.BLUE.key)
        card.isMultiColor = true
        assertThat(card.singleColor, `is`<Int>(-1))
        card = MTGCard(1)
        card.addColor(CardProperties.COLOR.BLUE.key)
        assertThat(card.singleColor, `is`<Int>(CardProperties.COLOR.BLUE.value))
    }
}
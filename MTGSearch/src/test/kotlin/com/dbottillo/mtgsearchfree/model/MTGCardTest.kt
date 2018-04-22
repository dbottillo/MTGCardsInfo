package com.dbottillo.mtgsearchfree.model

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*

class MTGCardTest {

    private lateinit var card: MTGCard

    @Before
    fun setup() {
        val set = MTGSet(id = 2, code = "ZEN", name = "Zendicar")
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
        card.rarity = "Mythic Rare"
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
        other.addColor("White")
        assertTrue(other.colors.contains(0))
        other.addColor("Blue")
        assertTrue(other.colors.contains(1))
        other.addColor("Black")
        assertTrue(other.colors.contains(2))
        other.addColor("Red")
        assertTrue(other.colors.contains(3))
        other.addColor("Green")
        assertTrue(other.colors.contains(4))
    }

    @Test
    fun `should detect eldrazi`() {
        var other = MTGCard(1)
        other.addColor("White")
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
        card.addColor("White")
        card.addColor("Blue")
        card.isMultiColor = true
        assertThat(card.singleColor, `is`(-1))
        card = MTGCard(1)
        card.addColor("Blue")
        assertThat(card.singleColor, `is`(1))
    }
}
package com.dbottillo.mtgsearchfree.model.storage

import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.support.test.runner.AndroidJUnit4
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.database.CardDataSource
import com.dbottillo.mtgsearchfree.model.database.DeckDataSource
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource
import com.dbottillo.mtgsearchfree.util.*
import com.google.gson.Gson
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.FileNotFoundException
import java.io.InputStream

@RunWith(AndroidJUnit4::class)
class DecksStorageIntegrationTest : BaseContextTest() {

    lateinit var storage: DecksStorage

    @Before
    @Throws(FileNotFoundException::class)
    fun setup() {
        val fileUtil = FileUtil(FileManagerForTest())
        val cardDataSource = CardDataSource(cardsInfoDbHelper.writableDatabase, Gson())
        val mtgCardDataSource = MTGCardDataSource(mtgDatabaseHelper.readableDatabase, cardDataSource)
        val deckDataSource = DeckDataSource(cardsInfoDbHelper.writableDatabase, cardDataSource, mtgCardDataSource)
        storage = DecksStorageImpl(fileUtil, deckDataSource, Logger())
    }

    @Test
    @Throws(MTGException::class)
    fun DecksStorage_willImportEldraziDeck() {
        val decks = storage.importDeck(Uri.parse("assets/Eldrazi.dec"))
        assertTrue(decks.isNotEmpty())
        val deck = decks[0]
        assertThat(deck.name, `is`("NAME: "))
        assertThat(deck.numberOfCards, `is`(60))
        assertThat(deck.sizeOfSideboard, `is`(15))
        val cards = storage.loadDeck(deck).allCards()
        assertCardInDeck(cards, "Chalice of the Void", 4)
        assertCardInDeck(cards, "Ancient Tomb", 4)
        assertCardInDeck(cards, "City of Traitors", 2)
        assertCardInDeck(cards, "Eldrazi Temple", 4)
        assertCardInDeck(cards, "Eye of Ugin", 3)
        assertCardInDeck(cards, "Urborg, Tomb of Yawgmoth", 2)
        assertCardInDeck(cards, "Cavern of Souls", 4)
        assertCardInDeck(cards, "Reality Smasher", 4)
        assertCardInDeck(cards, "Thought-Knot Seer", 4)
        assertCardInDeck(cards, "Endless One", 4)
        assertCardInDeck(cards, "Eldrazi Mimic", 4)
        assertCardInDeck(cards, "Matter Reshaper", 4)
        assertCardInDeck(cards, "Thorn of Amethyst", 3)
        assertCardInDeck(cards, "Warping Wail", 2)
        assertCardInDeck(cards, "Dismember", 1)
        assertCardInDeck(cards, "Umezawa's Jitte", 2)
        assertCardInDeck(cards, "Endbringer", 2)
        assertCardInDeck(cards, "Wasteland", 4)
        assertCardInDeck(cards, "Crystal Vein", 1)
        assertCardInDeck(cards, "Phyrexian Metamorph", 2)

        assertCardInSideboardDeck(cards, "Warping Wail", 1)
        assertCardInSideboardDeck(cards, "Sphere of Resistance", 2)
        assertCardInSideboardDeck(cards, "Leyline of the Void", 4)
        assertCardInSideboardDeck(cards, "Helm of Obedience", 2)
        assertCardInSideboardDeck(cards, "Ulamog, the Ceaseless Hunger", 1)
        assertCardInSideboardDeck(cards, "All Is Dust", 2)
        assertCardInSideboardDeck(cards, "Ratchet Bomb", 3)
    }

    @Test
    @Throws(MTGException::class)
    fun DecksStorage_willImportGBRampDeck() {
        val decks = storage.importDeck(Uri.parse("assets/GB_Ramp.dec"))
        assertTrue(decks.isNotEmpty())
        val deck = decks[0]
        assertThat(deck.name, `is`("GB Ramp, a Standard deck by CLYDE THE GLIDE DREXLER"))
        assertThat(deck.numberOfCards, `is`(60))
        assertThat(deck.sizeOfSideboard, `is`(15))
        val cards = storage.loadDeck(deck).allCards()
        assertCardInDeck(cards, "Blisterpod", 4)
        assertCardInDeck(cards, "Catacomb Sifter", 4)
        assertCardInDeck(cards, "Duskwatch Recruiter", 4)
        assertCardInDeck(cards, "Elvish Visionary", 4)
        assertCardInDeck(cards, "Liliana, Heretical Healer", 2)
        assertCardInDeck(cards, "Loam Dryad", 4)
        assertCardInDeck(cards, "Nantuko Husk", 4)
        assertCardInDeck(cards, "Zulaport Cutthroat", 4)
        assertCardInDeck(cards, "Collected Company", 4)
        assertCardInDeck(cards, "Cryptolith Rite", 3)
        assertCardInDeck(cards, "Hissing Quagmire", 4)
        assertCardInDeck(cards, "Forest", 8)
        assertCardInDeck(cards, "Llanowar Wastes", 4)
        assertCardInDeck(cards, "Swamp", 4)
        assertCardInDeck(cards, "Westvale Abbey", 3)

        assertCardInSideboardDeck(cards, "Duress", 3)
        assertCardInSideboardDeck(cards, "Evolutionary Leap", 2)
        assertCardInSideboardDeck(cards, "Fleshbag Marauder", 3)
        assertCardInSideboardDeck(cards, "Minister of Pain", 2)
        assertCardInSideboardDeck(cards, "Nissa, Vastwood Seer", 1)
        assertCardInSideboardDeck(cards, "Pitiless Horde", 2)
        assertCardInSideboardDeck(cards, "Transgress the Mind", 2)
    }

    @Test
    @Throws(MTGException::class)
    fun DecksStorage_willImportProtourDeck() {
        val decks = storage.importDeck(Uri.parse("assets/protour.txt"))
        assertTrue(decks.isNotEmpty())
        val deck = decks[0]
        assertThat(deck.name, `is`("protour.txt"))
        assertThat(deck.numberOfCards, `is`(60))
        assertThat(deck.sizeOfSideboard, `is`(15))
        val cards = storage.loadDeck(deck).allCards()
        assertCardInDeck(cards, "Elspeth, Sun's Champion", 2)
        assertCardInDeck(cards, "Obzedat, Ghost Council", 1)
        assertCardInDeck(cards, "Desecration Demon", 3)
        assertCardInDeck(cards, "Pack Rat", 4)
        assertCardInDeck(cards, "Lifebane Zombie", 3)
        assertCardInDeck(cards, "Blood Baron of Vizkopa", 3)
        assertCardInDeck(cards, "Thoughtseize", 4)
        assertCardInDeck(cards, "Devour Flesh", 2)
        assertCardInDeck(cards, "Bile Blight", 3)
        assertCardInDeck(cards, "Ultimate Price", 1)
        assertCardInDeck(cards, "Hero's Downfall", 3)
        assertCardInDeck(cards, "Underworld Connections", 3)
        assertCardInDeck(cards, "Banishing Light", 2)
        assertCardInDeck(cards, "Swamp", 8)
        assertCardInDeck(cards, "Plains", 1)
        assertCardInDeck(cards, "Godless Shrine", 4)
        assertCardInDeck(cards, "Temple of Silence", 4)
        assertCardInDeck(cards, "Urborg, Tomb of Yawgmoth", 1)
        assertCardInDeck(cards, "Mutavault", 4)
        assertCardInDeck(cards, "Caves of Koilos", 4)
        assertCardInSideboardDeck(cards, "Underworld Connections", 1)
        assertCardInSideboardDeck(cards, "Duress", 3)
        assertCardInSideboardDeck(cards, "Erebos, God of the Dead", 1)
        assertCardInSideboardDeck(cards, "Sin Collector", 1)
        assertCardInSideboardDeck(cards, "Drown in Sorrow", 2)
        assertCardInSideboardDeck(cards, "Last Breath", 2)
        assertCardInSideboardDeck(cards, "Doom Blade", 3)
        assertCardInSideboardDeck(cards, "Deicide", 2)
    }

    private fun getCardFromDeck(cards: List<MTGCard>, name: String, side: Boolean): MTGCard? {
        return cards.firstOrNull { it.name.contains(name) && it.isSideboard == side }
    }

    private fun assertCardInDeck(cards: List<MTGCard>, name: String, quantity: Int) {
        val card = getCardFromDeck(cards, name, false)
        assertNotNull(card)
        assertThat(card?.quantity, `is`(quantity))
        assertTrue(card?.isSideboard == false)
    }

    private fun assertCardInSideboardDeck(cards: List<MTGCard>, name: String, quantity: Int) {
        val card = getCardFromDeck(cards, name, true)
        assertNotNull(card)
        assertThat(card?.quantity, `is`(quantity))
        assertTrue(card?.isSideboard == true)
    }

    private inner class FileManagerForTest : FileManagerI {
        override fun saveBitmapToFile(bitmap: Bitmap): Uri {
            return Uri.parse("")
        }

        @Throws(FileNotFoundException::class)
        override fun loadUri(uri: Uri): InputStream {
            return javaClass.classLoader.getResourceAsStream(uri.toString())
        }

        @Throws(Resources.NotFoundException::class)
        override fun loadRaw(raw: Int): String {
            return ""
        }
    }

}
package com.dbottillo.mtgsearchfree.model.database

import android.database.Cursor
import com.dbottillo.mtgsearchfree.model.Rarity
import com.dbottillo.mtgsearchfree.model.storage.database.CardDataSource
import com.dbottillo.mtgsearchfree.model.toColor
import com.dbottillo.mtgsearchfree.util.LOG
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.whenever
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class CardDataSourceIntegrationTest {

    @Rule @JvmField var rule = MockitoJUnit.rule()!!

    @Mock
    lateinit var cursor: Cursor

    lateinit var mtgCardDataSource: MTGCardDataSource
    lateinit var cardsInfoDbHelper: CardsInfoDbHelper
    lateinit var mtgDatabaseHelper: MTGDatabaseHelper
    lateinit var underTest: CardDataSource

    @Before
    fun setup() {
        mtgDatabaseHelper = MTGDatabaseHelper(RuntimeEnvironment.application)
        cardsInfoDbHelper = CardsInfoDbHelper(RuntimeEnvironment.application)
        underTest = CardDataSource(cardsInfoDbHelper.writableDatabase, Gson())
        mtgCardDataSource = MTGCardDataSource(mtgDatabaseHelper.readableDatabase, underTest)
    }

    @After
    fun tearDown() {
        cardsInfoDbHelper.clear()
        cardsInfoDbHelper.close()
        mtgDatabaseHelper.close()
    }

    @Test
    fun test_generate_table_is_correct() {
        val query = CardDataSource.generateCreateTable()
        assertNotNull(query)
        assertThat(query, `is`("CREATE TABLE IF NOT EXISTS MTGCard (_id INTEGER PRIMARY KEY, name TEXT,type TEXT,types TEXT,subtypes TEXT,colors TEXT,cmc INTEGER,rarity TEXT,power TEXT,toughness TEXT,manaCost TEXT,text TEXT,multicolor INTEGER,land INTEGER,artifact INTEGER,multiVerseId INTEGER,setId INTEGER,setName TEXT,rulings TEXT,layout TEXT,setCode TEXT,number TEXT,names TEXT,supertypes TEXT,flavor TEXT,artist TEXT,loyalty INTEGER,printings TEXT,legalities TEXT,originalText TEXT,colorIdentity TEXT,uuid TEXT)"))
        assertThat(CardDataSource.generateCreateTable(1), `is`("CREATE TABLE IF NOT EXISTS MTGCard (_id INTEGER PRIMARY KEY, name TEXT,type TEXT,types TEXT,subtypes TEXT,colors TEXT,cmc INTEGER,rarity TEXT,power TEXT,toughness TEXT,manaCost TEXT,text TEXT,multicolor INTEGER,land INTEGER,artifact INTEGER,multiVerseId INTEGER,setId INTEGER,setName TEXT)"))
        assertThat(CardDataSource.generateCreateTable(2), `is`("CREATE TABLE IF NOT EXISTS MTGCard (_id INTEGER PRIMARY KEY, name TEXT,type TEXT,types TEXT,subtypes TEXT,colors TEXT,cmc INTEGER,rarity TEXT,power TEXT,toughness TEXT,manaCost TEXT,text TEXT,multicolor INTEGER,land INTEGER,artifact INTEGER,multiVerseId INTEGER,setId INTEGER,setName TEXT,rulings TEXT,layout TEXT)"))
        assertThat(CardDataSource.generateCreateTable(3), `is`("CREATE TABLE IF NOT EXISTS MTGCard (_id INTEGER PRIMARY KEY, name TEXT,type TEXT,types TEXT,subtypes TEXT,colors TEXT,cmc INTEGER,rarity TEXT,power TEXT,toughness TEXT,manaCost TEXT,text TEXT,multicolor INTEGER,land INTEGER,artifact INTEGER,multiVerseId INTEGER,setId INTEGER,setName TEXT,rulings TEXT,layout TEXT,setCode TEXT,number TEXT)"))
    }

    @Test
    fun test_card_can_be_saved_in_database() {
        val card = mtgCardDataSource.getRandomCard(1)[0]
        val id = underTest.saveCard(card)
        val cursor = cardsInfoDbHelper.readableDatabase.rawQuery("select * from " + CardDataSource.TABLE + " where rowid =?", arrayOf(id.toString() + ""))
        assertNotNull(cursor)
        assertThat(cursor.count, `is`(1))
        cursor.moveToFirst()
        val cardFromDb = underTest.fromCursor(cursor, true)
        assertNotNull(cardFromDb)
        assertThat(cardFromDb.name, `is`(card.name))
        assertThat(cardFromDb.type, `is`(card.type))
        assertThat(cardFromDb.subTypes.size, `is`(card.subTypes.size))
        for (i in 0 until cardFromDb.subTypes.size) {
            assertThat(cardFromDb.subTypes[i], `is`(card.subTypes[i]))
        }
        assertThat(cardFromDb.colors.size, `is`(card.colors.size))
        for (i in 0 until cardFromDb.colors.size) {
            assertThat(cardFromDb.colors[i], `is`(card.colors[i]))
        }
        assertThat(cardFromDb.cmc, `is`(card.cmc))
        assertThat(cardFromDb.rarity, `is`(card.rarity))
        assertThat(cardFromDb.power, `is`(card.power))
        assertThat(cardFromDb.toughness, `is`(card.toughness))
        assertThat(cardFromDb.manaCost, `is`(card.manaCost))
        assertThat(cardFromDb.text, `is`(card.text))
        assertThat(cardFromDb.isMultiColor, `is`(card.isMultiColor))
        assertThat(cardFromDb.isLand, `is`(card.isLand))
        assertThat(cardFromDb.isArtifact, `is`(card.isArtifact))
        assertThat(cardFromDb.isEldrazi, `is`(card.isEldrazi))
        assertThat(cardFromDb.set, `is`(card.set))
        assertThat(cardFromDb.layout, `is`(card.layout))
        assertThat<String>(cardFromDb.number, `is`<String>(card.number))
        assertThat(cardFromDb.rulings.size, `is`(card.rulings.size))
        for (i in 0 until cardFromDb.rulings.size) {
            assertThat(cardFromDb.rulings[i], `is`(card.rulings[i]))
        }

        assertThat(cardFromDb.names.size, `is`(card.names.size))
        for (i in 0 until cardFromDb.names.size) {
            assertThat(cardFromDb.names[i], `is`(card.names[i]))
        }

        assertThat(cardFromDb.superTypes.size, `is`(card.superTypes.size))
        for (i in 0 until cardFromDb.superTypes.size) {
            assertThat(cardFromDb.superTypes[i], `is`(card.superTypes[i]))
        }
        assertThat(cardFromDb.loyalty, `is`(card.loyalty))
        assertThat(cardFromDb.artist, `is`(card.artist))
        assertThat<String>(cardFromDb.flavor, `is`<String>(card.flavor))

        assertThat(cardFromDb.printings, `is`(card.printings))
        for (i in 0 until cardFromDb.printings.size) {
            assertThat(cardFromDb.printings[i], `is`(card.printings[i]))
        }
        assertThat(cardFromDb.originalText, `is`(card.originalText))

        assertThat<List<String>>(cardFromDb.colorsIdentity, `is`<List<String>>(card.colorsIdentity))

        assertThat(cardFromDb.rulings.size, `is`(card.rulings.size))
        for (i in 0 until cardFromDb.rulings.size) {
            assertThat(cardFromDb.rulings[i], `is`(card.rulings[i]))
        }
        assertThat(cardFromDb.legalities.size, `is`(card.legalities.size))
        for (i in 0 until cardFromDb.legalities.size) {
            assertThat(cardFromDb.legalities[i].format, `is`(card.legalities[i].format))
            assertThat(cardFromDb.legalities[i].legality, `is`(card.legalities[i].legality))
        }
        assertThat(cardFromDb.uuid, `is`(card.uuid))
        cursor.close()
    }

    @Test
    fun test_cards_can_be_saved_and_retrieved_from_database() {
        val cardsToAdd = mtgCardDataSource.getRandomCard(5)
        for (card in cardsToAdd) {
            underTest.saveCard(card)
        }

        val cards = underTest.cards
        assertNotNull(cards)
        assertThat(cards.size, `is`(cardsToAdd.size))
        assertThat(cards, `is`(cardsToAdd))
    }

    @Test
    fun parsesCardFromCursor() {
        setupCursorCard()
        val (id, uuid, name, type, types, subTypes, colors, cmc, rarity, power, toughness, manaCost, text, isMultiColor, isLand, isArtifact, multiVerseId, set, _, _, layout, number, rulings, names, superTypes, artist, flavor, loyalty, printings, originalText, colorsIdentity, legalities) = underTest.fromCursor(cursor)

        assertThat(id, `is`(2))
        assertThat(multiVerseId, `is`(1001))
        assertThat(uuid, `is`("9b1c7f07-8d39-425b-8ae9-b3ab317cc0fe"))
        assertThat(name, `is`("name"))
        assertThat(type, `is`("type"))
        assertThat<List<String>>(types, `is`(listOf("Artifact", "Creature")))
        assertThat<List<String>>(subTypes, `is`(listOf("Creature", "Artifact")))

        assertThat<List<Int>>(colors, `is`(listOf(1, 2)))
        assertThat(cmc, `is`(1))
        assertThat(rarity, `is`(Rarity.RARE))
        assertThat(power, `is`("2"))
        assertThat(toughness, `is`("3"))

        assertThat(manaCost, `is`("3{U}{B}"))
        assertThat(text, `is`("text"))

        assertFalse(isMultiColor)
        assertTrue(isLand)
        assertFalse(isArtifact)

        assertThat(set?.id, `is`(10))
        assertThat(set?.name, `is`("Commander 2016"))
        assertThat(set?.code, `is`("C16"))

        assertNotNull(rulings)
        assertThat(rulings.size, `is`(1))
        assertThat(rulings[0], `is`("If a spell or ability has you draw multiple cards, Hoofprints of the Stag's ability triggers that many times."))

        assertThat(layout, `is`("layout"))
        assertThat<String>(number, `is`("29"))

        assertNotNull(names)
        assertThat(names.size, `is`(2))
        assertThat(names[0], `is`("Order"))
        assertThat(names[1], `is`("Chaos"))

        assertNotNull(superTypes)
        assertThat(superTypes.size, `is`(2))
        assertThat(superTypes[0], `is`("Creature"))
        assertThat(superTypes[1], `is`("Artifact"))

        assertThat<String>(flavor, `is`("flavor"))
        assertThat(artist, `is`("artist"))
        assertThat(loyalty, `is`(4))

        assertNotNull(printings)
        assertThat(printings.size, `is`(2))
        assertThat(printings[0], `is`("C16"))
        assertThat(printings[1], `is`("C17"))

        assertThat(originalText, `is`("original text"))

        assertNotNull(colorsIdentity)
        assertThat(colorsIdentity?.size, `is`(2))
        assertThat(colorsIdentity!![0], `is`("U"))
        assertThat(colorsIdentity[1], `is`("W"))

        assertNotNull(legalities)
        assertThat(legalities.size, `is`(2))
        assertThat(legalities[0].format, `is`("Legacy"))
        assertThat(legalities[0].legality, `is`("Banned"))
        assertThat(legalities[1].format, `is`("Vintage"))
        assertThat(legalities[1].legality, `is`("Restricted"))
    }

    @Test
    fun createsContentValuesProperly() {
        val card = mtgCardDataSource.getRandomCard(1)[0]
        val contentValues = underTest.createContentValue(card)

        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.UUID.noun), `is`(card.uuid))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.NAME.noun), `is`(card.name))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.TYPE.noun), `is`(card.type))

        assertThat(contentValues.getAsInteger(CardDataSource.COLUMNS.SET_ID.noun), `is`(card.set?.id))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.SET_NAME.noun), `is`(card.set?.name))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.SET_CODE.noun), `is`(card.set?.code))

        if (card.colors.size > 0) {
            assertThat(contentValues.getAsString(CardDataSource.COLUMNS.COLORS.noun), `is`(joinListOfColors(card.colors, ",")))
        }

        if (card.types.size > 0) {
            assertThat(contentValues.getAsString(CardDataSource.COLUMNS.TYPES.noun), `is`(joinListOfStrings(card.types, ",")))
        }

        if (card.subTypes.size > 0) {
            assertThat(contentValues.getAsString(CardDataSource.COLUMNS.SUB_TYPES.noun), `is`(joinListOfStrings(card.subTypes, ",")))
        }

        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.MANA_COST.noun), `is`(card.manaCost))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.RARITY.noun), `is`(card.rarity.value))
        assertThat(contentValues.getAsInteger(CardDataSource.COLUMNS.MULTIVERSE_ID.noun), `is`(card.multiVerseId))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.POWER.noun), `is`(card.power))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.TOUGHNESS.noun), `is`(card.toughness))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.TEXT.noun), `is`(card.text))
        assertThat(contentValues.getAsInteger(CardDataSource.COLUMNS.CMC.noun), `is`(card.cmc))

        assertThat(contentValues.getAsBoolean(CardDataSource.COLUMNS.MULTICOLOR.noun), `is`(card.isMultiColor))
        assertThat(contentValues.getAsBoolean(CardDataSource.COLUMNS.LAND.noun), `is`(card.isLand))
        assertThat(contentValues.getAsBoolean(CardDataSource.COLUMNS.ARTIFACT.noun), `is`(card.isArtifact))

        if (card.rulings.size > 0) {
            val rules = JSONArray()
            for (rule in card.rulings) {
                val rulJ = JSONObject()
                try {
                    rulJ.put("text", rule)
                    rules.put(rulJ)
                } catch (e: JSONException) {
                    LOG.e(e)
                }
            }
            assertThat(contentValues.getAsString(CardDataSource.COLUMNS.RULINGS.noun), `is`(rules.toString()))
        }

        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.LAYOUT.noun), `is`(card.layout))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.NUMBER.noun), `is`<String>(card.number))

        val gson = Gson()
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.NAMES.noun), `is`(gson.toJson(card.names)))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.SUPER_TYPES.noun), `is`(gson.toJson(card.superTypes)))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.FLAVOR.noun), `is`<String>(card.flavor))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.ARTIST.noun), `is`(card.artist))
        assertThat(contentValues.getAsInteger(CardDataSource.COLUMNS.LOYALTY.noun), `is`(card.loyalty))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.PRINTINGS.noun), `is`(gson.toJson(card.printings)))

        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.ORIGINAL_TEXT.noun), `is`(card.originalText))

        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.COLORS_IDENTITY.noun), `is`(gson.toJson(card.colorsIdentity)))

        if (card.legalities.size > 0) {
            val legalities = JSONArray()
            for (legality in card.legalities) {
                val legalityJ = JSONObject()
                try {
                    legalityJ.put("format", legality.format)
                    legalityJ.put("legality", legality.legality)
                    legalities.put(legalityJ)
                } catch (e: JSONException) {
                    LOG.e(e)
                }
            }
            assertThat(contentValues.getAsString(CardDataSource.COLUMNS.LEGALITIES.noun), `is`(legalities.toString()))
        }
    }

    private fun setupCursorCard() {
        whenever(cursor.getColumnIndex("_id")).thenReturn(1)
        whenever(cursor.getInt(1)).thenReturn(2)

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.MULTIVERSE_ID.noun)).thenReturn(2)
        whenever(cursor.getInt(2)).thenReturn(1001)

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.NAME.noun)).thenReturn(3)
        whenever(cursor.getString(3)).thenReturn("name")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.TYPE.noun)).thenReturn(4)
        whenever(cursor.getString(4)).thenReturn("type")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.TYPES.noun)).thenReturn(5)
        whenever(cursor.getString(5)).thenReturn("Artifact,Creature")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.SUB_TYPES.noun)).thenReturn(6)
        whenever(cursor.getString(6)).thenReturn("Creature,Artifact")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.COLORS.noun)).thenReturn(7)
        whenever(cursor.getString(7)).thenReturn("Blue,Black")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.CMC.noun)).thenReturn(8)
        whenever(cursor.getInt(8)).thenReturn(1)

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.RARITY.noun)).thenReturn(9)
        whenever(cursor.getString(9)).thenReturn("rare")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.POWER.noun)).thenReturn(10)
        whenever(cursor.getString(10)).thenReturn("2")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.TOUGHNESS.noun)).thenReturn(11)
        whenever(cursor.getString(11)).thenReturn("3")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.MANA_COST.noun)).thenReturn(12)
        whenever(cursor.getString(12)).thenReturn("3{U}{B}")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.TEXT.noun)).thenReturn(13)
        whenever(cursor.getString(13)).thenReturn("text")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.MULTICOLOR.noun)).thenReturn(14)
        whenever(cursor.getInt(14)).thenReturn(0)

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.LAND.noun)).thenReturn(15)
        whenever(cursor.getInt(15)).thenReturn(1)

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.ARTIFACT.noun)).thenReturn(16)
        whenever(cursor.getInt(16)).thenReturn(0)

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.SET_ID.noun)).thenReturn(17)
        whenever(cursor.getInt(17)).thenReturn(10)

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.SET_NAME.noun)).thenReturn(18)
        whenever(cursor.getString(18)).thenReturn("Commander 2016")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.SET_CODE.noun)).thenReturn(19)
        whenever(cursor.getString(19)).thenReturn("C16")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.RULINGS.noun)).thenReturn(20)
        whenever(cursor.getString(20)).thenReturn("[{\"date\":\"2007-10-01\",\"text\":\"If a spell or ability has you draw multiple cards, Hoofprints of the Stag's ability triggers that many times.\"}]")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.LAYOUT.noun)).thenReturn(21)
        whenever(cursor.getString(21)).thenReturn("layout")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.NUMBER.noun)).thenReturn(22)
        whenever(cursor.getString(22)).thenReturn("29")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.NAMES.noun)).thenReturn(23)
        whenever(cursor.getString(23)).thenReturn("[\"Order\",\"Chaos\"]")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.SUPER_TYPES.noun)).thenReturn(24)
        whenever(cursor.getString(24)).thenReturn("[\"Creature\",\"Artifact\"]")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.FLAVOR.noun)).thenReturn(25)
        whenever(cursor.getString(25)).thenReturn("flavor")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.ARTIST.noun)).thenReturn(26)
        whenever(cursor.getString(26)).thenReturn("artist")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.LOYALTY.noun)).thenReturn(27)
        whenever(cursor.getInt(27)).thenReturn(4)

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.PRINTINGS.noun)).thenReturn(28)
        whenever(cursor.getString(28)).thenReturn("[\"C16\",\"C17\"]")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.LEGALITIES.noun)).thenReturn(29)
        whenever(cursor.getString(29)).thenReturn("[{\"format\":\"Legacy\", \"legality\" : \"Banned\" }, { \"format\" : \"Vintage\", \"legality\" : \"Restricted\" } ]")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.ORIGINAL_TEXT.noun)).thenReturn(30)
        whenever(cursor.getString(30)).thenReturn("original text")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.COLORS_IDENTITY.noun)).thenReturn(31)
        whenever(cursor.getString(31)).thenReturn("[\"U\",\"W\"]")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.UUID.noun)).thenReturn(32)
        whenever(cursor.getString(32)).thenReturn("9b1c7f07-8d39-425b-8ae9-b3ab317cc0fe")
    }

    private fun joinListOfStrings(list: List<String>, separator: String): String {
        val joined = StringBuilder("")
        if (list.isEmpty()) {
            return joined.toString()
        }
        for (i in list.indices) {
            val value = list[i]
            joined.append(value)
            if (i < list.size - 1) {
                joined.append(separator)
            }
        }
        return joined.toString()
    }

    private fun joinListOfColors(list: List<Int>, separator: String): String {
        val joined = StringBuilder("")
        if (list.isEmpty()) {
            return joined.toString()
        }
        for (i in list.indices) {
            val value = list[i]
            val color = value.toColor()
            joined.append(color)
            if (i < list.size - 1) {
                joined.append(separator)
            }
        }
        return joined.toString()
    }
}
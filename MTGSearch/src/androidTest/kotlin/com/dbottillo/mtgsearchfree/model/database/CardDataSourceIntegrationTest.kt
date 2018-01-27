package com.dbottillo.mtgsearchfree.model.database

import android.database.Cursor
import android.support.test.runner.AndroidJUnit4
import com.dbottillo.mtgsearchfree.model.CardProperties
import com.dbottillo.mtgsearchfree.util.BaseContextTest
import com.dbottillo.mtgsearchfree.util.LOG
import com.google.gson.Gson
import org.hamcrest.Matchers.`is`
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import java.util.*

@RunWith(AndroidJUnit4::class)
class CardDataSourceIntegrationTest : BaseContextTest() {

    @Rule @JvmField
    var rule = MockitoJUnit.rule()

    @Mock
    lateinit var cursor: Cursor

    lateinit var mtgCardDataSource: MTGCardDataSource
    lateinit var underTest: CardDataSource

    @Before
    fun setup() {
        underTest = CardDataSource(cardsInfoDbHelper.writableDatabase, Gson())
        mtgCardDataSource = MTGCardDataSource(mtgDatabaseHelper.writableDatabase, underTest)
    }

    @Test
    fun test_generate_table_is_correct() {
        val query = CardDataSource.generateCreateTable()
        assertNotNull(query)
        assertThat(query, `is`("CREATE TABLE IF NOT EXISTS MTGCard (_id INTEGER PRIMARY KEY, name TEXT,type TEXT,types TEXT,subtypes TEXT,colors TEXT,cmc INTEGER,rarity TEXT,power TEXT,toughness TEXT,manaCost TEXT,text TEXT,multicolor INTEGER,land INTEGER,artifact INTEGER,multiVerseId INTEGER,setId INTEGER,setName TEXT,rulings TEXT,layout TEXT,setCode TEXT,number TEXT,names TEXT,supertypes TEXT,flavor TEXT,artist TEXT,loyalty INTEGER,printings TEXT,legalities TEXT,originalText TEXT,mciNumber TEXT,colorIdentity TEXT)"))
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
        assertThat<String>(cardFromDb.mciNumber, `is`<String>(card.mciNumber))

        assertThat(cardFromDb.rulings.size, `is`(card.rulings.size))
        for (i in 0 until cardFromDb.rulings.size) {
            assertThat(cardFromDb.rulings[i], `is`(card.rulings[i]))
        }
        assertThat(cardFromDb.legalities.size, `is`(card.legalities.size))
        for (i in 0 until cardFromDb.legalities.size) {
            assertThat(cardFromDb.legalities[i].format, `is`(card.legalities[i].format))
            assertThat(cardFromDb.legalities[i].legality, `is`(card.legalities[i].legality))
        }

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
        val (id, name, type, types, subTypes, colors, cmc, rarity, power, toughness, manaCost, text, isMultiColor, isLand, isArtifact, multiVerseId, set, _, _, layout, number, rulings, names, superTypes, artist, flavor, loyalty, printings, originalText, mciNumber, colorsIdentity, legalities) = underTest.fromCursor(cursor)

        assertThat(id, `is`(2))
        assertThat(multiVerseId, `is`(1001))
        assertThat(name, `is`("name"))
        assertThat(type, `is`("type"))
        assertThat<List<String>>(types, `is`(Arrays.asList("Artifact", "Creature")))
        assertThat<List<String>>(subTypes, `is`(Arrays.asList("Creature", "Artifact")))

        assertThat<List<Int>>(colors, `is`(Arrays.asList(1, 2)))
        assertThat(cmc, `is`(1))
        assertThat(rarity, `is`("Rare"))
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

        assertThat<String>(mciNumber, `is`("233"))
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

        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.NAME.getName()), `is`(card.name))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.TYPE.getName()), `is`(card.type))

        assertThat(contentValues.getAsInteger(CardDataSource.COLUMNS.SET_ID.getName()), `is`(card.set?.id))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.SET_NAME.getName()), `is`(card.set?.name))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.SET_CODE.getName()), `is`(card.set?.code))

        if (card.colors.size > 0) {
            assertThat(contentValues.getAsString(CardDataSource.COLUMNS.COLORS.getName()), `is`(joinListOfColors(card.colors, ",")))
        }

        if (card.types.size > 0) {
            assertThat(contentValues.getAsString(CardDataSource.COLUMNS.TYPES.getName()), `is`(joinListOfStrings(card.types, ",")))
        }

        if (card.subTypes.size > 0) {
            assertThat(contentValues.getAsString(CardDataSource.COLUMNS.SUB_TYPES.getName()), `is`(joinListOfStrings(card.subTypes, ",")))
        }

        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.MANA_COST.getName()), `is`(card.manaCost))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.RARITY.getName()), `is`(card.rarity))
        assertThat(contentValues.getAsInteger(CardDataSource.COLUMNS.MULTIVERSE_ID.getName()), `is`(card.multiVerseId))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.POWER.getName()), `is`(card.power))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.TOUGHNESS.getName()), `is`(card.toughness))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.TEXT.getName()), `is`(card.text))
        assertThat(contentValues.getAsInteger(CardDataSource.COLUMNS.CMC.getName()), `is`(card.cmc))

        assertThat(contentValues.getAsBoolean(CardDataSource.COLUMNS.MULTICOLOR.getName()), `is`(card.isMultiColor))
        assertThat(contentValues.getAsBoolean(CardDataSource.COLUMNS.LAND.getName()), `is`(card.isLand))
        assertThat(contentValues.getAsBoolean(CardDataSource.COLUMNS.ARTIFACT.getName()), `is`(card.isArtifact))

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
            assertThat(contentValues.getAsString(CardDataSource.COLUMNS.RULINGS.getName()), `is`(rules.toString()))
        }

        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.LAYOUT.getName()), `is`(card.layout))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.NUMBER.getName()), `is`<String>(card.number))

        val gson = Gson()
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.NAMES.getName()), `is`(gson.toJson(card.names)))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.SUPER_TYPES.getName()), `is`(gson.toJson(card.superTypes)))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.FLAVOR.getName()), `is`<String>(card.flavor))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.ARTIST.getName()), `is`(card.artist))
        assertThat(contentValues.getAsInteger(CardDataSource.COLUMNS.LOYALTY.getName()), `is`(card.loyalty))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.PRINTINGS.getName()), `is`(gson.toJson(card.printings)))

        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.ORIGINAL_TEXT.getName()), `is`(card.originalText))

        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.MCI_NUMBER.getName()), `is`<String>(card.mciNumber))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.COLORS_IDENTITY.getName()), `is`(gson.toJson(card.colorsIdentity)))

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
            assertThat(contentValues.getAsString(CardDataSource.COLUMNS.LEGALITIES.getName()), `is`(legalities.toString()))
        }
    }

    private fun setupCursorCard() {
        `when`(cursor.getColumnIndex("_id")).thenReturn(1)
        `when`(cursor.getInt(1)).thenReturn(2)

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.MULTIVERSE_ID.getName())).thenReturn(2)
        `when`(cursor.getInt(2)).thenReturn(1001)

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.NAME.getName())).thenReturn(3)
        `when`(cursor.getString(3)).thenReturn("name")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.TYPE.getName())).thenReturn(4)
        `when`(cursor.getString(4)).thenReturn("type")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.TYPES.getName())).thenReturn(5)
        `when`(cursor.getString(5)).thenReturn("Artifact,Creature")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.SUB_TYPES.getName())).thenReturn(6)
        `when`(cursor.getString(6)).thenReturn("Creature,Artifact")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.COLORS.getName())).thenReturn(7)
        `when`(cursor.getString(7)).thenReturn("Blue,Black")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.CMC.getName())).thenReturn(8)
        `when`(cursor.getInt(8)).thenReturn(1)

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.RARITY.getName())).thenReturn(9)
        `when`(cursor.getString(9)).thenReturn("Rare")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.POWER.getName())).thenReturn(10)
        `when`(cursor.getString(10)).thenReturn("2")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.TOUGHNESS.getName())).thenReturn(11)
        `when`(cursor.getString(11)).thenReturn("3")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.MANA_COST.getName())).thenReturn(12)
        `when`(cursor.getString(12)).thenReturn("3{U}{B}")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.TEXT.getName())).thenReturn(13)
        `when`(cursor.getString(13)).thenReturn("text")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.MULTICOLOR.getName())).thenReturn(14)
        `when`(cursor.getInt(14)).thenReturn(0)

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.LAND.getName())).thenReturn(15)
        `when`(cursor.getInt(15)).thenReturn(1)

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.ARTIFACT.getName())).thenReturn(16)
        `when`(cursor.getInt(16)).thenReturn(0)

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.SET_ID.getName())).thenReturn(17)
        `when`(cursor.getInt(17)).thenReturn(10)

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.SET_NAME.getName())).thenReturn(18)
        `when`(cursor.getString(18)).thenReturn("Commander 2016")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.SET_CODE.getName())).thenReturn(19)
        `when`(cursor.getString(19)).thenReturn("C16")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.RULINGS.getName())).thenReturn(20)
        `when`(cursor.getString(20)).thenReturn("[{\"date\":\"2007-10-01\",\"text\":\"If a spell or ability has you draw multiple cards, Hoofprints of the Stag's ability triggers that many times.\"}]")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.LAYOUT.getName())).thenReturn(21)
        `when`(cursor.getString(21)).thenReturn("layout")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.NUMBER.getName())).thenReturn(22)
        `when`(cursor.getString(22)).thenReturn("29")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.NAMES.getName())).thenReturn(23)
        `when`(cursor.getString(23)).thenReturn("[\"Order\",\"Chaos\"]")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.SUPER_TYPES.getName())).thenReturn(24)
        `when`(cursor.getString(24)).thenReturn("[\"Creature\",\"Artifact\"]")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.FLAVOR.getName())).thenReturn(25)
        `when`(cursor.getString(25)).thenReturn("flavor")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.ARTIST.getName())).thenReturn(26)
        `when`(cursor.getString(26)).thenReturn("artist")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.LOYALTY.getName())).thenReturn(27)
        `when`(cursor.getInt(27)).thenReturn(4)

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.PRINTINGS.getName())).thenReturn(28)
        `when`(cursor.getString(28)).thenReturn("[\"C16\",\"C17\"]")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.LEGALITIES.getName())).thenReturn(29)
        `when`(cursor.getString(29)).thenReturn("[{\"format\":\"Legacy\", \"legality\" : \"Banned\" }, { \"format\" : \"Vintage\", \"legality\" : \"Restricted\" } ]")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.ORIGINAL_TEXT.getName())).thenReturn(30)
        `when`(cursor.getString(30)).thenReturn("original text")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.MCI_NUMBER.getName())).thenReturn(31)
        `when`(cursor.getString(31)).thenReturn("233")

        `when`(cursor.getColumnIndex(CardDataSource.COLUMNS.COLORS_IDENTITY.getName())).thenReturn(32)
        `when`(cursor.getString(32)).thenReturn("[\"U\",\"W\"]")

    }

    private fun joinListOfStrings(list: List<String>, separator: String): String {
        val joined = StringBuilder("")
        if (list.size == 0) {
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
        if (list.size == 0) {
            return joined.toString()
        }
        for (i in list.indices) {
            val value = list[i]
            val color = CardProperties.COLOR.getStringFromNumber(value)
            joined.append(color)
            if (i < list.size - 1) {
                joined.append(separator)
            }
        }
        return joined.toString()
    }

}
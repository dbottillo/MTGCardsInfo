package com.dbottillo.mtgsearchfree.database

import android.database.Cursor
import com.dbottillo.mtgsearchfree.model.Color
import com.dbottillo.mtgsearchfree.model.Rarity
import com.dbottillo.mtgsearchfree.model.Side
import com.dbottillo.mtgsearchfree.util.LOG
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.whenever
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.junit.After
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

    private lateinit var mtgCardDataSource: MTGCardDataSource
    private lateinit var cardsInfoDbHelper: CardsInfoDbHelper
    private lateinit var mtgDatabaseHelper: MTGDatabaseHelper
    private lateinit var underTest: CardDataSource

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
        assertThat(query).isNotNull()
        assertThat(
            query).isEqualTo("CREATE TABLE IF NOT EXISTS MTGCard (_id INTEGER PRIMARY KEY, name TEXT,type TEXT,types TEXT,subtypes TEXT,colors TEXT,cmc INTEGER,rarity TEXT,power TEXT,toughness TEXT,manaCost TEXT,text TEXT,multicolor INTEGER,land INTEGER,artifact INTEGER,multiVerseId INTEGER,setId INTEGER,setName TEXT,rulings TEXT,layout TEXT,setCode TEXT,number TEXT,names TEXT,supertypes TEXT,flavor TEXT,artist TEXT,loyalty INTEGER,printings TEXT,legalities TEXT,originalText TEXT,colorIdentity TEXT,uuid TEXT,scryfallId TEXT,tcgplayerProductId INTEGER,tcgplayerPurchaseUrl TEXT,faceConvertedManaCost INTEGER,isArena INTEGER,isMtgo INTEGER,cardSide TEXT,otherFaceIds TEXT)")
        assertThat(
            CardDataSource.generateCreateTable(1)).isEqualTo("CREATE TABLE IF NOT EXISTS MTGCard (_id INTEGER PRIMARY KEY, name TEXT,type TEXT,types TEXT,subtypes TEXT,colors TEXT,cmc INTEGER,rarity TEXT,power TEXT,toughness TEXT,manaCost TEXT,text TEXT,multicolor INTEGER,land INTEGER,artifact INTEGER,multiVerseId INTEGER,setId INTEGER,setName TEXT)")
        assertThat(
            CardDataSource.generateCreateTable(2)).isEqualTo("CREATE TABLE IF NOT EXISTS MTGCard (_id INTEGER PRIMARY KEY, name TEXT,type TEXT,types TEXT,subtypes TEXT,colors TEXT,cmc INTEGER,rarity TEXT,power TEXT,toughness TEXT,manaCost TEXT,text TEXT,multicolor INTEGER,land INTEGER,artifact INTEGER,multiVerseId INTEGER,setId INTEGER,setName TEXT,rulings TEXT,layout TEXT)")
        assertThat(
            CardDataSource.generateCreateTable(3)).isEqualTo("CREATE TABLE IF NOT EXISTS MTGCard (_id INTEGER PRIMARY KEY, name TEXT,type TEXT,types TEXT,subtypes TEXT,colors TEXT,cmc INTEGER,rarity TEXT,power TEXT,toughness TEXT,manaCost TEXT,text TEXT,multicolor INTEGER,land INTEGER,artifact INTEGER,multiVerseId INTEGER,setId INTEGER,setName TEXT,rulings TEXT,layout TEXT,setCode TEXT,number TEXT)")
        assertThat(
            CardDataSource.generateCreateTable(10)).isEqualTo("CREATE TABLE IF NOT EXISTS MTGCard (_id INTEGER PRIMARY KEY, name TEXT,type TEXT,types TEXT,subtypes TEXT,colors TEXT,cmc INTEGER,rarity TEXT,power TEXT,toughness TEXT,manaCost TEXT,text TEXT,multicolor INTEGER,land INTEGER,artifact INTEGER,multiVerseId INTEGER,setId INTEGER,setName TEXT,rulings TEXT,layout TEXT,setCode TEXT,number TEXT,names TEXT,supertypes TEXT,flavor TEXT,artist TEXT,loyalty INTEGER,printings TEXT,legalities TEXT,originalText TEXT,colorIdentity TEXT,uuid TEXT,scryfallId TEXT,tcgplayerProductId INTEGER)")
        assertThat(
            CardDataSource.generateCreateTable(11)).isEqualTo("CREATE TABLE IF NOT EXISTS MTGCard (_id INTEGER PRIMARY KEY, name TEXT,type TEXT,types TEXT,subtypes TEXT,colors TEXT,cmc INTEGER,rarity TEXT,power TEXT,toughness TEXT,manaCost TEXT,text TEXT,multicolor INTEGER,land INTEGER,artifact INTEGER,multiVerseId INTEGER,setId INTEGER,setName TEXT,rulings TEXT,layout TEXT,setCode TEXT,number TEXT,names TEXT,supertypes TEXT,flavor TEXT,artist TEXT,loyalty INTEGER,printings TEXT,legalities TEXT,originalText TEXT,colorIdentity TEXT,uuid TEXT,scryfallId TEXT,tcgplayerProductId INTEGER,tcgplayerPurchaseUrl TEXT)")
        assertThat(
            CardDataSource.generateCreateTable(12)).isEqualTo("CREATE TABLE IF NOT EXISTS MTGCard (_id INTEGER PRIMARY KEY, name TEXT,type TEXT,types TEXT,subtypes TEXT,colors TEXT,cmc INTEGER,rarity TEXT,power TEXT,toughness TEXT,manaCost TEXT,text TEXT,multicolor INTEGER,land INTEGER,artifact INTEGER,multiVerseId INTEGER,setId INTEGER,setName TEXT,rulings TEXT,layout TEXT,setCode TEXT,number TEXT,names TEXT,supertypes TEXT,flavor TEXT,artist TEXT,loyalty INTEGER,printings TEXT,legalities TEXT,originalText TEXT,colorIdentity TEXT,uuid TEXT,scryfallId TEXT,tcgplayerProductId INTEGER,tcgplayerPurchaseUrl TEXT,faceConvertedManaCost INTEGER,isArena INTEGER,isMtgo INTEGER,cardSide TEXT)")
    }

    @Test
    fun test_card_can_be_saved_in_database() {
        val card = mtgCardDataSource.getRandomCard(1)[0]
        val id = underTest.saveCard(card)
        val cursor = cardsInfoDbHelper.readableDatabase.rawQuery(
            "select * from " + CardDataSource.TABLE + " where rowid =?",
            arrayOf(id.toString() + "")
        )
        assertThat(cursor).isNotNull()
        assertThat(cursor.count).isEqualTo(1)
        cursor.moveToFirst()
        val cardFromDb = underTest.fromCursor(cursor, true)
        assertThat(cardFromDb).isNotNull()
        assertThat(cardFromDb.name).isEqualTo(card.name)
        assertThat(cardFromDb.type).isEqualTo(card.type)
        assertThat(cardFromDb.subTypes.size).isEqualTo(card.subTypes.size)
        for (i in 0 until cardFromDb.subTypes.size) {
            assertThat(cardFromDb.subTypes[i]).isEqualTo(card.subTypes[i])
        }
        assertThat(cardFromDb.colorsDisplay.size).isEqualTo(card.colorsDisplay.size)
        for (i in cardFromDb.colorsDisplay.indices) {
            assertThat(cardFromDb.colorsDisplay[i]).isEqualTo(card.colorsDisplay[i])
        }
        assertThat(cardFromDb.cmc).isEqualTo(card.cmc)
        assertThat(cardFromDb.rarity).isEqualTo(card.rarity)
        assertThat(cardFromDb.power).isEqualTo(card.power)
        assertThat(cardFromDb.toughness).isEqualTo(card.toughness)
        assertThat(cardFromDb.manaCost).isEqualTo(card.manaCost)
        assertThat(cardFromDb.text).isEqualTo(card.text)
        assertThat(cardFromDb.isMultiColor).isEqualTo(card.isMultiColor)
        assertThat(cardFromDb.isLand).isEqualTo(card.isLand)
        assertThat(cardFromDb.isArtifact).isEqualTo(card.isArtifact)
        assertThat(cardFromDb.isEldrazi).isEqualTo(card.isEldrazi)
        assertThat(cardFromDb.set).isEqualTo(card.set)
        assertThat(cardFromDb.layout).isEqualTo(card.layout)
        assertThat<String>(cardFromDb.number).isEqualTo(card.number)
        assertThat(cardFromDb.rulings.size).isEqualTo(card.rulings.size)
        for (i in 0 until cardFromDb.rulings.size) {
            assertThat(cardFromDb.rulings[i]).isEqualTo(card.rulings[i])
        }

        assertThat(cardFromDb.names.size).isEqualTo(card.names.size)
        for (i in cardFromDb.names.indices) {
            assertThat(cardFromDb.names[i]).isEqualTo(card.names[i])
        }

        assertThat(cardFromDb.superTypes.size).isEqualTo(card.superTypes.size)
        for (i in cardFromDb.superTypes.indices) {
            assertThat(cardFromDb.superTypes[i]).isEqualTo(card.superTypes[i])
        }
        assertThat(cardFromDb.loyalty).isEqualTo(card.loyalty)
        assertThat(cardFromDb.artist).isEqualTo(card.artist)
        assertThat<String>(cardFromDb.flavor).isEqualTo(card.flavor)

        assertThat(cardFromDb.printings).isEqualTo(card.printings)
        for (i in cardFromDb.printings.indices) {
            assertThat(cardFromDb.printings[i]).isEqualTo(card.printings[i])
        }
        assertThat(cardFromDb.originalText).isEqualTo(card.originalText)

        assertThat(cardFromDb.colorsIdentity).isEqualTo(card.colorsIdentity)

        assertThat(cardFromDb.rulings.size).isEqualTo(card.rulings.size)
        for (i in 0 until cardFromDb.rulings.size) {
            assertThat(cardFromDb.rulings[i]).isEqualTo(card.rulings[i])
        }
        assertThat(cardFromDb.legalities.size).isEqualTo(card.legalities.size)
        for (i in 0 until cardFromDb.legalities.size) {
            assertThat(cardFromDb.legalities[i].format).isEqualTo(card.legalities[i].format)
            assertThat(cardFromDb.legalities[i].legality).isEqualTo(card.legalities[i].legality)
        }
        assertThat(cardFromDb.uuid).isEqualTo(card.uuid)
        assertThat(cardFromDb.scryfallId).isEqualTo(card.scryfallId)
        assertThat(cardFromDb.tcgplayerProductId).isEqualTo(card.tcgplayerProductId)
        assertThat(cardFromDb.tcgplayerPurchaseUrl).isEqualTo(card.tcgplayerPurchaseUrl)

        assertThat(cardFromDb.faceConvertedManaCost).isEqualTo(card.faceConvertedManaCost)
        assertThat(cardFromDb.isArena).isEqualTo(card.isArena)
        assertThat(cardFromDb.isMtgo).isEqualTo(card.isMtgo)
        assertThat(cardFromDb.side).isEqualTo(card.side)
        assertThat(cardFromDb.otherFaceIds).isEqualTo(card.otherFaceIds)

        cursor.close()
    }

    @Test
    fun test_cards_can_be_saved_and_retrieved_from_database() {
        val cardsToAdd = mtgCardDataSource.getRandomCard(5)
        for (card in cardsToAdd) {
            underTest.saveCard(card)
        }

        val cards = underTest.cards
        assertThat(cards).isNotNull()
        assertThat(cards.size).isEqualTo(cardsToAdd.size)
        assertThat(cards).isEqualTo(cardsToAdd)
    }

    @Test
    fun parsesCardFromCursor() {
        setupCursorCard()
        val card = underTest.fromCursor(cursor)

        assertThat(card.id).isEqualTo(2)
        assertThat(card.multiVerseId).isEqualTo(1001)
        assertThat(card.tcgplayerProductId).isEqualTo(129859)
        assertThat(card.uuid).isEqualTo("9b1c7f07-8d39-425b-8ae9-b3ab317cc0fe")
        assertThat(card.scryfallId).isEqualTo("05e2a5e6-3aaa-4096-bdd0-fcc1afe5a36c")
        assertThat(card.name).isEqualTo("name")
        assertThat(card.type).isEqualTo("type")
        assertThat(card.types).isEqualTo(listOf("Artifact", "Creature"))
        assertThat(card.subTypes).isEqualTo(listOf("Creature", "Artifact"))

        assertThat(card.colorsDisplay).isEqualTo(listOf("U", "W"))
        assertThat(card.cmc).isEqualTo(1)
        assertThat(card.rarity).isEqualTo(Rarity.RARE)
        assertThat(card.power).isEqualTo("2")
        assertThat(card.toughness).isEqualTo("3")

        assertThat(card.manaCost).isEqualTo("3{U}{B}")
        assertThat(card.text).isEqualTo("text")

        assertThat(card.isMultiColor).isFalse()
        assertThat(card.isLand).isTrue()
        assertThat(card.isArtifact).isFalse()

        assertThat(card.set?.id).isEqualTo(10)
        assertThat(card.set?.name).isEqualTo("Commander 2016")
        assertThat(card.set?.code).isEqualTo("C16")

        assertThat(card.rulings).isNotNull()
        assertThat(card.rulings.size).isEqualTo(1)
        assertThat(card.rulings[0]).isEqualTo("If a spell or ability has you draw multiple cards, Hoofprints of the Stag's ability triggers that many times.")

        assertThat(card.layout).isEqualTo("layout")
        assertThat(card.number).isEqualTo("29")

        assertThat(card.names).isNotNull()
        assertThat(card.names.size).isEqualTo(2)
        assertThat(card.names[0]).isEqualTo("Order")
        assertThat(card.names[1]).isEqualTo("Chaos")

        assertThat(card.superTypes).isNotNull()
        assertThat(card.superTypes.size).isEqualTo(2)
        assertThat(card.superTypes[0]).isEqualTo("Creature")
        assertThat(card.superTypes[1]).isEqualTo("Artifact")

        assertThat(card.flavor).isEqualTo("flavor")
        assertThat(card.artist).isEqualTo("artist")
        assertThat(card.loyalty).isEqualTo(4)

        assertThat(card.printings).isNotNull()
        assertThat(card.printings.size).isEqualTo(2)
        assertThat(card.printings[0]).isEqualTo("C16")
        assertThat(card.printings[1]).isEqualTo("C17")

        assertThat(card.originalText).isEqualTo("original text")

        assertThat(card.colorsIdentity).isNotNull()
        assertThat(card.colorsIdentity.size).isEqualTo(2)
        assertThat(card.colorsIdentity[0]).isEqualTo(Color.BLUE)
        assertThat(card.colorsIdentity[1]).isEqualTo(Color.WHITE)

        assertThat(card.legalities).isNotNull()
        assertThat(card.legalities.size).isEqualTo(2)
        assertThat(card.legalities[0].format).isEqualTo("Legacy")
        assertThat(card.legalities[0].legality).isEqualTo("Banned")
        assertThat(card.legalities[1].format).isEqualTo("Vintage")
        assertThat(card.legalities[1].legality).isEqualTo("Restricted")

        assertThat(card.tcgplayerPurchaseUrl).isEqualTo("tcg_player_url")

        assertThat(card.faceConvertedManaCost).isEqualTo(4)
        assertThat(card.isArena == true).isTrue()
        assertThat(card.isMtgo == true).isTrue()
        assertThat(card.side).isEqualTo(Side.B)

        assertThat(card.otherFaceIds).isEqualTo(listOf("UUID-1", "UUID-2"))
    }

    @Test
    fun createsContentValuesProperly() {
        val card = mtgCardDataSource.getRandomCard(1)[0]
        val contentValues = underTest.createContentValue(card)

        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.UUID.noun)).isEqualTo(card.uuid)
        assertThat(
            contentValues.getAsString(CardDataSource.COLUMNS.SCRYFALLID.noun)).isEqualTo(card.scryfallId)
        assertThat(
            contentValues.getAsInteger(CardDataSource.COLUMNS.TCG_PLAYER_PRODUCT_ID.noun)).isEqualTo(card.tcgplayerProductId)
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.NAME.noun)).isEqualTo(card.name)
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.TYPE.noun)).isEqualTo(card.type)

        assertThat(
            contentValues.getAsInteger(CardDataSource.COLUMNS.SET_ID.noun)).isEqualTo(card.set?.id)
        assertThat(
            contentValues.getAsString(CardDataSource.COLUMNS.SET_NAME.noun)).isEqualTo(card.set?.name)
        assertThat(
            contentValues.getAsString(CardDataSource.COLUMNS.SET_CODE.noun)).isEqualTo(card.set?.code)

        if (card.colorsDisplay.isNotEmpty()) {
            assertThat(
                contentValues.getAsString(CardDataSource.COLUMNS.COLORS.noun)).isEqualTo(card.colorsDisplay.joinToString(","))
        }

        if (card.types.size > 0) {
            assertThat(
                contentValues.getAsString(CardDataSource.COLUMNS.TYPES.noun)).isEqualTo(card.types.joinToString(","))
        }

        if (card.subTypes.size > 0) {
            assertThat(
                contentValues.getAsString(CardDataSource.COLUMNS.SUB_TYPES.noun)).isEqualTo(card.subTypes.joinToString(","))
        }

        assertThat(
            contentValues.getAsString(CardDataSource.COLUMNS.MANA_COST.noun)).isEqualTo(card.manaCost)
        assertThat(
            contentValues.getAsString(CardDataSource.COLUMNS.RARITY.noun)).isEqualTo(card.rarity.value)
        assertThat(
            contentValues.getAsInteger(CardDataSource.COLUMNS.MULTIVERSE_ID.noun)).isEqualTo(card.multiVerseId)
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.POWER.noun)).isEqualTo(card.power)
        assertThat(
            contentValues.getAsString(CardDataSource.COLUMNS.TOUGHNESS.noun)).isEqualTo(card.toughness)
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.TEXT.noun)).isEqualTo(card.text)
        assertThat(contentValues.getAsInteger(CardDataSource.COLUMNS.CMC.noun)).isEqualTo(card.cmc)

        assertThat(
            contentValues.getAsBoolean(CardDataSource.COLUMNS.MULTICOLOR.noun)).isEqualTo(card.isMultiColor)
        assertThat(contentValues.getAsBoolean(CardDataSource.COLUMNS.LAND.noun)).isEqualTo(card.isLand)
        assertThat(
            contentValues.getAsBoolean(CardDataSource.COLUMNS.ARTIFACT.noun)).isEqualTo(card.isArtifact)

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
            assertThat(
                contentValues.getAsString(CardDataSource.COLUMNS.RULINGS.noun)).isEqualTo(rules.toString())
        }

        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.LAYOUT.noun)).isEqualTo(card.layout)
        assertThat(
            contentValues.getAsString(CardDataSource.COLUMNS.NUMBER.noun)).isEqualTo(card.number)

        val gson = Gson()
        assertThat(
            contentValues.getAsString(CardDataSource.COLUMNS.NAMES.noun)).isEqualTo(gson.toJson(card.names))
        assertThat(
            contentValues.getAsString(CardDataSource.COLUMNS.SUPER_TYPES.noun)).isEqualTo(gson.toJson(card.superTypes))
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.FLAVOR.noun)).isEqualTo(card.flavor)
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.ARTIST.noun)).isEqualTo(card.artist)
        assertThat(
            contentValues.getAsInteger(CardDataSource.COLUMNS.LOYALTY.noun)).isEqualTo(card.loyalty)
        assertThat(
            contentValues.getAsString(CardDataSource.COLUMNS.PRINTINGS.noun)).isEqualTo(gson.toJson(card.printings))

        assertThat(
            contentValues.getAsString(CardDataSource.COLUMNS.ORIGINAL_TEXT.noun)).isEqualTo(card.originalText)

        assertThat(
            contentValues.getAsString(CardDataSource.COLUMNS.COLORS_IDENTITY.noun)).isEqualTo(gson.toJson(card.colorsIdentity.map { it.unmap() }))

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
            assertThat(
                contentValues.getAsString(CardDataSource.COLUMNS.LEGALITIES.noun)).isEqualTo(legalities.toString())
        }

        assertThat(
            contentValues.getAsString(CardDataSource.COLUMNS.TCG_PLAYER_PURCHASE_URL.noun)).isEqualTo(card.tcgplayerPurchaseUrl)

        assertThat(
            contentValues.getAsInteger(CardDataSource.COLUMNS.FACE_CMC.noun)).isEqualTo(card.faceConvertedManaCost)
        val expectedIsArena = when {
            card.isArena == true -> 1
            card.isArena == false -> 0
            else -> null
        }
        assertThat(
            contentValues.getAsInteger(CardDataSource.COLUMNS.IS_ARENA.noun)).isEqualTo(expectedIsArena)
        val expectedIsMtgo = when {
            card.isArena == true -> 1
            card.isArena == false -> 0
            else -> null
        }
        contentValues.getAsInteger(CardDataSource.COLUMNS.IS_MTGO.noun)?.let {
            assertThat(it).isEqualTo(expectedIsMtgo)
        }
        assertThat(
            contentValues.getAsString(CardDataSource.COLUMNS.SIDE.noun)).isEqualTo(if (card.side == Side.A) "A" else "B")

        if (card.otherFaceIds.size > 0) {
            assertThat(
                contentValues.getAsString(CardDataSource.COLUMNS.OTHER_FACE_IDS.noun)).isEqualTo(card.otherFaceIds.joinToString(","))
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
        whenever(cursor.getString(7)).thenReturn("U,W")

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

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.SCRYFALLID.noun)).thenReturn(33)
        whenever(cursor.getString(33)).thenReturn("05e2a5e6-3aaa-4096-bdd0-fcc1afe5a36c")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.TCG_PLAYER_PRODUCT_ID.noun)).thenReturn(
            34
        )
        whenever(cursor.getInt(34)).thenReturn(129859)

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.TCG_PLAYER_PURCHASE_URL.noun)).thenReturn(
            35
        )
        whenever(cursor.getString(35)).thenReturn("tcg_player_url")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.FACE_CMC.noun)).thenReturn(36)
        whenever(cursor.getInt(36)).thenReturn(4)
        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.IS_ARENA.noun)).thenReturn(37)
        whenever(cursor.getInt(37)).thenReturn(1)
        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.IS_MTGO.noun)).thenReturn(38)
        whenever(cursor.getInt(38)).thenReturn(1)
        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.SIDE.noun)).thenReturn(39)
        whenever(cursor.getString(39)).thenReturn("b")
        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.SCRYFALLID.noun)).thenReturn(40)
        whenever(cursor.getString(40)).thenReturn("05e2a5e6-3aaa-4096-bdd0-fcc1afe5a36c")

        whenever(cursor.getColumnIndex(CardDataSource.COLUMNS.OTHER_FACE_IDS.noun)).thenReturn(41)
        whenever(cursor.getString(41)).thenReturn("UUID-1,UUID-2")
    }
}

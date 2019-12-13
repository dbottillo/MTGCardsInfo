package com.dbottillo.mtgsearchfree.database

import android.database.sqlite.SQLiteDatabase
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.util.Logger
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.mock
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class CardsInfoDbHelperTest {

    private lateinit var cardsInfoDbHelper: CardsInfoDbHelper

    @Before
    fun setup() {
        cardsInfoDbHelper = CardsInfoDbHelper(RuntimeEnvironment.application)
    }

    @After
    fun tearDown() {
        cardsInfoDbHelper.clear()
    }

    @Test
    fun test_tables_are_created() {
        val tables = readTables(cardsInfoDbHelper)
        assertThat(tables.size).isEqualTo(7) // android_metadata + sqlite_sequence + number of tables required
        assertThat(tables.contains(CardDataSource.TABLE)).isEqualTo(true)
        assertThat(tables.contains(DeckDataSource.TABLE)).isEqualTo(true)
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN)).isEqualTo(true)
        assertThat(tables.contains(PlayerDataSource.TABLE)).isEqualTo(true)
        assertThat(tables.contains(FavouritesDataSource.TABLE)).isEqualTo(true)
    }

    @Test
    fun test_tables_are_cleared() {
        val db = cardsInfoDbHelper.writableDatabase
        addDummyData(db)
        assertTableExist(db, CardDataSource.TABLE)
        assertTableExist(db, DeckDataSource.TABLE)
        assertTableExist(db, DeckDataSource.TABLE_JOIN)
        assertTableExist(db, PlayerDataSource.TABLE)
        assertTableExist(db, FavouritesDataSource.TABLE)
        cardsInfoDbHelper.clear()
        assertTableDoesNotExist(db, CardDataSource.TABLE)
        assertTableDoesNotExist(db, DeckDataSource.TABLE)
        assertTableDoesNotExist(db, DeckDataSource.TABLE_JOIN)
        assertTableDoesNotExist(db, PlayerDataSource.TABLE)
        assertTableDoesNotExist(db, FavouritesDataSource.TABLE)
    }

    @Test
    fun test_db_upgrade_from_version1_to_version2() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 1)
        val tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(CardDataSource.TABLE)).isEqualTo(true)
        assertThat(tables.contains(DeckDataSource.TABLE)).isEqualTo(false)
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN)).isEqualTo(false)
        assertThat(tables.contains(PlayerDataSource.TABLE)).isEqualTo(false)
        assertThat(tables.contains(FavouritesDataSource.TABLE)).isEqualTo(false)
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun)).isEqualTo(false)
        cardsInfoDbHelper.onUpgrade(db, 1, 2)
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun)).isEqualTo(true)
    }

    @Test
    fun test_db_upgrade_from_version1_to_version3() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 1)
        var tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(CardDataSource.TABLE)).isEqualTo(true)
        assertThat(tables.contains(DeckDataSource.TABLE)).isEqualTo(false)
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN)).isEqualTo(false)
        assertThat(tables.contains(PlayerDataSource.TABLE)).isEqualTo(false)
        assertThat(tables.contains(FavouritesDataSource.TABLE)).isEqualTo(false)
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun)).isEqualTo(false)
        cardsInfoDbHelper.onUpgrade(db, 1, 3)
        tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(PlayerDataSource.TABLE)).isEqualTo(true)
        assertThat(tables.contains(FavouritesDataSource.TABLE)).isEqualTo(true)
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun)).isEqualTo(true)
    }

    @Test
    fun test_db_upgrade_from_version1_to_version4() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 1)
        var tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(CardDataSource.TABLE)).isEqualTo(true)
        assertThat(tables.contains(DeckDataSource.TABLE)).isEqualTo(false)
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN)).isEqualTo(false)
        assertThat(tables.contains(PlayerDataSource.TABLE)).isEqualTo(false)
        assertThat(tables.contains(FavouritesDataSource.TABLE)).isEqualTo(false)
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun)).isEqualTo(false)
        cardsInfoDbHelper.onUpgrade(db, 1, 4)
        tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(PlayerDataSource.TABLE)).isEqualTo(true)
        assertThat(tables.contains(FavouritesDataSource.TABLE)).isEqualTo(true)
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun)).isEqualTo(true)
    }

    @Test
    fun test_db_upgrade_from_version2_to_version3() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 2)
        var tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(CardDataSource.TABLE)).isEqualTo(true)
        assertThat(tables.contains(DeckDataSource.TABLE)).isEqualTo(false)
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN)).isEqualTo(false)
        assertThat(tables.contains(PlayerDataSource.TABLE)).isEqualTo(false)
        assertThat(tables.contains(FavouritesDataSource.TABLE)).isEqualTo(false)
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun)).isEqualTo(false)
        cardsInfoDbHelper.onUpgrade(db, 2, 3)
        tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(PlayerDataSource.TABLE)).isEqualTo(true)
        assertThat(tables.contains(FavouritesDataSource.TABLE)).isEqualTo(true)
        assertThat(tables.contains(DeckDataSource.TABLE)).isEqualTo(false)
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN)).isEqualTo(false)
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun)).isEqualTo(true)
    }

    @Test
    fun test_db_upgrade_from_version2_to_version4() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 2)
        var tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(CardDataSource.TABLE)).isEqualTo(true)
        assertThat(tables.contains(DeckDataSource.TABLE)).isEqualTo(false)
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN)).isEqualTo(false)
        assertThat(tables.contains(PlayerDataSource.TABLE)).isEqualTo(false)
        assertThat(tables.contains(FavouritesDataSource.TABLE)).isEqualTo(false)
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun)).isEqualTo(false)
        cardsInfoDbHelper.onUpgrade(db, 2, 4)
        tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(PlayerDataSource.TABLE)).isEqualTo(true)
        assertThat(tables.contains(FavouritesDataSource.TABLE)).isEqualTo(true)
        assertThat(tables.contains(DeckDataSource.TABLE)).isEqualTo(true)
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN)).isEqualTo(true)
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun)).isEqualTo(true)
    }

    @Test
    fun test_db_upgrade_from_version3_to_version4() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 3)
        var tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(CardDataSource.TABLE)).isEqualTo(true)
        assertThat(tables.contains(DeckDataSource.TABLE)).isEqualTo(false)
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN)).isEqualTo(false)
        assertThat(tables.contains(PlayerDataSource.TABLE)).isEqualTo(true)
        assertThat(tables.contains(FavouritesDataSource.TABLE)).isEqualTo(true)
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun)).isEqualTo(false)
        cardsInfoDbHelper.onUpgrade(db, 3, 4)
        tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(DeckDataSource.TABLE)).isEqualTo(true)
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN)).isEqualTo(true)
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun)).isEqualTo(true)
    }

    @Test
    fun test_db_upgrade_from_version4_to_version5_does_not_generate_error() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 4)
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun)).isEqualTo(true)
        cardsInfoDbHelper.onUpgrade(db, 4, 5)
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun)).isEqualTo(true)
    }

    @Test
    fun test_db_upgrade_from_version6_to_version7_does_not_generate_error() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 6)
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.NAMES.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.SUPER_TYPES.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.FLAVOR.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.ARTIST.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.LOYALTY.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.PRINTINGS.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.LEGALITIES.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.ORIGINAL_TEXT.noun)).isEqualTo(false)
        cardsInfoDbHelper.onUpgrade(db, 6, 7)
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.NAMES.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.SUPER_TYPES.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.FLAVOR.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.ARTIST.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.LOYALTY.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.PRINTINGS.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.LEGALITIES.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.ORIGINAL_TEXT.noun)).isEqualTo(true)
    }

    @Test
    fun test_db_upgrade_from_version7_to_version8_does_not_generate_error() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 7)
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.COLORS_IDENTITY.noun)).isEqualTo(false)
        cardsInfoDbHelper.onUpgrade(db, 7, 8)
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.COLORS_IDENTITY.noun)).isEqualTo(true)
    }

    @Test
    fun test_db_upgrade_from_version8_to_version9_does_not_generate_error() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 8)
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.UUID.noun)).isEqualTo(false)
        cardsInfoDbHelper.onUpgrade(db, 8, 9)
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.UUID.noun)).isEqualTo(true)
    }

    @Test
    fun test_db_upgrade_from_version9_to_version10_does_not_generate_error() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 9)
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.SCRYFALLID.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.TCG_PLAYER_PRODUCT_ID.noun)).isEqualTo(false)
        cardsInfoDbHelper.onUpgrade(db, 9, 10)
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.SCRYFALLID.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.TCG_PLAYER_PRODUCT_ID.noun)).isEqualTo(true)
    }

    @Test
    fun test_db_upgrade_from_version10_to_version11_does_not_generate_error() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 10)
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.TCG_PLAYER_PURCHASE_URL.noun)).isEqualTo(false)
        cardsInfoDbHelper.onUpgrade(db, 10, 11)
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.TCG_PLAYER_PURCHASE_URL.noun)).isEqualTo(true)
    }

    @Test
    fun test_db_upgrade_from_version11_to_version12_does_not_generate_error() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 11)
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.FACE_CMC.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.IS_ARENA.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.IS_MTGO.noun)).isEqualTo(false)
        assertThat(columns.contains(CardDataSource.COLUMNS.SIDE.noun)).isEqualTo(false)
        cardsInfoDbHelper.onUpgrade(db, 11, 12)
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.FACE_CMC.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.IS_ARENA.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.IS_MTGO.noun)).isEqualTo(true)
        assertThat(columns.contains(CardDataSource.COLUMNS.SIDE.noun)).isEqualTo(true)
    }

    private fun assertTableExist(db: SQLiteDatabase, table: String, exist: Boolean) {
        var isExist = false
        val cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '$table'", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                isExist = true
            }
            cursor.close()
        }
        assertTrue(isExist == exist)
    }

    private fun assertTableExist(db: SQLiteDatabase, table: String) = assertTableExist(db, table, true)

    private fun assertTableDoesNotExist(db: SQLiteDatabase, table: String) = assertTableExist(db, table, false)

    private fun readTables(cardsInfoDbHelper: CardsInfoDbHelper): Set<String> {
        return readTables(cardsInfoDbHelper.readableDatabase)
    }

    private fun readTables(db: SQLiteDatabase): Set<String> {
        val cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
        val tables = HashSet<String>(cursor.count)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            tables.add(cursor.getString(0))
            cursor.moveToNext()
        }
        cursor.close()
        return tables
    }

    private fun addDummyData(db: SQLiteDatabase) {
        val card = MTGCard()
        card.belongsTo(MTGSet(id = 1, name = "Zendikar", code = "ZDK"))
        val gson = Gson()
        val cardDataSource = CardDataSource(db, gson)
        val mtgCardDataSource = MTGCardDataSource(db, cardDataSource)
        val logger = mock<Logger>()
        val deckDataSource = DeckDataSource(db, cardDataSource, mtgCardDataSource, DeckColorMapper(gson), logger)
        val deck = deckDataSource.addDeck("deck")
        deckDataSource.addCardToDeck(deck, card, 2)
        val playerDataSource = PlayerDataSource(db)
        playerDataSource.savePlayer(Player(20, "liliana"))
        val favouritesDataSource = FavouritesDataSource(db, cardDataSource)
        favouritesDataSource.saveFavourites(card)
    }

    private fun downgradeDb(db: SQLiteDatabase, version: Int) {
        db.execSQL("DROP TABLE " + CardDataSource.TABLE)
        db.execSQL("DROP TABLE " + DeckDataSource.TABLE)
        db.execSQL("DROP TABLE " + DeckDataSource.TABLE_JOIN)
        db.execSQL("DROP TABLE " + FavouritesDataSource.TABLE)
        db.execSQL("DROP TABLE " + PlayerDataSource.TABLE)
        if (version == 1) {
            db.execSQL(CardDataSource.generateCreateTable(1))
        }
        if (version == 2) {
            db.execSQL(CardDataSource.generateCreateTable(2))
        }
        if (version == 3) {
            db.execSQL(CardDataSource.generateCreateTable(2))
            db.execSQL(PlayerDataSource.generateCreateTable())
            db.execSQL(FavouritesDataSource.generateCreateTable())
        }
        if (version == 4) {
            db.execSQL(CardDataSource.generateCreateTable())
            db.execSQL(PlayerDataSource.generateCreateTable())
            db.execSQL(FavouritesDataSource.generateCreateTable())
        }
        if (version == 6) {
            db.execSQL(CardDataSource.generateCreateTable(6))
            db.execSQL(PlayerDataSource.generateCreateTable())
            db.execSQL(FavouritesDataSource.generateCreateTable())
        }
        if (version == 7) {
            db.execSQL(CardDataSource.generateCreateTable(7))
            db.execSQL(PlayerDataSource.generateCreateTable())
            db.execSQL(FavouritesDataSource.generateCreateTable())
        }
        if (version == 8) {
            db.execSQL(CardDataSource.generateCreateTable(8))
            db.execSQL(PlayerDataSource.generateCreateTable())
            db.execSQL(FavouritesDataSource.generateCreateTable())
        }
        if (version == 9) {
            db.execSQL(CardDataSource.generateCreateTable(9))
            db.execSQL(PlayerDataSource.generateCreateTable())
            db.execSQL(FavouritesDataSource.generateCreateTable())
        }
        if (version == 10) {
            db.execSQL(CardDataSource.generateCreateTable(10))
            db.execSQL(PlayerDataSource.generateCreateTable())
            db.execSQL(FavouritesDataSource.generateCreateTable())
        }
        if (version == 11) {
            db.execSQL(CardDataSource.generateCreateTable(11))
            db.execSQL(PlayerDataSource.generateCreateTable())
            db.execSQL(FavouritesDataSource.generateCreateTable())
        }
    }
}
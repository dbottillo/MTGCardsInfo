package com.dbottillo.mtgsearchfree.model.database

import android.database.sqlite.SQLiteDatabase
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.Player
import com.google.gson.Gson
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
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
        assertThat(tables.size, `is`(7)) // android_metadata + sqlite_sequence + number of tables required
        assertThat(tables.contains(CardDataSource.TABLE), `is`(true))
        assertThat(tables.contains(DeckDataSource.TABLE), `is`(true))
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN), `is`(true))
        assertThat(tables.contains(PlayerDataSource.TABLE), `is`(true))
        assertThat(tables.contains(FavouritesDataSource.TABLE), `is`(true))
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
        assertTableDontExist(db, CardDataSource.TABLE)
        assertTableDontExist(db, DeckDataSource.TABLE)
        assertTableDontExist(db, DeckDataSource.TABLE_JOIN)
        assertTableDontExist(db, PlayerDataSource.TABLE)
        assertTableDontExist(db, FavouritesDataSource.TABLE)
    }

    @Test
    fun test_db_upgrade_from_version1_to_version2() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 1)
        val tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(CardDataSource.TABLE), `is`(true))
        assertThat(tables.contains(DeckDataSource.TABLE), `is`(false))
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN), `is`(false))
        assertThat(tables.contains(PlayerDataSource.TABLE), `is`(false))
        assertThat(tables.contains(FavouritesDataSource.TABLE), `is`(false))
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.noun), `is`(false))
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun), `is`(false))
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun), `is`(false))
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun), `is`(false))
        cardsInfoDbHelper.onUpgrade(db, 1, 2)
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun), `is`(true))
    }

    @Test
    fun test_db_upgrade_from_version1_to_version3() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 1)
        var tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(CardDataSource.TABLE), `is`(true))
        assertThat(tables.contains(DeckDataSource.TABLE), `is`(false))
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN), `is`(false))
        assertThat(tables.contains(PlayerDataSource.TABLE), `is`(false))
        assertThat(tables.contains(FavouritesDataSource.TABLE), `is`(false))
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.noun), `is`(false))
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun), `is`(false))
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun), `is`(false))
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun), `is`(false))
        cardsInfoDbHelper.onUpgrade(db, 1, 3)
        tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(PlayerDataSource.TABLE), `is`(true))
        assertThat(tables.contains(FavouritesDataSource.TABLE), `is`(true))
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun), `is`(true))
    }

    @Test
    fun test_db_upgrade_from_version1_to_version4() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 1)
        var tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(CardDataSource.TABLE), `is`(true))
        assertThat(tables.contains(DeckDataSource.TABLE), `is`(false))
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN), `is`(false))
        assertThat(tables.contains(PlayerDataSource.TABLE), `is`(false))
        assertThat(tables.contains(FavouritesDataSource.TABLE), `is`(false))
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.noun), `is`(false))
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun), `is`(false))
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun), `is`(false))
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun), `is`(false))
        cardsInfoDbHelper.onUpgrade(db, 1, 4)
        tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(PlayerDataSource.TABLE), `is`(true))
        assertThat(tables.contains(FavouritesDataSource.TABLE), `is`(true))
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun), `is`(true))
    }

    @Test
    fun test_db_upgrade_from_version2_to_version3() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 2)
        var tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(CardDataSource.TABLE), `is`(true))
        assertThat(tables.contains(DeckDataSource.TABLE), `is`(false))
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN), `is`(false))
        assertThat(tables.contains(PlayerDataSource.TABLE), `is`(false))
        assertThat(tables.contains(FavouritesDataSource.TABLE), `is`(false))
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun), `is`(false))
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun), `is`(false))
        cardsInfoDbHelper.onUpgrade(db, 2, 3)
        tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(PlayerDataSource.TABLE), `is`(true))
        assertThat(tables.contains(FavouritesDataSource.TABLE), `is`(true))
        assertThat(tables.contains(DeckDataSource.TABLE), `is`(false))
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN), `is`(false))
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun), `is`(true))
    }

    @Test
    fun test_db_upgrade_from_version2_to_version4() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 2)
        var tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(CardDataSource.TABLE), `is`(true))
        assertThat(tables.contains(DeckDataSource.TABLE), `is`(false))
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN), `is`(false))
        assertThat(tables.contains(PlayerDataSource.TABLE), `is`(false))
        assertThat(tables.contains(FavouritesDataSource.TABLE), `is`(false))
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun), `is`(false))
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun), `is`(false))
        cardsInfoDbHelper.onUpgrade(db, 2, 4)
        tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(PlayerDataSource.TABLE), `is`(true))
        assertThat(tables.contains(FavouritesDataSource.TABLE), `is`(true))
        assertThat(tables.contains(DeckDataSource.TABLE), `is`(true))
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN), `is`(true))
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun), `is`(true))
    }

    @Test
    fun test_db_upgrade_from_version3_to_version4() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 3)
        var tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(CardDataSource.TABLE), `is`(true))
        assertThat(tables.contains(DeckDataSource.TABLE), `is`(false))
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN), `is`(false))
        assertThat(tables.contains(PlayerDataSource.TABLE), `is`(true))
        assertThat(tables.contains(FavouritesDataSource.TABLE), `is`(true))
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun), `is`(false))
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun), `is`(false))
        cardsInfoDbHelper.onUpgrade(db, 3, 4)
        tables = readTables(cardsInfoDbHelper)
        assertThat(tables.contains(DeckDataSource.TABLE), `is`(true))
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN), `is`(true))
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.noun), `is`(true))
    }

    @Test
    fun test_db_upgrade_from_version4_to_version5_does_not_generate_error() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 4)
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun), `is`(true))
        cardsInfoDbHelper.onUpgrade(db, 4, 5)
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.noun), `is`(true))
    }

    @Test
    fun test_db_upgrade_from_version6_to_version7_does_not_generate_error() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 6)
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.NAMES.noun), `is`(false))
        assertThat(columns.contains(CardDataSource.COLUMNS.SUPER_TYPES.noun), `is`(false))
        assertThat(columns.contains(CardDataSource.COLUMNS.FLAVOR.noun), `is`(false))
        assertThat(columns.contains(CardDataSource.COLUMNS.ARTIST.noun), `is`(false))
        assertThat(columns.contains(CardDataSource.COLUMNS.LOYALTY.noun), `is`(false))
        assertThat(columns.contains(CardDataSource.COLUMNS.PRINTINGS.noun), `is`(false))
        assertThat(columns.contains(CardDataSource.COLUMNS.LEGALITIES.noun), `is`(false))
        assertThat(columns.contains(CardDataSource.COLUMNS.ORIGINAL_TEXT.noun), `is`(false))
        cardsInfoDbHelper.onUpgrade(db, 6, 7)
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.NAMES.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.SUPER_TYPES.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.FLAVOR.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.ARTIST.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.LOYALTY.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.PRINTINGS.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.LEGALITIES.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.ORIGINAL_TEXT.noun), `is`(true))
    }

    @Test
    fun test_db_upgrade_from_version7_to_version8_does_not_generate_error() {
        val db = cardsInfoDbHelper.writableDatabase
        downgradeDb(db, 7)
        var columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.MCI_NUMBER.noun), `is`(false))
        assertThat(columns.contains(CardDataSource.COLUMNS.COLORS_IDENTITY.noun), `is`(false))
        cardsInfoDbHelper.onUpgrade(db, 6, 7)
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE)
        assertThat(columns.contains(CardDataSource.COLUMNS.MCI_NUMBER.noun), `is`(true))
        assertThat(columns.contains(CardDataSource.COLUMNS.COLORS_IDENTITY.noun), `is`(true))
    }

    private fun assertTableExistance(db: SQLiteDatabase, table: String, exist: Boolean) {
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

    private fun assertTableExist(db: SQLiteDatabase, table: String) = assertTableExistance(db, table, true)

    private fun assertTableDontExist(db: SQLiteDatabase, table: String) = assertTableExistance(db, table, false)

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
        val cardDataSource = CardDataSource(db, Gson())
        val mtgCardDataSource = MTGCardDataSource(db, cardDataSource)
        val deckDataSource = DeckDataSource(db, cardDataSource, mtgCardDataSource)
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
    }
}
package com.dbottillo.mtgsearchfree.database

import com.dbottillo.mtgsearchfree.model.Player
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class PlayerDataSourceTest {

    private lateinit var cardsInfoDbHelper: CardsInfoDbHelper
    private lateinit var underTest: PlayerDataSource

    @Before
    fun setup() {
        cardsInfoDbHelper = CardsInfoDbHelper(RuntimeEnvironment.application)
        underTest = PlayerDataSource(cardsInfoDbHelper.writableDatabase)
    }

    @After
    fun tearDown() {
        cardsInfoDbHelper.clear()
        cardsInfoDbHelper.close()
    }

    @Test
    fun generate_table_is_correct() {
        val query = PlayerDataSource.generateCreateTable()
        assertNotNull(query)
        assertThat(query, `is`("CREATE TABLE IF NOT EXISTS MTGPlayer (_id INTEGER PRIMARY KEY, name TEXT,life INT,poison INT)"))
    }

    @Test
    fun player_can_be_saved_in_database() {
        val player = generatePlayer()
        val id = underTest.savePlayer(player)
        val cursor = cardsInfoDbHelper.readableDatabase.rawQuery("select * from " + PlayerDataSource.TABLE + " where rowid =?", arrayOf(id.toString() + ""))
        assertNotNull(cursor)
        assertThat(cursor.count, `is`(1))
        cursor.moveToFirst()
        val playerFromDb = underTest.fromCursor(cursor)
        assertNotNull(playerFromDb)
        assertPlayer(playerFromDb, player)
        cursor.close()
    }

    @Test
    fun player_can_be_removed_from_database() {
        val player = generatePlayer()
        val id = underTest.savePlayer(player)
        underTest.removePlayer(player)
        val cursor = cardsInfoDbHelper.readableDatabase.rawQuery("select * from " + PlayerDataSource.TABLE + " where rowid =?", arrayOf(id.toString() + ""))
        assertNotNull(cursor)
        assertThat(cursor.count, `is`(0))
        cursor.close()
    }

    @Test
    fun player_are_unique_in_database() {
        val db = cardsInfoDbHelper.writableDatabase
        val uniqueId = 444
        val player = generatePlayer(uniqueId, "Jayce", 15, 2)
        val id = underTest.savePlayer(player)
        val player2 = generatePlayer(uniqueId, "Jayce", 18, 4)
        val id2 = underTest.savePlayer(player2)
        assertThat(id, `is`(id2))
        val cursor = db.rawQuery("select * from " + PlayerDataSource.TABLE + " where _id =?", arrayOf(uniqueId.toString() + ""))
        assertNotNull(cursor)
        assertThat(cursor.count, `is`(1))
        cursor.moveToFirst()
        val playerFromDb = underTest.fromCursor(cursor)
        assertNotNull(playerFromDb)
        assertPlayer(playerFromDb, player2)
        cursor.close()
    }

    @Test
    fun test_cards_can_be_retrieved_from_database() {
        val player1 = generatePlayer()
        underTest.savePlayer(player1)
        val player2 = generatePlayer(20, "Liliana", 10, 10)
        underTest.savePlayer(player2)
        val player3 = generatePlayer(30, "Garruk", 12, 3)
        underTest.savePlayer(player3)
        val player = underTest.players
        assertNotNull(player)
        assertThat(player.size, `is`(3))
        assertPlayer(player[0], player1)
        assertPlayer(player[1], player2)
        assertPlayer(player[2], player3)
    }

    private fun generatePlayer(id: Int = 10, name: String = "Jayce", life: Int = 15, poison: Int = 2): Player {
        return Player(id = id, name = name, life = life, poisonCount = poison)
    }

    private fun assertPlayer(one: Player, two: Player) {
        assertThat(one.id, `is`(two.id))
        assertThat(one.name, `is`(two.name))
        assertThat(one.life, `is`(two.life))
        assertThat(one.poisonCount, `is`(two.poisonCount))
    }
}
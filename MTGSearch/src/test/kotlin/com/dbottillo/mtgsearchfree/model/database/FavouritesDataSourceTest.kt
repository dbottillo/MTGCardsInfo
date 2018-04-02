package com.dbottillo.mtgsearchfree.model.database

import com.google.gson.Gson
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class FavouritesDataSourceTest {

    lateinit var mtgCardDataSource: MTGCardDataSource
    lateinit var cardsInfoDbHelper: CardsInfoDbHelper
    lateinit var mtgDatabaseHelper: MTGDatabaseHelper
    lateinit var underTest: FavouritesDataSource

    @Before
    fun setup() {
        mtgDatabaseHelper = MTGDatabaseHelper(RuntimeEnvironment.application)
        cardsInfoDbHelper = CardsInfoDbHelper(RuntimeEnvironment.application)
        val cardDataSource = CardDataSource(cardsInfoDbHelper.writableDatabase, Gson())
        mtgCardDataSource = MTGCardDataSource(mtgDatabaseHelper.readableDatabase, cardDataSource)
        underTest = FavouritesDataSource(cardsInfoDbHelper.writableDatabase, cardDataSource)
    }

    @After
    fun tearDown() {
        cardsInfoDbHelper.clear()
        cardsInfoDbHelper.close()
        mtgDatabaseHelper.close()
    }

    @Test
    fun generate_table_is_correct() {
        val query = FavouritesDataSource.generateCreateTable()
        assertNotNull(query)
        assertThat(query, `is`("CREATE TABLE IF NOT EXISTS Favourites (_id INTEGER PRIMARY KEY)"))
    }

    @Test
    fun cards_can_be_saved_as_favourites() {
        val cards = mtgCardDataSource.getRandomCard(3)
        for (card in cards) {
            underTest.saveFavourites(card)
        }
        val favouritesCard = underTest.getCards(true)
        assertThat(favouritesCard.size, `is`(cards.size))
        assertTrue(cards.containsAll(favouritesCard))
        assertTrue(favouritesCard.containsAll(cards))
    }

    @Test
    fun cards_can_be_removed_from_favourites() {
        val cards = mtgCardDataSource.getRandomCard(3)
        for (card in cards) {
            underTest.saveFavourites(card)
        }
        underTest.removeFavourites(cards[0])
        val favouritesCard = underTest.getCards(true)
        assertThat(favouritesCard.size, `is`(2))
        assertTrue(cards.containsAll(favouritesCard))
        assertFalse(favouritesCard.contains(cards[0]))
    }

}
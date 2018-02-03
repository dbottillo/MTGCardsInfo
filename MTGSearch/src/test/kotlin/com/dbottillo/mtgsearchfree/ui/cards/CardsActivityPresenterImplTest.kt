package com.dbottillo.mtgsearchfree.ui.cards

import android.content.Intent
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractor
import com.dbottillo.mtgsearchfree.model.*
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit

class CardsActivityPresenterImplTest {

    @Rule @JvmField
    var mockitoRule = MockitoJUnit.rule()

    lateinit var underTest: CardsActivityPresenter
    internal val idFavs = intArrayOf(2, 3, 4)

    @Mock
    lateinit var cardsInteractor: CardsInteractor
    @Mock
    lateinit var savedCardsInteractor: SavedCardsInteractor
    @Mock
    lateinit var decksInteractor: DecksInteractor
    @Mock
    lateinit var cardsPreferences: CardsPreferences
    @Mock
    lateinit var view: CardsActivityView
    @Mock
    internal lateinit var logger: Logger
    @Mock
    internal lateinit var intent: Intent
    @Mock
    internal lateinit var set: MTGSet
    @Mock
    internal lateinit var deck: Deck
    @Mock
    internal lateinit var cards: CardsCollection
    @Mock
    internal lateinit var deckCollection: DeckCollection
    @Mock
    internal lateinit var error: Throwable
    @Mock
    internal lateinit var searchParams: SearchParams
    @Mock
    internal lateinit var card: MTGCard

    @Before
    fun setUp() {
        underTest = CardsActivityPresenterImpl(cardsInteractor, savedCardsInteractor, decksInteractor,
                cardsPreferences, logger)
    }

    @Test
    fun `init view with null intent should call finish`() {
        underTest.init(view, null)

        verify(view).finish()
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `init view with intent that contains a not recognizable should call finish`() {
        `when`(intent.hasExtra(CardsActivity.KEY_SET)).thenReturn(false)
        `when`(intent.hasExtra(CardsActivity.KEY_SEARCH)).thenReturn(false)
        `when`(intent.hasExtra(CardsActivity.KEY_DECK)).thenReturn(false)
        `when`(intent.hasExtra(CardsActivity.KEY_FAV)).thenReturn(false)

        underTest.init(view, intent)

        verify(view).finish()
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `init view with intent that contains set should load favourites and then set`() {
        `when`(cardsInteractor.loadIdFav()).thenReturn(Observable.just(idFavs))
        `when`(intent.hasExtra(CardsActivity.KEY_SET)).thenReturn(true)
        `when`(intent.getParcelableExtra<MTGSet>(CardsActivity.KEY_SET)).thenReturn(set)
        `when`(intent.getIntExtra(CardsActivity.POSITION, 0)).thenReturn(5)
        `when`(cardsPreferences.showImage()).thenReturn(true)
        `when`(cardsInteractor.loadSet(set)).thenReturn(Observable.just(cards))
        `when`(set.name).thenReturn("Set name")

        underTest.init(view, intent)

        verify(cardsInteractor).loadIdFav()
        verify(cardsInteractor).loadSet(set)
        verify(cardsPreferences).showImage()
        verify(view).updateTitle("Set name")
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).updateAdapter(cards, true, 5)
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `init view with intent that contains set should load show error if observable fails`() {
        `when`(cardsInteractor.loadIdFav()).thenReturn(Observable.just(idFavs))
        `when`(error.localizedMessage).thenReturn("error message")
        `when`(intent.hasExtra(CardsActivity.KEY_SET)).thenReturn(true)
        `when`(intent.getParcelableExtra<MTGSet>(CardsActivity.KEY_SET)).thenReturn(set)
        `when`(intent.getIntExtra(CardsActivity.POSITION, 0)).thenReturn(5)
        `when`(cardsInteractor.loadSet(set)).thenReturn(Observable.error(error))
        `when`(set.name).thenReturn("Set name")

        underTest.init(view, intent)

        verify(cardsInteractor).loadIdFav()
        verify(cardsInteractor).loadSet(set)
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).updateTitle("Set name")
        verify(view).showError("error message")
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `init view with intent that contains deck should load favourites and then deck`() {
        `when`(cardsInteractor.loadIdFav()).thenReturn(Observable.just(idFavs))
        `when`(intent.hasExtra(CardsActivity.KEY_SET)).thenReturn(false)
        `when`(intent.hasExtra(CardsActivity.KEY_DECK)).thenReturn(true)
        `when`(intent.getParcelableExtra<Deck>(CardsActivity.KEY_DECK)).thenReturn(deck)
        `when`(intent.getIntExtra(CardsActivity.POSITION, 0)).thenReturn(5)
        `when`(cardsPreferences.showImage()).thenReturn(true)
        `when`(deckCollection.toCardsCollection()).thenReturn(cards)
        `when`(decksInteractor.loadDeck(deck)).thenReturn(Observable.just(deckCollection))
        `when`(deck.name).thenReturn("Deck name")

        underTest.init(view, intent)

        verify(cardsInteractor).loadIdFav()
        verify(decksInteractor).loadDeck(deck)
        verify(cardsPreferences).showImage()
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).updateTitle("Deck name")
        verify(view).updateAdapter(cards, true, 5)
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

     @Test
     fun `init view with intent that contains deck should load show error if observable fails`() {
         `when`(cardsInteractor.loadIdFav()).thenReturn(Observable.just(idFavs))
         `when`(error.localizedMessage).thenReturn("error message")
         `when`(intent.hasExtra(CardsActivity.KEY_SET)).thenReturn(false)
         `when`(intent.hasExtra(CardsActivity.KEY_DECK)).thenReturn(true)
         `when`(intent.getParcelableExtra<Deck>(CardsActivity.KEY_DECK)).thenReturn(deck)
         `when`(intent.getIntExtra(CardsActivity.POSITION, 0)).thenReturn(5)
         `when`(decksInteractor.loadDeck(deck)).thenReturn(Observable.error(error))
         `when`(deck.name).thenReturn("Deck name")

         underTest.init(view, intent)

         verify(cardsInteractor).loadIdFav()
         verify(decksInteractor).loadDeck(deck)
         verify(view).showLoading()
         verify(view).hideLoading()
         verify(view).updateTitle("Deck name")
         verify(view).showError("error message")
         verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
     }

    @Test
    fun `init view with intent that contains search should load favourites and then perform search`() {
        `when`(cardsInteractor.loadIdFav()).thenReturn(Observable.just(idFavs))
        `when`(intent.hasExtra(CardsActivity.KEY_SET)).thenReturn(false)
        `when`(intent.hasExtra(CardsActivity.KEY_DECK)).thenReturn(false)
        `when`(intent.hasExtra(CardsActivity.KEY_SEARCH)).thenReturn(true)
        `when`(intent.getParcelableExtra<SearchParams>(CardsActivity.KEY_SEARCH)).thenReturn(searchParams)
        `when`(cardsPreferences.showImage()).thenReturn(true)
        `when`(cardsInteractor.doSearch(searchParams)).thenReturn(Observable.just(cards))

        underTest.init(view, intent)

        verify(cardsInteractor).loadIdFav()
        verify(cardsInteractor).doSearch(searchParams)
        verify(cardsPreferences).showImage()
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).updateTitle(R.string.action_search)
        verify(view).updateAdapter(cards, true, 0)
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `init view with intent that contains search should load show error if observable fails`() {
        `when`(cardsInteractor.loadIdFav()).thenReturn(Observable.just(idFavs))
        `when`(error.localizedMessage).thenReturn("error message")
        `when`(intent.hasExtra(CardsActivity.KEY_SET)).thenReturn(false)
        `when`(intent.hasExtra(CardsActivity.KEY_DECK)).thenReturn(false)
        `when`(intent.hasExtra(CardsActivity.KEY_SEARCH)).thenReturn(true)
        `when`(intent.getParcelableExtra<SearchParams>(CardsActivity.KEY_SEARCH)).thenReturn(searchParams)
        `when`(cardsInteractor.doSearch(searchParams)).thenReturn(Observable.error(error))

        underTest.init(view, intent)

        verify(cardsInteractor).loadIdFav()
        verify(cardsInteractor).doSearch(searchParams)
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).updateTitle(R.string.action_search)
        verify(view).showError("error message")
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `init view with intent that contains favourites should load favourites and then favourites' cards`() {
        `when`(cardsInteractor.loadIdFav()).thenReturn(Observable.just(idFavs))
        `when`(intent.hasExtra(CardsActivity.KEY_SET)).thenReturn(false)
        `when`(intent.hasExtra(CardsActivity.KEY_DECK)).thenReturn(false)
        `when`(intent.hasExtra(CardsActivity.KEY_SEARCH)).thenReturn(false)
        `when`(intent.hasExtra(CardsActivity.KEY_FAV)).thenReturn(true)
        `when`(cardsPreferences.showImage()).thenReturn(true)
        `when`(savedCardsInteractor.load()).thenReturn(Observable.just(cards))

        underTest.init(view, intent)

        verify(cardsInteractor).loadIdFav()
        verify(savedCardsInteractor).load()
        verify(cardsPreferences).showImage()
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).updateTitle(R.string.action_saved)
        verify(view).updateAdapter(cards, true, 0)
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `init view with intent that contains favourites should load show error if observable fails`() {
        `when`(cardsInteractor.loadIdFav()).thenReturn(Observable.just(idFavs))
        `when`(error.localizedMessage).thenReturn("error message")
        `when`(intent.hasExtra(CardsActivity.KEY_SET)).thenReturn(false)
        `when`(intent.hasExtra(CardsActivity.KEY_DECK)).thenReturn(false)
        `when`(intent.hasExtra(CardsActivity.KEY_SEARCH)).thenReturn(false)
        `when`(intent.hasExtra(CardsActivity.KEY_FAV)).thenReturn(true)
        `when`(savedCardsInteractor.load()).thenReturn(Observable.error(error))

        underTest.init(view, intent)

        verify(cardsInteractor).loadIdFav()
        verify(savedCardsInteractor).load()
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).updateTitle(R.string.action_saved)
        verify(view).showError("error message")
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `update menu should do nothing if called before id favs are loaded`() {
        underTest.updateMenu(card)

        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `update menu should remove fav item menu if card is null and show image menu item`() {
        prepareAfterInit()
        `when`(cardsPreferences.showImage()).thenReturn(true)

        underTest.updateMenu(null)

        verify(view).hideFavMenuItem()
        verify(view).setImageMenuItemChecked(true)
        verify(cardsPreferences).showImage()
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `update menu should remove fav item menu if card is null and hide image menu item`() {
        prepareAfterInit()
        `when`(cardsPreferences.showImage()).thenReturn(false)

        underTest.updateMenu(null)

        verify(view).hideFavMenuItem()
        verify(view).setImageMenuItemChecked(false)
        verify(cardsPreferences).showImage()
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `update menu should remove fav item menu if multiverse id is less than 0 and show image menu item`() {
        prepareAfterInit()
        `when`(card.multiVerseId).thenReturn(0)
        `when`(cardsPreferences.showImage()).thenReturn(true)

        underTest.updateMenu(card)

        verify(view).hideFavMenuItem()
        verify(view).setImageMenuItemChecked(true)
        verify(cardsPreferences).showImage()
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `update menu should remove fav item menu if multiverse id is less than 0 and hide image menu item`() {
        prepareAfterInit()
        `when`(card.multiVerseId).thenReturn(0)
        `when`(cardsPreferences.showImage()).thenReturn(false)

        underTest.updateMenu(card)

        verify(view).hideFavMenuItem()
        verify(view).setImageMenuItemChecked(false)
        verify(cardsPreferences).showImage()
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `update menu should show and update fav item menu if card is not a fav and show image menu item`() {
        prepareAfterInit()
        `when`(card.multiVerseId).thenReturn(1)
        `when`(cardsPreferences.showImage()).thenReturn(true)

        underTest.updateMenu(card)

        verify(view).showFavMenuItem()
        verify(view).updateFavMenuItem(R.string.favourite_add, R.drawable.ab_star)
        verify(view).setImageMenuItemChecked(true)
        verify(cardsPreferences).showImage()
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `update menu should show and update fav item menu if card is not a fav and hide image menu item`() {
        prepareAfterInit()
        `when`(card.multiVerseId).thenReturn(1)
        `when`(cardsPreferences.showImage()).thenReturn(false)

        underTest.updateMenu(card)

        verify(view).showFavMenuItem()
        verify(view).updateFavMenuItem(R.string.favourite_add, R.drawable.ab_star)
        verify(view).setImageMenuItemChecked(false)
        verify(cardsPreferences).showImage()
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `update menu should show and update fav item menu if card is a fav and show image menu item`() {
        prepareAfterInit()
        `when`(card.multiVerseId).thenReturn(2)
        `when`(cardsPreferences.showImage()).thenReturn(true)

        underTest.updateMenu(card)

        verify(view).showFavMenuItem()
        verify(view).updateFavMenuItem(R.string.favourite_remove, R.drawable.ab_star_colored)
        verify(view).setImageMenuItemChecked(true)
        verify(cardsPreferences).showImage()
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `update menu should show and update fav item menu if card is a fav and hide image menu item`() {
        prepareAfterInit()
        `when`(card.multiVerseId).thenReturn(2)
        `when`(cardsPreferences.showImage()).thenReturn(false)

        underTest.updateMenu(card)

        verify(view).showFavMenuItem()
        verify(view).updateFavMenuItem(R.string.favourite_remove, R.drawable.ab_star_colored)
        verify(view).setImageMenuItemChecked(false)
        verify(cardsPreferences).showImage()
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    internal fun prepareAfterInit(){
        `when`(cardsInteractor.loadIdFav()).thenReturn(Observable.just(idFavs))
        `when`(intent.hasExtra(CardsActivity.KEY_SET)).thenReturn(true)
        `when`(intent.getParcelableExtra<MTGSet>(CardsActivity.KEY_SET)).thenReturn(set)
        `when`(intent.getIntExtra(CardsActivity.POSITION, 0)).thenReturn(5)
        `when`(cardsPreferences.showImage()).thenReturn(true)
        `when`(cardsInteractor.loadSet(set)).thenReturn(Observable.just(cards))
        `when`(set.name).thenReturn("Set name")
        underTest.init(view, intent)
        Mockito.reset(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }
}
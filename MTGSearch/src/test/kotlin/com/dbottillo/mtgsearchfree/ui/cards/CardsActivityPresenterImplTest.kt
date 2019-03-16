package com.dbottillo.mtgsearchfree.ui.cards

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractor
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.DeckCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.util.Logger
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit

class CardsActivityPresenterImplTest {

    @Rule @JvmField var mockitoRule = MockitoJUnit.rule()!!

    lateinit var underTest: CardsActivityPresenter

    private val idFavs = intArrayOf(2, 3, 4)

    @Mock lateinit var cardsInteractor: CardsInteractor
    @Mock lateinit var savedCardsInteractor: SavedCardsInteractor
    @Mock lateinit var decksInteractor: DecksInteractor
    @Mock lateinit var cardsPreferences: CardsPreferences
    @Mock lateinit var view: CardsActivityView
    @Mock lateinit var logger: Logger
    @Mock lateinit var intent: Intent
    @Mock lateinit var set: MTGSet
    @Mock lateinit var deck: Deck
    @Mock lateinit var cards: CardsCollection
    @Mock lateinit var deckCollection: DeckCollection
    @Mock lateinit var error: Throwable
    @Mock lateinit var searchParams: SearchParams
    @Mock lateinit var card: MTGCard

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
        whenever(intent.hasExtra(KEY_SET)).thenReturn(false)
        whenever(intent.hasExtra(KEY_SEARCH)).thenReturn(false)
        whenever(intent.hasExtra(KEY_DECK)).thenReturn(false)
        whenever(intent.hasExtra(KEY_FAV)).thenReturn(false)

        underTest.init(view, intent)

        verify(view).finish()
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `init view with intent that contains set should load favourites and then set`() {
        whenever(cardsInteractor.loadIdFav()).thenReturn(Observable.just(idFavs))
        whenever(intent.hasExtra(KEY_SET)).thenReturn(true)
        whenever(intent.getParcelableExtra<MTGSet>(KEY_SET)).thenReturn(set)
        whenever(intent.getIntExtra(POSITION, 0)).thenReturn(5)
        whenever(cardsPreferences.showImage()).thenReturn(true)
        whenever(cardsInteractor.loadSet(set)).thenReturn(Observable.just(cards))
        whenever(set.name).thenReturn("Set name")

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
        whenever(cardsInteractor.loadIdFav()).thenReturn(Observable.just(idFavs))
        whenever(error.localizedMessage).thenReturn("error message")
        whenever(intent.hasExtra(KEY_SET)).thenReturn(true)
        whenever(intent.getParcelableExtra<MTGSet>(KEY_SET)).thenReturn(set)
        whenever(intent.getIntExtra(POSITION, 0)).thenReturn(5)
        whenever(cardsInteractor.loadSet(set)).thenReturn(Observable.error(error))
        whenever(set.name).thenReturn("Set name")

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
        whenever(cardsInteractor.loadIdFav()).thenReturn(Observable.just(idFavs))
        whenever(intent.hasExtra(KEY_SET)).thenReturn(false)
        whenever(intent.hasExtra(KEY_DECK)).thenReturn(true)
        whenever(intent.getLongExtra(KEY_DECK, 0)).thenReturn(2L)
        whenever(intent.getIntExtra(POSITION, 0)).thenReturn(5)
        whenever(cardsPreferences.showImage()).thenReturn(true)
        whenever(deckCollection.toCardsCollection()).thenReturn(cards)
        whenever(deck.id).thenReturn(2L)
        whenever(decksInteractor.loadDeckById(2L)).thenReturn(Single.just(deck))
        whenever(decksInteractor.loadDeck(2L)).thenReturn(Observable.just(deckCollection))
        whenever(deck.name).thenReturn("Deck name")

        underTest.init(view, intent)

        verify(cardsInteractor).loadIdFav()
        verify(decksInteractor).loadDeckById(2L)
        verify(decksInteractor).loadDeck(2L)
        verify(cardsPreferences).showImage()
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).updateTitle("Deck name")
        verify(view).updateAdapter(cards, true, 5)
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `init view with intent that contains deck should load show error if observable fails`() {
        whenever(cardsInteractor.loadIdFav()).thenReturn(Observable.just(idFavs))
        whenever(error.localizedMessage).thenReturn("error message")
        whenever(intent.hasExtra(KEY_SET)).thenReturn(false)
        whenever(intent.hasExtra(KEY_DECK)).thenReturn(true)
        whenever(intent.getLongExtra(KEY_DECK, 0)).thenReturn(2L)
        whenever(intent.getIntExtra(POSITION, 0)).thenReturn(5)
        whenever(deck.id).thenReturn(2L)
        whenever(decksInteractor.loadDeckById(2L)).thenReturn(Single.just(deck))
        whenever(decksInteractor.loadDeck(2L)).thenReturn(Observable.error(error))
        whenever(deck.name).thenReturn("Deck name")

        underTest.init(view, intent)

        verify(cardsInteractor).loadIdFav()
        verify(decksInteractor).loadDeckById(2L)
        verify(decksInteractor).loadDeck(2L)
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showError("error message")
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `init view with intent that contains search should load favourites and then perform search`() {
        whenever(cardsInteractor.loadIdFav()).thenReturn(Observable.just(idFavs))
        whenever(intent.hasExtra(KEY_SET)).thenReturn(false)
        whenever(intent.hasExtra(KEY_DECK)).thenReturn(false)
        whenever(intent.hasExtra(KEY_SEARCH)).thenReturn(true)
        whenever(intent.getParcelableExtra<SearchParams>(KEY_SEARCH)).thenReturn(searchParams)
        whenever(cardsPreferences.showImage()).thenReturn(true)
        whenever(cardsInteractor.doSearch(searchParams)).thenReturn(Observable.just(cards))

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
        whenever(cardsInteractor.loadIdFav()).thenReturn(Observable.just(idFavs))
        whenever(error.localizedMessage).thenReturn("error message")
        whenever(intent.hasExtra(KEY_SET)).thenReturn(false)
        whenever(intent.hasExtra(KEY_DECK)).thenReturn(false)
        whenever(intent.hasExtra(KEY_SEARCH)).thenReturn(true)
        whenever(intent.getParcelableExtra<SearchParams>(KEY_SEARCH)).thenReturn(searchParams)
        whenever(cardsInteractor.doSearch(searchParams)).thenReturn(Observable.error(error))

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
        whenever(cardsInteractor.loadIdFav()).thenReturn(Observable.just(idFavs))
        whenever(intent.hasExtra(KEY_SET)).thenReturn(false)
        whenever(intent.hasExtra(KEY_DECK)).thenReturn(false)
        whenever(intent.hasExtra(KEY_SEARCH)).thenReturn(false)
        whenever(intent.hasExtra(KEY_FAV)).thenReturn(true)
        whenever(cardsPreferences.showImage()).thenReturn(true)
        whenever(savedCardsInteractor.load()).thenReturn(Observable.just(cards))

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
        whenever(cardsInteractor.loadIdFav()).thenReturn(Observable.just(idFavs))
        whenever(error.localizedMessage).thenReturn("error message")
        whenever(intent.hasExtra(KEY_SET)).thenReturn(false)
        whenever(intent.hasExtra(KEY_DECK)).thenReturn(false)
        whenever(intent.hasExtra(KEY_SEARCH)).thenReturn(false)
        whenever(intent.hasExtra(KEY_FAV)).thenReturn(true)
        whenever(savedCardsInteractor.load()).thenReturn(Observable.error(error))

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
        whenever(cardsPreferences.showImage()).thenReturn(true)

        underTest.updateMenu(null)

        verify(view).hideFavMenuItem()
        verify(view).setImageMenuItemChecked(true)
        verify(cardsPreferences).showImage()
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `update menu should remove fav item menu if card is null and hide image menu item`() {
        prepareAfterInit()
        whenever(cardsPreferences.showImage()).thenReturn(false)

        underTest.updateMenu(null)

        verify(view).hideFavMenuItem()
        verify(view).setImageMenuItemChecked(false)
        verify(cardsPreferences).showImage()
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `update menu should remove fav item menu if multiverse id is less than 0 and show image menu item`() {
        prepareAfterInit()
        whenever(card.multiVerseId).thenReturn(0)
        whenever(cardsPreferences.showImage()).thenReturn(true)

        underTest.updateMenu(card)

        verify(view).hideFavMenuItem()
        verify(view).setImageMenuItemChecked(true)
        verify(cardsPreferences).showImage()
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `update menu should remove fav item menu if multiverse id is less than 0 and hide image menu item`() {
        prepareAfterInit()
        whenever(card.multiVerseId).thenReturn(0)
        whenever(cardsPreferences.showImage()).thenReturn(false)

        underTest.updateMenu(card)

        verify(view).hideFavMenuItem()
        verify(view).setImageMenuItemChecked(false)
        verify(cardsPreferences).showImage()
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `update menu should show and update fav item menu if card is not a fav and show image menu item`() {
        prepareAfterInit()
        whenever(card.multiVerseId).thenReturn(1)
        whenever(cardsPreferences.showImage()).thenReturn(true)

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
        whenever(card.multiVerseId).thenReturn(1)
        whenever(cardsPreferences.showImage()).thenReturn(false)

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
        whenever(card.multiVerseId).thenReturn(2)
        whenever(cardsPreferences.showImage()).thenReturn(true)

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
        whenever(card.multiVerseId).thenReturn(2)
        whenever(cardsPreferences.showImage()).thenReturn(false)

        underTest.updateMenu(card)

        verify(view).showFavMenuItem()
        verify(view).updateFavMenuItem(R.string.favourite_remove, R.drawable.ab_star_colored)
        verify(view).setImageMenuItemChecked(false)
        verify(cardsPreferences).showImage()
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `share bitmap should get artwork and share it on view`() {
        prepareAfterInit()
        val bitmap = mock<Bitmap>()
        val uri = mock<Uri>()
        whenever(cardsInteractor.getArtworkUri(bitmap)).thenReturn(Single.just(uri))

        underTest.shareImage(bitmap)

        verify(view).shareUri(uri)
        verify(cardsInteractor).getArtworkUri(bitmap)
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    @Test
    fun `share bitmap should should show error if get artwork uri fails`() {
        prepareAfterInit()
        val bitmap = mock<Bitmap>()
        val throwable = mock<Throwable>()
        whenever(throwable.localizedMessage).thenReturn("message")
        whenever(cardsInteractor.getArtworkUri(bitmap)).thenReturn(Single.error(throwable))

        underTest.shareImage(bitmap)

        verify(view).showError("message")
        verify(cardsInteractor).getArtworkUri(bitmap)
        verifyNoMoreInteractions(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }

    private fun prepareAfterInit() {
        whenever(cardsInteractor.loadIdFav()).thenReturn(Observable.just(idFavs))
        whenever(intent.hasExtra(KEY_SET)).thenReturn(true)
        whenever(intent.getParcelableExtra<MTGSet>(KEY_SET)).thenReturn(set)
        whenever(intent.getIntExtra(POSITION, 0)).thenReturn(5)
        whenever(cardsPreferences.showImage()).thenReturn(true)
        whenever(cardsInteractor.loadSet(set)).thenReturn(Observable.just(cards))
        whenever(set.name).thenReturn("Set name")
        underTest.init(view, intent)
        Mockito.reset(view, cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences)
    }
}
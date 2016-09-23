package com.dbottillo.mtgsearchfree.model.storage;

import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import com.dbottillo.mtgsearchfree.exceptions.MTGException;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.database.BaseDatabaseTest;
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource;
import com.dbottillo.mtgsearchfree.util.FileLoader;
import com.dbottillo.mtgsearchfree.util.FileUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class DecksStorageIntegrationTest extends BaseDatabaseTest {

    DecksStorage storage;

    @Before
    public void setup() throws FileNotFoundException {
        FileUtil fileUtil = new FileUtil(new FileLoaderLocal());
        storage = new DecksStorage(fileUtil, cardsInfoDbHelper, new MTGCardDataSource(mtgDatabaseHelper));
    }

    @Test
    public void DecksStorage_willImportEldraziDeck() throws MTGException {
        List<Deck> decks = storage.importDeck(Uri.parse("assets/Eldrazi.dec"));
        assertTrue(decks.size() > 0);
        Deck deck = decks.get(0);
        assertThat(deck.getName(), is("NAME: "));
        assertThat(deck.getNumberOfCards(), is(60));
        assertThat(deck.getSizeOfSideboard(), is(15));
        List<MTGCard> cards = storage.loadDeck(deck);
        assertCardInDeck(cards, "Chalice of the Void", 4);
        assertCardInDeck(cards, "Ancient Tomb", 4);
        assertCardInDeck(cards, "City of Traitors", 2);
        assertCardInDeck(cards, "Eldrazi Temple", 4);
        assertCardInDeck(cards, "Eye of Ugin", 3);
        assertCardInDeck(cards, "Urborg, Tomb of Yawgmoth", 2);
        assertCardInDeck(cards, "Cavern of Souls", 4);
        assertCardInDeck(cards, "Reality Smasher", 4);
        assertCardInDeck(cards, "Thought-Knot Seer", 4);
        assertCardInDeck(cards, "Endless One", 4);
        assertCardInDeck(cards, "Eldrazi Mimic", 4);
        assertCardInDeck(cards, "Matter Reshaper", 4);
        assertCardInDeck(cards, "Thorn of Amethyst", 3);
        assertCardInDeck(cards, "Warping Wail", 2);
        assertCardInDeck(cards, "Dismember", 1);
        assertCardInDeck(cards, "Umezawa's Jitte", 2);
        assertCardInDeck(cards, "Endbringer", 2);
        assertCardInDeck(cards, "Wasteland", 4);
        assertCardInDeck(cards, "Crystal Vein", 1);
        assertCardInDeck(cards, "Phyrexian Metamorph", 2);

        assertCardInSideboardDeck(cards, "Warping Wail", 1);
        assertCardInSideboardDeck(cards, "Sphere of Resistance", 2);
        assertCardInSideboardDeck(cards, "Leyline of the Void", 4);
        assertCardInSideboardDeck(cards, "Helm of Obedience", 2);
        assertCardInSideboardDeck(cards, "Ulamog, the Ceaseless Hunger", 1);
        assertCardInSideboardDeck(cards, "All Is Dust", 2);
        assertCardInSideboardDeck(cards, "Ratchet Bomb", 3);
    }

    @Test
    public void DecksStorage_willImportGBRampDeck() throws MTGException {
        List<Deck> decks = storage.importDeck(Uri.parse("assets/GB_Ramp.dec"));
        assertTrue(decks.size() > 0);
        Deck deck = decks.get(0);
        assertThat(deck.getName(), is("GB Ramp, a Standard deck by CLYDE THE GLIDE DREXLER"));
        assertThat(deck.getNumberOfCards(), is(60));
        assertThat(deck.getSizeOfSideboard(), is(15));
        List<MTGCard> cards = storage.loadDeck(deck);
        assertCardInDeck(cards, "Blisterpod", 4);
        assertCardInDeck(cards, "Catacomb Sifter", 4);
        assertCardInDeck(cards, "Duskwatch Recruiter", 4);
        assertCardInDeck(cards, "Elvish Visionary", 4);
        assertCardInDeck(cards, "Liliana, Heretical Healer", 2);
        assertCardInDeck(cards, "Loam Dryad", 4);
        assertCardInDeck(cards, "Nantuko Husk", 4);
        assertCardInDeck(cards, "Zulaport Cutthroat", 4);
        assertCardInDeck(cards, "Collected Company", 4);
        assertCardInDeck(cards, "Cryptolith Rite", 3);
        assertCardInDeck(cards, "Hissing Quagmire", 4);
        assertCardInDeck(cards, "Forest", 8);
        assertCardInDeck(cards, "Llanowar Wastes", 4);
        assertCardInDeck(cards, "Swamp", 4);
        assertCardInDeck(cards, "Westvale Abbey", 3);

        assertCardInSideboardDeck(cards, "Duress", 3);
        assertCardInSideboardDeck(cards, "Evolutionary Leap", 2);
        assertCardInSideboardDeck(cards, "Fleshbag Marauder", 3);
        assertCardInSideboardDeck(cards, "Minister of Pain", 2);
        assertCardInSideboardDeck(cards, "Nissa, Vastwood Seer", 1);
        assertCardInSideboardDeck(cards, "Pitiless Horde", 2);
        assertCardInSideboardDeck(cards, "Transgress the Mind", 2);
    }

    @Test
    public void DecksStorage_willImportProtourDeck() throws MTGException {
        List<Deck> decks = storage.importDeck(Uri.parse("assets/protour.txt"));
        assertTrue(decks.size() > 0);
        Deck deck = decks.get(0);
        assertThat(deck.getName(), is("protour.txt"));
        assertThat(deck.getNumberOfCards(), is(60));
        assertThat(deck.getSizeOfSideboard(), is(15));
        List<MTGCard> cards = storage.loadDeck(deck);
        assertCardInDeck(cards, "Elspeth, Sun's Champion", 2);
        assertCardInDeck(cards, "Obzedat, Ghost Council", 1);
        assertCardInDeck(cards, "Desecration Demon", 3);
        assertCardInDeck(cards, "Pack Rat", 4);
        assertCardInDeck(cards, "Lifebane Zombie", 3);
        assertCardInDeck(cards, "Blood Baron of Vizkopa", 3);
        assertCardInDeck(cards, "Thoughtseize", 4);
        assertCardInDeck(cards, "Devour Flesh", 2);
        assertCardInDeck(cards, "Bile Blight", 3);
        assertCardInDeck(cards, "Ultimate Price", 1);
        assertCardInDeck(cards, "Hero's Downfall", 3);
        assertCardInDeck(cards, "Underworld Connections", 3);
        assertCardInDeck(cards, "Banishing Light", 2);
        assertCardInDeck(cards, "Swamp", 8);
        assertCardInDeck(cards, "Plains", 1);
        assertCardInDeck(cards, "Godless Shrine", 4);
        assertCardInDeck(cards, "Temple of Silence", 4);
        assertCardInDeck(cards, "Urborg, Tomb of Yawgmoth", 1);
        assertCardInDeck(cards, "Mutavault", 4);
        assertCardInDeck(cards, "Caves of Koilos", 4);
        assertCardInSideboardDeck(cards, "Underworld Connections", 1);
        assertCardInSideboardDeck(cards, "Duress", 3);
        assertCardInSideboardDeck(cards, "Erebos, God of the Dead", 1);
        assertCardInSideboardDeck(cards, "Sin Collector", 1);
        assertCardInSideboardDeck(cards, "Drown in Sorrow", 2);
        assertCardInSideboardDeck(cards, "Last Breath", 2);
        assertCardInSideboardDeck(cards, "Doom Blade", 3);
        assertCardInSideboardDeck(cards, "Deicide", 2);
    }

    private static MTGCard getCardFromDeck(List<MTGCard> cards, String name, boolean side) {
        for (MTGCard card : cards) {
            if (card.getName().contains(name) && card.isSideboard() == side) {
                return card;
            }
        }
        return null;
    }

    private static void assertCardInDeck(List<MTGCard> cards, String name, int quantity) {
        MTGCard card = getCardFromDeck(cards, name, false);
        assertNotNull(card);
        assertThat(card.getQuantity(), is(quantity));
        assertFalse(card.isSideboard());
    }

    private static void assertCardInSideboardDeck(List<MTGCard> cards, String name, int quantity) {
        MTGCard card = getCardFromDeck(cards, name, true);
        assertNotNull(card);
        assertThat(card.getQuantity(), is(quantity));
        assertTrue(card.isSideboard());
    }

    private class FileLoaderLocal implements FileLoader {
        @Override
        public InputStream loadUri(Uri uri) throws FileNotFoundException {
            return getClass().getClassLoader().getResourceAsStream(uri.toString());
        }
    }

}
package com.dbottillo.mtgsearchfree.model.storage;

import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.database.BaseDatabaseTest;
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource;
import com.dbottillo.mtgsearchfree.util.FileUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DecksStorageIntegrationTest extends BaseDatabaseTest {

    DecksStorage storage;

    @Before
    public void setup() {
        FileUtil fileUtil = new FileUtil(context);
        storage = new DecksStorage(fileUtil, cardsInfoDbHelper, new MTGCardDataSource(mtgDatabaseHelper));
    }

    @Test
    public void testasfs(){
        Uri uri = Uri.parse("android.resource://com.dbottillo.mtgsearchfree/raw/deck.dec");
        List<Deck> decks = storage.importDeck(uri);
        assertTrue(decks.size() > 0);
    }

}
package com.dbottillo.mtgsearchfree.model;

import android.os.Parcel;
import android.test.suitebuilder.annotation.SmallTest;

import com.dbottillo.mtgsearchfree.BaseTest;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@SmallTest
public class DeckTest extends BaseTest {

    Deck deck;

    @Before
    public void setup() {
        deck = new Deck(100);
        deck.setArchived(false);
        deck.setName("Standard");
        deck.setNumberOfCards(20);
        deck.setSizeOfSideboard(10);
    }

    @Test
    public void deck_ParcelableWriteRead() {
        Parcel parcel = Parcel.obtain();
        deck.writeToParcel(parcel, deck.describeContents());
        parcel.setDataPosition(0);

        Deck createdFromParcel = Deck.CREATOR.createFromParcel(parcel);
        assertThat(createdFromParcel, is(deck));
    }

}
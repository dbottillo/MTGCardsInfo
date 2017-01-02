package com.dbottillo.mtgsearchfree.model;

import android.os.Parcel;

import com.dbottillo.mtgsearchfree.BaseContextTest;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CardFilterTest extends BaseContextTest {

    private CardFilter cardFilter;

    @Before
    public void setup() {
        cardFilter = new CardFilter();

        cardFilter.white = true;
        cardFilter.blue = false;
        cardFilter.black = true;
        cardFilter.red = false;
        cardFilter.green = true;

        cardFilter.artifact = true;
        cardFilter.eldrazi = false;
        cardFilter.land = true;

        cardFilter.common = true;
        cardFilter.uncommon = false;
        cardFilter.rare = false;
        cardFilter.mythic = true;
    }


    @Test
    public void cardFilter_ParcelableWriteRead() {
        Parcel parcel = Parcel.obtain();
        cardFilter.writeToParcel(parcel, cardFilter.describeContents());
        parcel.setDataPosition(0);

        CardFilter createdFromParcel = CardFilter.CREATOR.createFromParcel(parcel);
        assertThat(createdFromParcel, is(cardFilter));
    }

}
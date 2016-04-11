package com.dbottillo.mtgsearchfree.model;

import android.os.Parcel;
import android.test.suitebuilder.annotation.SmallTest;

import com.dbottillo.mtgsearchfree.BaseTest;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@SmallTest
public class CardFilterTest extends BaseTest {

    CardFilter cardFilter;

    @Before
    public void setup(){
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
package com.dbottillo.mtgsearchfree.model;

import android.os.Parcel;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TCGPriceTest {

    private TCGPrice price;

    @Before
    public void setup() {
        price = new TCGPrice();
        price.setAvgPrice("avg");
        price.setHiPrice("hi");
        price.setLowprice("low");
        price.setLink("link");
        price.setError("error");
        price.setNotFound(false);
    }

    @Test
    public void tcgPrice_ParcelableWriteRead() {
        Parcel parcel = Parcel.obtain();
        price.writeToParcel(parcel, price.describeContents());
        parcel.setDataPosition(0);

        TCGPrice createdFromParcel = TCGPrice.CREATOR.createFromParcel(parcel);
        assertThat(createdFromParcel, is(price));
    }
}
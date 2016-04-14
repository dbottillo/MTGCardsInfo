package com.dbottillo.mtgsearchfree.model;

import android.os.Parcel;
import android.test.suitebuilder.annotation.SmallTest;

import com.dbottillo.mtgsearchfree.BaseTest;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@SmallTest
public class TCGPriceTest extends BaseTest {

    TCGPrice price;

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
    public void tcgPirce_ParcelableWriteRead() {
        Parcel parcel = Parcel.obtain();
        price.writeToParcel(parcel, price.describeContents());
        parcel.setDataPosition(0);

        TCGPrice createdFromParcel = TCGPrice.CREATOR.createFromParcel(parcel);
        assertThat(createdFromParcel, is(price));
    }
}
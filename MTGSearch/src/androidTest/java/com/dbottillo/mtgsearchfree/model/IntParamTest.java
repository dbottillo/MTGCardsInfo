package com.dbottillo.mtgsearchfree.model;

import android.os.Parcel;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IntParamTest {

    private IntParam param;

    @Before
    public void setup() {
        param = new IntParam("+", 100);
    }

    @Test
    public void deck_ParcelableWriteRead() {
        Parcel parcel = Parcel.obtain();
        param.writeToParcel(parcel, param.describeContents());
        parcel.setDataPosition(0);

        IntParam createdFromParcel = IntParam.CREATOR.createFromParcel(parcel);
        assertThat(createdFromParcel, is(param));
    }
}
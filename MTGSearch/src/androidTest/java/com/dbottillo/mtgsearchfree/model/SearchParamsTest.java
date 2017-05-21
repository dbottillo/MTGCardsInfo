package com.dbottillo.mtgsearchfree.model;

import android.os.Parcel;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SearchParamsTest {

    private SearchParams searchParams;

    @Before
    public void setup() {
        searchParams = new SearchParams();
        searchParams.setName("jayce");
        searchParams.setTypes("creatures");
        searchParams.setText("text");
        searchParams.setCmc(new IntParam("=", 4));
        searchParams.setPower(new IntParam(">", 3));
        searchParams.setTough(new IntParam("<", 1));
        searchParams.setWhite(false);
        searchParams.setBlack(false);
        searchParams.setBlue(true);
        searchParams.setRed(true);
        searchParams.setGreen(false);
        searchParams.setOnlyMulti(true);
        searchParams.setCommon(true);
        searchParams.setUncommon(true);
        searchParams.setRare(false);
        searchParams.setMythic(false);
        searchParams.setSetId(3);
    }

    @Test
    public void searchParams_ParcelableWriteRead() {
        Parcel parcel = Parcel.obtain();
        searchParams.writeToParcel(parcel, searchParams.describeContents());
        parcel.setDataPosition(0);

        SearchParams createdFromParcel = SearchParams.CREATOR.createFromParcel(parcel);
        assertThat(createdFromParcel, is(searchParams));
    }

    @Test
    public void searchParams_willDetectOneColor() {
        assertTrue(searchParams.atLeastOneColor());
        searchParams.setBlue(false);
        searchParams.setRed(false);
        assertFalse(searchParams.atLeastOneColor());
    }

    @Test
    public void searchParams_willDetectOneRarity() {
        assertTrue(searchParams.atLeastOneRarity());
        searchParams.setCommon(false);
        searchParams.setUncommon(false);
        assertFalse(searchParams.atLeastOneRarity());
    }

    @Test
    public void searchParams_validation() {
        SearchParams searchParams = new SearchParams();
        assertFalse(searchParams.isValid());

        searchParams = new SearchParams();
        searchParams.setText("text");
        assertTrue(searchParams.isValid());

        searchParams = new SearchParams();
        searchParams.setTypes("types");
        assertTrue(searchParams.isValid());

        searchParams = new SearchParams();
        searchParams.setName("search");
        assertTrue(searchParams.isValid());

        searchParams = new SearchParams();
        searchParams.setCmc(new IntParam("=", 4));
        assertTrue(searchParams.isValid());

        searchParams = new SearchParams();
        searchParams.setPower(new IntParam("=", 4));
        assertTrue(searchParams.isValid());

        searchParams = new SearchParams();
        searchParams.setTough(new IntParam("=", 4));
        assertTrue(searchParams.isValid());

        searchParams = new SearchParams();
        searchParams.setGreen(true);
        assertTrue(searchParams.isValid());

        searchParams = new SearchParams();
        searchParams.setRare(true);
        assertTrue(searchParams.isValid());

        searchParams = new SearchParams();
        searchParams.setSetId(2);
        assertTrue(searchParams.isValid());
    }

    @Test
    public void isNotValid() {
        SearchParams searchParams = new SearchParams();
        assertThat(searchParams.isValid(), is(false));

        searchParams.setPower(null);
        assertThat(searchParams.isValid(), is(false));
    }

    @Test
    public void searchParams_willHandleMulticolorOption() {
        SearchParams searchParams = new SearchParams();
        assertFalse(searchParams.isNoMulti());
        assertFalse(searchParams.onlyMulti());
        searchParams.setNoMulti(true);
        assertTrue(searchParams.isNoMulti());
        assertFalse(searchParams.onlyMulti());
        searchParams.setOnlyMulti(true);
        assertFalse(searchParams.isNoMulti());
        assertTrue(searchParams.onlyMulti());
    }

    @Test
    public void returnValidWithOnlyLandSet() {
        SearchParams searchParams = new SearchParams();
        searchParams.setLand(true);
        assertTrue(searchParams.isValid());
    }
}
package com.dbottillo.mtgsearchfree.model.storage;

import com.dbottillo.mtgsearchfree.util.BaseContextTest;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class CardsPreferencesImplTest extends BaseContextTest{

    private CardsPreferencesImpl underTest;

    @Before
    public void setup(){
        underTest = new CardsPreferencesImpl(context);
        underTest.clear();
    }

    @Test
    public void retainsSetPosition(){
        assertThat(underTest.getSetPosition(), is(0));
        underTest.saveSetPosition(2);
        assertThat(underTest.getSetPosition(), is(2));
    }

    @Test
    public void retainsShowImage(){
        assertTrue(underTest.showImage());
        underTest.setShowImage(false);
        assertFalse(underTest.showImage());
    }

}
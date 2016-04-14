package com.dbottillo.mtgsearchfree.model;

import android.test.suitebuilder.annotation.SmallTest;

import com.dbottillo.mtgsearchfree.BaseTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@SmallTest
public class CardPropertiesTest extends BaseTest{

    public void CardProperties_areCorrect(){
        assertThat(CardProperties.COLOR.getNumberFromString("Blue"), is(1));
    }

}
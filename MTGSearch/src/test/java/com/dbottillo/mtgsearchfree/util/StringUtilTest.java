package com.dbottillo.mtgsearchfree.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class StringUtilTest {

    @Test
    public void checkIfStringContainsAnother(){
        assertTrue(StringUtil.contains("bla jaycebla", "jayce"));
        assertTrue(StringUtil.contains("bla jaycebla", "bla"));
        assertTrue(StringUtil.contains("bla jaycebla", "cebl"));
        assertTrue(StringUtil.contains("bla jaycebla", "bl"));

        assertFalse(StringUtil.contains("bla jaycebla", ""));
        assertFalse(StringUtil.contains("bla jaycebla", null));
        assertFalse(StringUtil.contains("bla jaycebla", "garruck"));
        assertFalse(StringUtil.contains("", "garruck"));
        assertFalse(StringUtil.contains(null, "garruck"));
    }

    @Test
    public void joinListOfColors(){
        List integerList = Arrays.asList(new Integer[]{0,1,2});
        assertThat(StringUtil.joinListOfColors(integerList, ","), is("White,Blue,Black"));
        integerList = Arrays.asList(new Integer[]{0,1,2,3,4});
        assertThat(StringUtil.joinListOfColors(integerList, ","), is("White,Blue,Black,Red,Green"));
    }

    @Test
    public void joinListOfStrings(){
        List integerList = Arrays.asList(new String[]{"Uno", "Due", "Tre"});
        assertThat(StringUtil.joinListOfStrings(integerList, "*"), is("Uno*Due*Tre"));
    }
}
package com.dbottillo.mtgsearchfree.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayUtilsTest {

    @Test
    public void returnFalseIfArrayIsEmptyOrNull(){
        int[] array = new int[0];
        assertFalse(ArrayUtils.contains(array, 5));
        assertFalse(ArrayUtils.contains(null, 5));
    }

    @Test
    public void returnFalseIfIntegerIsNotInTheArray(){
        int[] array = new int[]{1,2,3};
        assertFalse(ArrayUtils.contains(array, 5));
    }

    @Test
    public void returnTrueIfIntegerIsInTheArray(){
        int[] array = new int[]{1,2,3};
        assertTrue(ArrayUtils.contains(array, 2));
    }

}
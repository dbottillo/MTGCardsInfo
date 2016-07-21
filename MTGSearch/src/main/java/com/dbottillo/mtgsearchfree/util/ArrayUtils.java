package com.dbottillo.mtgsearchfree.util;

public class ArrayUtils {

    public static boolean contains(int[] array, int value){
        if (array == null || array.length == 0){
            return false;
        }
        for (int element : array){
            if (element == value){
                return true;
            }
        }
        return false;
    }
}

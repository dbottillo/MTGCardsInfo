package com.dbottillo.util;

import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

public class InputUtil {

    private InputUtil() {

    }

    public static boolean hideKeyboard(Context activity, IBinder token) {
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}

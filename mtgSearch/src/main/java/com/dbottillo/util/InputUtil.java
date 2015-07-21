package com.dbottillo.util;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

public class InputUtil {

    public static void hideKeyboard(Context activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(((Activity) activity).getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }

    public static void hideKeyboard(Context activity, IBinder token) {
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }


}

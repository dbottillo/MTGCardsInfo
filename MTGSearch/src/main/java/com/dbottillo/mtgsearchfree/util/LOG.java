package com.dbottillo.mtgsearchfree.util;

import android.app.Instrumentation;
import android.text.TextUtils;
import android.util.Log;

import com.dbottillo.mtgsearchfree.BuildConfig;
import com.dbottillo.mtgsearchfree.view.activities.BasicActivity;
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class LOG {

    private LOG() {

    }

    private static final String TAG = "MTG";

    private static final StackTraceElement NOT_FOUND = new StackTraceElement("", "", "", 0);

    private static StackTraceElement determineCaller() {
        for (final StackTraceElement element : new RuntimeException().getStackTrace()) {
            if (element.getClassName().equals(LOG.class.getName())) {
                continue;
            }
            if (element.getClassName().equals(BasicActivity.class.getName())) {
                continue;
            }
            if (element.getClassName().equals(BasicFragment.class.getName())) {
                continue;
            }
            if (element.getClassName().equals(Instrumentation.class.getName())) {
                continue;
            }
            return element;
        }
        return NOT_FOUND;
    }

    private static String getClassNameOnly(final String classNameWithPackage) {
        final int lastDotPos = classNameWithPackage.lastIndexOf('.');
        if (lastDotPos == -1) {
            return classNameWithPackage;
        }
        return classNameWithPackage.substring(lastDotPos + 1);
    }

    private static String enhanced(final String message) {
        if (!BuildConfig.DEBUG) {
            return message;
        }
        final StackTraceElement caller = determineCaller();
        final String classNameOnly = getClassNameOnly(caller.getClassName());
        final String methodName = caller.getMethodName();
        final int lineNumber = caller.getLineNumber();
        if (BuildConfig.LOG_THREAD) {
            final Thread thread = Thread.currentThread();
            return String.format("%s [%s:%s:%s] %s", message, classNameOnly, methodName, lineNumber, thread);
        }
        if (message == null || TextUtils.isEmpty(message)) {
            return String.format("=== %s:%s:%s", classNameOnly, methodName, lineNumber);
        }
        return String.format("=== %s:%s:%s ->  %s", classNameOnly, methodName, lineNumber, message);
    }

    public static void d(String msg) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        Log.d(TAG, enhanced(msg));
    }

    public static void d() {
        d("");
    }

    public static void e(String message) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, enhanced(message));
        }
    }

    public static void e(Exception e) {
        e(e.getMessage());
    }

    public static void query(String query, String... params) {
        if (BuildConfig.DEBUG) {
            String message = query;
            if (params.length > 0) {
                message += " with param: ";
            }
            for (String param : params) {
                message += param + " ";
            }
            d(message);
        }
    }

    public static void dump(Object o) {
        if (BuildConfig.DEBUG) {
            try {
                if (o == null) {
                    d("Object is null");
                } else {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    d("" + gson.toJson(o));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

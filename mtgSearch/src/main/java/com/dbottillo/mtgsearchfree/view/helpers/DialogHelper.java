package com.dbottillo.mtgsearchfree.view.helpers;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.dbottillo.mtgsearchfree.util.LOG;

public class DialogHelper {

    public static void open(AppCompatActivity activity, String tag, DialogFragment fragment) {
        LOG.d();
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        Fragment prev = activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        fragment.show(ft, tag);
    }
}

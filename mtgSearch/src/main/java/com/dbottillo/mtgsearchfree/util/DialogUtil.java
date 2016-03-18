package com.dbottillo.mtgsearchfree.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.helper.TrackingHelper;

public final class DialogUtil {

    private DialogUtil() {

    }

    public interface SortDialogListener {
        void onSortSelected();
    }

    public static void chooseSortDialog(final Context context, final SharedPreferences sharedPreferences, final SortDialogListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.pick_sort_option)
                .setItems(R.array.sort_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(BasicFragment.Companion.getPREF_SORT_WUBRG(), which == 1);
                        editor.apply();
                        listener.onSortSelected();
                        TrackingHelper.getInstance(context.getApplicationContext()).trackEvent(TrackingHelper.UA_CATEGORY_SET, TrackingHelper.UA_ACTION_TOGGLE,
                                which == 1 ? "wubrg" : "alphabetically");
                    }
                });
        builder.create().show();
    }
}

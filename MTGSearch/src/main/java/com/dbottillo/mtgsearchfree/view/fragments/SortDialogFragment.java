package com.dbottillo.mtgsearchfree.view.fragments;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.util.TrackingManager;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SortDialogFragment extends BottomSheetDialogFragment {

    @Bind(R.id.sort_option_container)
    RadioGroup azContainer;
    @Bind(R.id.sort_option_az)
    AppCompatRadioButton azOption;
    @Bind(R.id.sort_option_color)
    AppCompatRadioButton colorOption;

    SortDialogListener listener;

    SharedPreferences sharedPreferences;

    private boolean colorSelected;

    public interface SortDialogListener {
        void onSortSelected();
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    public void setListener(SortDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.dialog_sort, null);
        ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        sharedPreferences = getActivity().getSharedPreferences(MTGApp.PREFS_NAME, 0);
        colorSelected = sharedPreferences.getBoolean(BasicFragment.PREF_SORT_WUBRG, true);
        syncUI();

        azContainer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (colorSelected && checkedId == R.id.sort_option_az){
                    colorSelected = false;
                    syncUI();
                } else if (!colorSelected && checkedId == R.id.sort_option_color){
                    colorSelected = true;
                    syncUI();
                }
            }
        });
    }

    private void syncUI() {
        azOption.setChecked(!colorSelected);
        colorOption.setChecked(colorSelected);
        optionUpdate(colorSelected);
    }

    private void optionUpdate(boolean color) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(BasicFragment.PREF_SORT_WUBRG, color);
        editor.apply();
        listener.onSortSelected();
        TrackingManager.trackSortCard(color);
    }

}

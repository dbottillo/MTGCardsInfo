package com.dbottillo.mtgsearchfree.view.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.widget.RadioGroup;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences;
import com.dbottillo.mtgsearchfree.util.TrackingManager;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

@Deprecated
public class SortDialogFragment extends BottomSheetDialogFragment {

    @BindView(R.id.sort_option_container)
    RadioGroup azContainer;
    @BindView(R.id.sort_option_az)
    AppCompatRadioButton azOption;
    @BindView(R.id.sort_option_color)
    AppCompatRadioButton colorOption;

    private SortDialogListener listener;
    private boolean colorSelected;

    @Inject
    CardsPreferences cardsPreferences;

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
    @SuppressLint("RestrictedApi")
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.dialog_sort, null);
        ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);

        ((MTGApp) getActivity().getApplication()).getUiGraph().inject(this);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        //colorSelected = cardsPreferences.isSortWUBRG();
        syncUI();

        azContainer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (colorSelected && checkedId == R.id.sort_option_az) {
                    colorSelected = false;
                    syncUI();
                } else if (!colorSelected && checkedId == R.id.sort_option_color) {
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
       // cardsPreferences.setSortOption(color);
        listener.onSortSelected();
        TrackingManager.trackSortCard(color);
    }

}

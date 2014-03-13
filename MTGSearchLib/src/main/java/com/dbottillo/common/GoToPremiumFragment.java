package com.dbottillo.common;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.dbottillo.base.DBFragment;
import com.dbottillo.mtgsearch.R;

/**
 * Created by danielebottillo on 02/03/2014.
 */
public class GoToPremiumFragment extends DBFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_to_premium, container, false);

        Button openPlayStore = (Button) v.findViewById(R.id.open_play_store);
        openPlayStore.setOnClickListener(this);

        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onClick(View v) {
       openPlayStore();
    }

    @Override
    public String getPageTrack() {
        return "/premium";
    }
}

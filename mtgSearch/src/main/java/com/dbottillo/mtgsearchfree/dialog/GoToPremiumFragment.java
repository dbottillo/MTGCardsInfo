package com.dbottillo.mtgsearchfree.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment;

public class GoToPremiumFragment extends BasicFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_to_premium, container, false);

        Button openPlayStore = (Button) v.findViewById(R.id.open_play_store);
        openPlayStore.setOnClickListener(this);

        TextView title = (TextView) v.findViewById(R.id.title_dialog);
        title.setText(getString(R.string.need_premium));

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

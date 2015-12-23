package com.dbottillo.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dbottillo.R;
import com.dbottillo.base.DBFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReleaseNoteFragment extends DBFragment {

    @Bind(R.id.release_note)
    TextView top;

    @Bind(R.id.release_note_full)
    TextView bottom;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_release_note, container, false);

        ButterKnife.bind(this, v);

        setActionBarTitle(getString(R.string.action_release_note));

        top.setText(getText(R.string.release_note_text));
        bottom.setText(getText(R.string.release_note_text_full));

        return v;
    }

    @Override
    public String getPageTrack() {
        return "/release-note";
    }
}

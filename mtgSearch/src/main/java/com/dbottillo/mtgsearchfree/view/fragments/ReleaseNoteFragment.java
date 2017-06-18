package com.dbottillo.mtgsearchfree.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.ui.BasicFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReleaseNoteFragment extends BasicFragment {

    @BindView(R.id.release_note)
    TextView releaseNote;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_release_note, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setActionBarTitle(getString(R.string.action_release_note));
        releaseNote.setText(getText(R.string.release_note_text_full));
    }

    public String getPageTrack() {
        return "/release-note";
    }

}


package com.dbottillo.mtgsearchfree.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.component.AppComponent;
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment;

import org.jetbrains.annotations.NotNull;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReleaseNoteFragment extends BasicFragment {

    @Bind(R.id.release_note)
    TextView releaseNote;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_release_note, container, false);

        ButterKnife.bind(this, v);

        setActionBarTitle(getString(R.string.action_release_note));

        releaseNote.setText(getText(R.string.release_note_text_full));

        return v;
    }

    @Override
    public String getPageTrack() {
        return "/release-note";
    }

    @Override
    public void setupComponent(@NotNull AppComponent appComponent) {

    }
}

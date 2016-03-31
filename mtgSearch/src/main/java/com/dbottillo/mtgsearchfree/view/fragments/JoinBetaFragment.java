package com.dbottillo.mtgsearchfree.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.storage.GeneralPreferences;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class JoinBetaFragment extends BasicFragment implements View.OnClickListener, View.OnTouchListener {

    String[] librariesName = new String[]{"Smooth Progress Bar", "Picasso", "LeakMemory"};
    String[] librariesAuthor = new String[]{"Castorflex", "Square", "Square"};
    String[] librariesLink = new String[]{"https://github.com/castorflex/SmoothProgressBar", "http://square.github.io/picasso/", "https://github.com/square/leakcanary"};

    String versionName;

    @Bind(R.id.share_app)
    View shareApp;

    @Bind(R.id.send_feedback)
    Button sendFeedback;

    @Bind(R.id.about_version)
    TextView version;

    @Bind(R.id.copyright)
    TextView copyright;

    @Bind(R.id.libraries_container)
    LinearLayout cardContainer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);

        ButterKnife.bind(this, v);

        setActionBarTitle(getString(R.string.action_about));

        return v;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public String getPageTrack() {
        return "/about";
    }

    private long firstTap;

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                firstTap = Calendar.getInstance().getTimeInMillis();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                long diff = Calendar.getInstance().getTimeInMillis() - firstTap;
                long seconds = diff / 1000;
                if (seconds < 5) {
                    version.setOnTouchListener(null);
                    GeneralPreferences.with(getActivity().getApplicationContext()).setDebug();
                    Toast.makeText(getActivity(), R.string.debug_mode_active, Toast.LENGTH_LONG).show();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            default:
                break;
        }
        return true;
    }
}

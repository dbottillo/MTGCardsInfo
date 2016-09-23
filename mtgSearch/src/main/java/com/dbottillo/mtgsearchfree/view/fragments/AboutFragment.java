package com.dbottillo.mtgsearchfree.view.fragments;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
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
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.TrackingManager;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.net.Uri.parse;

public class AboutFragment extends BasicFragment implements View.OnClickListener, View.OnTouchListener {

    String[] librariesName = new String[]{"Smooth Progress Bar", "Picasso", "LeakMemory"};
    String[] librariesAuthor = new String[]{"Castorflex", "Square", "Square"};
    String[] librariesLink = new String[]{"https://github.com/castorflex/SmoothProgressBar", "http://square.github.io/picasso/", "https://github.com/square/leakcanary"};

    String versionName;

    @BindView(R.id.share_app)
    View shareApp;

    @BindView(R.id.send_feedback)
    Button sendFeedback;

    @BindView(R.id.about_version)
    TextView version;

    @BindView(R.id.copyright)
    TextView copyright;

    @BindView(R.id.libraries_container)
    LinearLayout cardContainer;

    private long firstTap;

    @Inject
    GeneralPreferences generalPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);

        setActionBarTitle(getString(R.string.action_about));

        ButterKnife.bind(this, v);
        version.setOnTouchListener(this);

        getMTGApp().getUiGraph().inject(this);

        versionName = "";
        try {
            versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            TextView version = (TextView) v.findViewById(R.id.about_version);
            version.setText(Html.fromHtml("<b>" + getString(R.string.version) + "</b>: " + versionName));
        } catch (PackageManager.NameNotFoundException e) {
            LOG.e(e);
        }

        sendFeedback.setOnClickListener(this);

        shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackingManager.trackShareApp();
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                String url = "https://play.google.com/store/apps/details?id=com.dbottillo.mtgsearchfree";
                i.putExtra(Intent.EXTRA_TEXT, url);
                startActivity(Intent.createChooser(i, getString(R.string.share)));
            }
        });

        for (int i = 0; i < librariesName.length; i++) {
            View libraryView = View.inflate(getContext(), R.layout.row_library, null);
            TextView title = (TextView) libraryView.findViewById(R.id.library_name);
            title.setText(librariesName[i]);
            TextView author = (TextView) libraryView.findViewById(R.id.library_author);
            author.setText(librariesAuthor[i]);
            cardContainer.addView(libraryView);
            libraryView.setTag(0);
            libraryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (int) v.getTag();
                    Uri uri = parse(librariesLink[tag]);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    startActivity(intent);
                    TrackingManager.trackAboutLibrary(librariesLink[0]);
                }
            });
        }

        copyright.setText(getString(R.string.copyright));
        return v;
    }

    @Override
    public void onClick(View v) {
        LOG.d();
        TrackingManager.trackOpenFeedback();
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getActivity().getString(R.string.email), null));
        String text = String.format(getString(R.string.feedback_text), versionName,
                String.valueOf(Build.VERSION.SDK_INT), Build.DEVICE, Build.MODEL, Build.PRODUCT);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback) + " " + getActivity().getString(R.string.app_name));
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(text));
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_feedback)));
    }

    @Override
    public String getPageTrack() {
        return "/about";
    }

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
                    generalPreferences.setDebug();
                    Toast.makeText(getActivity(), R.string.debug_mode_active, Toast.LENGTH_LONG).show();
                }
                v.performClick();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            default:
                break;
        }
        return true;
    }
}

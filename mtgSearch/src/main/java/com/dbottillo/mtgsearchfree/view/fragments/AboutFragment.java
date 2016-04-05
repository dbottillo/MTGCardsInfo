package com.dbottillo.mtgsearchfree.view.fragments;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.TrackingManager;

import static android.net.Uri.parse;

public class AboutFragment extends BasicFragment implements View.OnClickListener {

    String[] librariesName = new String[]{"Smooth Progress Bar", "Picasso", "LeakMemory"};
    String[] librariesAuthor = new String[]{"Castorflex", "Square", "Square"};
    String[] librariesLink = new String[]{"https://github.com/castorflex/SmoothProgressBar", "http://square.github.io/picasso/", "https://github.com/square/leakcanary"};

    String versionName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);

        setActionBarTitle(getString(R.string.action_about));

        versionName = "";
        try {
            versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            TextView version = (TextView) v.findViewById(R.id.about_version);
            version.setText(Html.fromHtml("<b>" + getString(R.string.version) + "</b>: " + versionName));
        } catch (PackageManager.NameNotFoundException e) {
            LOG.e(e);
        }

        Button sendFeedback = (Button) v.findViewById(R.id.send_feedback);
        sendFeedback.setOnClickListener(this);

        v.findViewById(R.id.share_app).setOnClickListener(new View.OnClickListener() {
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

        LinearLayout cardContainer = (LinearLayout) v.findViewById(R.id.libraries_container);
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

        TextView copyright = (TextView) v.findViewById(R.id.copyright);
        copyright.setText(getString(R.string.copyright));
        return v;
    }

    @Override
    public void onClick(View v) {
        LOG.d();
        TrackingManager.trackOpenFeedback();
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getActivity().getString(R.string.email), null));
        String text = String.format(getString(R.string.feedback_text), versionName,
                Build.VERSION.SDK_INT, Build.DEVICE, Build.MODEL, Build.PRODUCT);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback) + " " + getActivity().getString(R.string.app_name));
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(text));
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_feedback)));
    }

    @Override
    public String getPageTrack() {
        return "/about";
    }
}

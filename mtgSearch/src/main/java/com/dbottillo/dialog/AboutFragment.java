package com.dbottillo.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.dbottillo.R;
import com.dbottillo.base.DBFragment;
import com.dbottillo.helper.TrackingHelper;

public class AboutFragment extends DBFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);

        setActionBarTitle(getString(R.string.action_about));

        try {
            String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            TextView version = (TextView) v.findViewById(R.id.about_version);
            version.setText(Html.fromHtml("<b>" + getString(R.string.version) + "</b>: " + versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Button sendFeedback = (Button) v.findViewById(R.id.send_feedback);
        sendFeedback.setOnClickListener(this);

        TextView copyright = (TextView) v.findViewById(R.id.copyright);
        copyright.setText(getString(R.string.copyright));
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
        TrackingHelper.getInstance(v.getContext()).trackEvent(TrackingHelper.UA_CATEGORY_UI, TrackingHelper.UA_ACTION_OPEN, "feedback");
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getActivity().getString(R.string.email), null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback) + " " + getActivity().getString(R.string.app_name));
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_feedback)));
    }

    @Override
    public String getPageTrack() {
        return "/about";
    }
}

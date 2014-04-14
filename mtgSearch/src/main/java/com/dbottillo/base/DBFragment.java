package com.dbottillo.base;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public abstract class DBFragment extends DialogFragment {

    public static final String PREFS_NAME = "Filter";

    public static final String PREF_SHOW_IMAGE = "show_image";

    private AdView adView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public SharedPreferences getSharedPreferences(){
        return getActivity().getSharedPreferences(PREFS_NAME, 0);
    }

    protected void setActionBarTitle(String title) {
        DBActivity act = (DBActivity) getActivity();
        act.getSupportActionBar().setTitle(title);
    }

    protected void openPlayStore(){
        getApp().trackEvent(MTGApp.UA_CATEGORY_UI, MTGApp.UA_ACTION_OPEN, "play_store");
        String appPackageName = "com.dbottillo.mtgsearch";
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


    protected AdView createAdView(String unitID) {
        adView = new AdView(getActivity());
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(unitID);
        return adView;
    }

    protected AdRequest createAdRequest() {
        AdRequest adRequest = new AdRequest.Builder()
                .setGender(AdRequest.GENDER_MALE)
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("512F56FD659AA867F7F67C7066A85F33")
                .addTestDevice("81CB49F53C9C0C74241CB8BD3383E1C7")
                .build();
        return adRequest;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
        if (getApp() != null) {
            getApp().trackPage(getPageTrack());
        }
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /** Called before the activity is destroyed. */
    @Override
    public void onDestroy() {
        // Destroy the AdView.
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    public abstract String getPageTrack();

    public AdView getAdView(){
        return adView;
    }

    public void trackPage(String page){
        MTGApp app = (MTGApp) getActivity().getApplication();
        app.trackPage(page);
    }

    public MTGApp getApp(){
        if (getActivity() != null){
            return ((DBActivity) getActivity()).getApp();
        }
        return null;
    }

    protected void trackEvent(String category, String action, String label){
        if (getApp() != null){
            getApp().trackEvent(category,action,label);
        }
    }

}

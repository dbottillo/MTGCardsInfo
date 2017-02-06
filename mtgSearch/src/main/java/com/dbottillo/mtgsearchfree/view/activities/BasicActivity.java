package com.dbottillo.mtgsearchfree.view.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.BuildConfig;
import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.model.helper.AddFavouritesAsyncTask;
import com.dbottillo.mtgsearchfree.model.helper.CreateDecksAsyncTask;
import com.dbottillo.mtgsearchfree.model.storage.GeneralData;
import com.dbottillo.mtgsearchfree.util.FileUtil;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.MaterialWrapper;
import com.dbottillo.mtgsearchfree.util.PermissionUtil;
import com.dbottillo.mtgsearchfree.util.TrackingManager;
import com.dbottillo.mtgsearchfree.view.fragments.AboutFragment;
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment;
import com.dbottillo.mtgsearchfree.view.fragments.DecksFragment;
import com.dbottillo.mtgsearchfree.view.fragments.JoinBetaFragment;
import com.dbottillo.mtgsearchfree.view.fragments.LifeCounterFragment;
import com.dbottillo.mtgsearchfree.view.fragments.MainFragment;
import com.dbottillo.mtgsearchfree.view.fragments.SavedFragment;
import com.dbottillo.mtgsearchfree.view.helpers.NavDrawerHelper;

import javax.inject.Inject;

public abstract class BasicActivity extends AppCompatActivity {

    int sizeToolbar = 0;
    Toolbar toolbar = null;
    boolean isPortrait = false;

    @Inject
    public GeneralData generalData;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LOG.d("============================================");

        getMTGApp().getUiGraph().inject(this);

        isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            sizeToolbar = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
    }

    public void onResume() {
        super.onResume();
        LOG.d();
        if (getPageTrack() != null) {
            TrackingManager.trackPage(getPageTrack());
        }
    }

    public abstract String getPageTrack();

    void hideIme() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
        }

        if (getCurrentFocus() != null) {
            getCurrentFocus().clearFocus();
        }
    }

    void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MaterialWrapper.setElevation(toolbar, getResources().getDimensionPixelSize(R.dimen.toolbar_elevation));
    }

    public void changeFragment(BasicFragment fragment, String tag, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commit();
    }

    public void openRateTheApp() {
        String packageName = getPackageName();
        if (BuildConfig.DEBUG) {
            packageName = "com.dbottillo.mtgsearchfree";
        }
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Uri play = Uri.parse("http://play.google.com/store/apps/details?id=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            Intent goToPlay = new Intent(Intent.ACTION_VIEW, play);
            goToPlay.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            startActivity(goToPlay);
        }
        TrackingManager.trackOpenRateApp();
    }

    void openDialog(String tag, DialogFragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        fragment.show(ft, tag);
    }

    private PermissionUtil.PermissionListener permissionListener;

    public void requestPermission(PermissionUtil.TYPE type, PermissionUtil.PermissionListener listener) {
        this.permissionListener = listener;
        if (PermissionUtil.permissionGranted(this, type)) {
            listener.permissionGranted();
            return;
        }
        PermissionUtil.requestPermission(this, type);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtil.isGranted(grantResults)) {
            permissionListener.permissionGranted();
        } else {
            permissionListener.permissionNotGranted();
        }
    }

    public MTGApp getMTGApp() {
        return (MTGApp) getApplication();
    }

    @Override
    public Object getSystemService(String name) {
        if ("Dagger".equals(name)) {
            return getMTGApp().getUiGraph();
        }
        return super.getSystemService(name);
    }

}

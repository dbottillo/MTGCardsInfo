package com.dbottillo.mtgsearchfree.util;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public final class LeakCanaryUtil {

    private LeakCanaryUtil() {

    }

    private static RefWatcher refWatcher;

    public static void install(MTGApp mtgApp) {
        refWatcher = LeakCanary.install(mtgApp);
    }

    public static void watchFragment(BasicFragment basicFragment) {
        refWatcher.watch(basicFragment);
    }

}

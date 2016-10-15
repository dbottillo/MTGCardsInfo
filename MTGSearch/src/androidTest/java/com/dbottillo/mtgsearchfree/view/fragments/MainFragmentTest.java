package com.dbottillo.mtgsearchfree.view.fragments;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.view.activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.dbottillo.mtgsearchfree.matchers.RecyclerViewMatcher.withRecyclerView;
import static com.dbottillo.mtgsearchfree.matchers.ViewMatchers.setWithName;
import static com.dbottillo.mtgsearchfree.matchers.ViewMatchers.withDrawable;

@RunWith(AndroidJUnit4.class)
public class MainFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    private final static String SET_NAME = "Kaladesh";
    private final static String CARD_NAME = "Aerial Responder";

    @Test
    public void willShowKaladeshSet() {
        goToKaladesh();
        onView(withId(R.id.set_chooser_name)).check(matches(withText(SET_NAME)));
        onView(withRecyclerView(R.id.card_list).atPositionOnView(1, R.id.grid_item_card_image))
                .check(matches(withContentDescription(CARD_NAME)));
    }

    @Test
    public void willSwitchToListMode() {
        goToKaladesh();
        onView(withId(R.id.cards_view_type)).check(matches(withDrawable(R.drawable.cards_list_type)));
        onView(withId(R.id.cards_view_type)).perform(click());
        onView(withId(R.id.cards_view_type)).check(matches(withDrawable(R.drawable.cards_grid_type)));
        onView(withRecyclerView(R.id.card_list).atPositionOnView(1, R.id.card_name))
                .check(matches(withText(CARD_NAME)));
    }

    private void waitForIt() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void goToKaladesh() {
        onView(withId(R.id.set_chooser)).perform(click());
        onData(setWithName(SET_NAME))
                .inAdapterView(withId(R.id.set_list))
                .perform(click());
    }
}
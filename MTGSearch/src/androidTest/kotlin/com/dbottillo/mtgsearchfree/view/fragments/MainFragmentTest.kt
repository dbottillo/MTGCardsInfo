package com.dbottillo.mtgsearchfree.view.fragments


class MainFragmentTest/*@Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    private final static String SET_NAME = "Kaladesh";
    private final static String CARD_NAME = "Aerial Responder";

    @Test
    @Ignore
    public void showsKaladeshSet() {
        goToKaladesh();
        onView(withId(R.id.set_chooser_name)).check(matches(withText(SET_NAME)));
        onView(withRecyclerView(R.id.card_list).atPositionOnView(1, R.id.grid_item_card_image))
                .check(matches(withContentDescription(CARD_NAME)));
    }

    @Test
    @Ignore
    public void switchesToListMode() {
        goToKaladesh();
        onView(withId(R.id.cards_view_type)).check(matches(withDrawable(R.drawable.cards_list_type)));
        onView(withId(R.id.cards_view_type)).perform(click());
        onView(withId(R.id.cards_view_type)).check(matches(withDrawable(R.drawable.cards_grid_type)));
        onView(withRecyclerView(R.id.card_list).atPositionOnView(1, R.id.card_name))
                .check(matches(withText(CARD_NAME)));
        onView(withId(R.id.cards_view_type)).perform(click());
    }

    @Test
    @Ignore
    public void switchesToAlphabeticalOrder() {
        goToKaladesh();
        onView(withId(R.id.cards_sort)).perform(click());
        onView(withId(R.id.sort_option_az)).perform(click());
        Espresso.pressBack();
        onView(withRecyclerView(R.id.card_list).atPositionOnView(0, R.id.grid_item_card_image))
                .check(matches(withContentDescription("Accomplished Automaton")));
        onView(withId(R.id.cards_sort)).perform(click());
        onView(withId(R.id.sort_option_color)).perform(click());
    }

    @Test
    @Ignore
    public void retainsSetOnOrientationChange() {
        goToKaladesh();
        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        onView(withId(R.id.set_chooser_name)).check(matches(withText(SET_NAME)));
        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void waitForIt() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void goToKaladesh() {
        onView(withId(R.id.set_chooser)).perform(click());
        onData(setWithName(SET_NAME))
                .inAdapterView(withId(R.id.set_list))
                .perform(click());
    }*/
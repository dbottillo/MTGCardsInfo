package com.dbottillo.mtgsearchfree.matchers;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;

import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

public final class ViewMatchers {

    private ViewMatchers(){

    }

    public static Matcher<Object> setWithName(final String name) {

        return new BoundedMatcher<Object, MTGSet>(MTGSet.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with text: " + name);
            }

            @Override
            protected boolean matchesSafely(MTGSet item) {
                return name.equals(item.getName());
            }

        };
    }

    public static Matcher<Object> cardWithName(final String name) {

        return new BoundedMatcher<Object, MTGCard>(MTGCard.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with text: " + name);
            }

            @Override
            protected boolean matchesSafely(MTGCard item) {
                return name.equals(item.getName());
            }

        };
    }

    public static Matcher<View> withDrawable(final int resourceId) {
        return new DrawableMatcher(resourceId);
    }

    public static Matcher<View> noDrawable() {
        return new DrawableMatcher(-1);
    }

}

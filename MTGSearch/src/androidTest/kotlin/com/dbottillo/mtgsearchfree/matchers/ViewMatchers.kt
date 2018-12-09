package com.dbottillo.mtgsearchfree.matchers

import android.support.test.espresso.matcher.BoundedMatcher
import android.view.View

import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet

import org.hamcrest.Description
import org.hamcrest.Matcher

object ViewMatchers {

    fun setWithName(name: String): Matcher<Any> {

        return object : BoundedMatcher<Any, MTGSet>(MTGSet::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("with text: $name")
            }

            override fun matchesSafely(item: MTGSet): Boolean {
                return name == item.name
            }
        }
    }

    fun cardWithName(name: String): Matcher<Any> {

        return object : BoundedMatcher<Any, MTGCard>(MTGCard::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("with text: $name")
            }

            override fun matchesSafely(item: MTGCard): Boolean {
                return name == item.name
            }
        }
    }

    fun withDrawable(resourceId: Int): Matcher<View> {
        return DrawableMatcher(resourceId)
    }

    fun noDrawable(): Matcher<View> {
        return DrawableMatcher(-1)
    }
}

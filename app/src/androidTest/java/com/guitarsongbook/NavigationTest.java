package com.guitarsongbook;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NavigationTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void navigationTestTwo() throws InterruptedException {
        ViewInteraction cardView = onView(
                allOf(withId(R.id.all_songs_card_view),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.scroll_view),
                                        0),
                                1)));
        cardView.perform(scrollTo(), click());

        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText("WSZYSTKIE"))));

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.song_list_rv_),
                        childAtPosition(
                                withId(R.id.parent_view),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(1, click()));

        Thread.sleep(3000);
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.displayed_song_title_txt_), withText("A gdy jest już ciemno"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.lyrics_rv_),
                                        0),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("A gdy jest już ciemno")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }



}

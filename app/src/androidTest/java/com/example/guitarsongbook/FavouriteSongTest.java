package com.example.guitarsongbook;


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
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class FavouriteSongTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void favouriteSongTest() throws InterruptedException {
        ViewInteraction cardView = onView(
                allOf(withId(R.id.all_songs_card_view),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.scroll_view),
                                        0),
                                1)));
        cardView.perform(scrollTo(), click());

        Thread.sleep(4000);

        String songTitle = "A wszystko to, bo Ciebie kocham!";

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.favourite_btn_),
                        hasSibling(withText(songTitle)),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.app_bar_layout),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction cardView2 = onView(
                allOf(withId(R.id.fav_songs_card_view),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.scroll_view),
                                        0),
                                2)));
        cardView2.perform(scrollTo(), click());

        Thread.sleep(4000);

        ViewInteraction textView = onView(
                allOf(withId(R.id.song_title_txt_), withText(songTitle),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        0),
                                1),
                        isDisplayed()));
        textView.check(matches(isDisplayed()));
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

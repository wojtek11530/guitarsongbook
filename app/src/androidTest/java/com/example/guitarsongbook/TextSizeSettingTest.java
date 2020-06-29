package com.example.guitarsongbook;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TextSizeSettingTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void textSizeSettingTest() throws InterruptedException {
        ViewInteraction cardView = onView(
                allOf(withId(R.id.settings_card_view),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.scroll_view),
                                        0),
                                6)));
        cardView.perform(scrollTo(), click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recycler_view),
                        childAtPosition(
                                withClassName(is("android.widget.FrameLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(allOf(withId(R.id.select_dialog_listview),
                        childAtPosition(
                                withId(R.id.contentPanel),
                                1)))
                .atPosition(4);
        appCompatCheckedTextView.perform(click());

        ViewInteraction appCompatImageButton = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.toolbar),
                                childAtPosition(
                                        withId(R.id.app_bar_layout),
                                        0)),
                        2),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction cardView2 = onView(
                allOf(withId(R.id.all_songs_card_view),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.scroll_view),
                                        0),
                                1)));
        cardView2.perform(scrollTo(), click());

        Thread.sleep(2000);

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.song_list_rv_),
                        childAtPosition(
                                withId(R.id.parent_view),
                                0)));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));

        Thread.sleep(2000);

        ViewInteraction textView = onView(
                allOf(withId(R.id.song_lyric_line_txt_), withText("Kołysał nas zachodni wiatr"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.lyrics_rv_),
                                        3),
                                0),
                        isDisplayed()));
        textView.check(matches(withFontSize(24)));
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

    public static Matcher<View> withFontSize(final float expectedSize) {
        return new BoundedMatcher<View, View>(View.class) {

            @Override
            public boolean matchesSafely(View target) {
                if (!(target instanceof TextView)) {
                    return false;
                }
                TextView targetEditText = (TextView) target;
                float pixels = targetEditText.getTextSize();
                float actualSize = Math.round(pixels / target.getResources().getDisplayMetrics().scaledDensity);
                return Float.compare(actualSize, expectedSize) == 0;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with fontSize: ");
                description.appendValue(expectedSize);
            }
        };
    }
}

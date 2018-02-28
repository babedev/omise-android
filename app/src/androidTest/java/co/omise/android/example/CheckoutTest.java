package co.omise.android.example;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.intent.Intents.init;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.release;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CheckoutTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        init();
    }

    @After
    public void tearDown() {
        release();
    }

    @Test
    @Ignore("Required PUBLIC_KEY to run test")
    public void checkoutShouldSuccessful() throws InterruptedException {
        // Select product
        onData(anything())
                .inAdapterView(allOf(withId(R.id.list_products),
                        childAtPosition(withId(android.R.id.content), 0)))
                .atPosition(0)
                .perform(click());

        onView(withId(R.id.button_checkout)).perform(click());

        // Enter credit card form
        onView(withId(R.id.edit_card_number)).perform(replaceText("4242 4242 4242 4242"), closeSoftKeyboard());
        onView(withId(R.id.edit_card_name)).perform(replaceText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.spinner_expiry_month)).perform(click());
        onData(anything())
                .inAdapterView(withClassName(is("com.android.internal.app.AlertController$RecycleListView")))
                .atPosition(2)
                .perform(click());
        onView(withId(R.id.spinner_expiry_year)).perform(click());
        onData(anything())
                .inAdapterView(withClassName(is("com.android.internal.app.AlertController$RecycleListView")))
                .atPosition(12)
                .perform(click());
        onView(withId(R.id.edit_security_code)).perform(replaceText("123"), closeSoftKeyboard());
        onView(withId(R.id.button_submit)).perform(click());

        sleep(3000);

        intended(hasComponent(ReceiptActivity.class.getName()));
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

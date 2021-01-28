package com.cmgcode.minimoods.about

import android.content.Intent.*
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.cmgcode.minimoods.R
import com.cmgcode.minimoods.util.Constants
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers.arrayContaining
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class AboutActivityTest {

    @get:Rule val scenario = ActivityScenarioRule(AboutActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun when_OpeningContact_Expect_IntentWithEmail() {
        onView(withText(R.string.contact_us)).perform(click())

        val intent = allOf(
            hasAction(ACTION_SENDTO),
            hasData(Uri.parse(Constants.MAIL_TO)),
            hasExtra(equalTo(EXTRA_EMAIL), arrayContaining(Constants.EMAIL))
        )

        intended(
            allOf(
                hasAction(ACTION_CHOOSER),
                hasExtra(equalTo(EXTRA_INTENT), intent)
            )
        )
    }

    @Test
    fun when_OpeningWebsite_Expect_IntentWithPersonalWebsite() {
        onView(withText(R.string.visit_website)).perform(click())

        intended(
            allOf(
                hasAction(ACTION_VIEW),
                hasData(Uri.parse(Constants.WEBSITE))
            )
        )
    }

    @Test
    fun when_OpeningContribute_Expect_IntentWithRepo() {
        onView(withText(R.string.contribute)).perform(click())

        intended(
            allOf(
                hasAction(ACTION_VIEW),
                hasData(Uri.parse(Constants.REPO))
            )
        )
    }
}

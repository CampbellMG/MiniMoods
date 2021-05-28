package com.cmgcode.minimoods.moods

import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import com.cmgcode.minimoods.MiniMoodsApplication
import com.cmgcode.minimoods.R
import com.cmgcode.minimoods.about.AboutActivity
import com.cmgcode.minimoods.mock.MockContainer
import com.cmgcode.minimoods.util.DateHelpers.isToday
import com.cmgcode.minimoods.util.getTestValue
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class MainActivityTest {

    private lateinit var container: MockContainer

    @Before
    fun setUp() {
        container = MockContainer()

        MiniMoodsApplication.module = container

        launch(MainActivity::class.java)

        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun when_PressingMood_Expect_MoodAddedToDatabase() {
        onView(withId(R.id.mood_1)).perform(click())

        val mood = container.repository.moods.getTestValue().first()

        assertThat(mood.date.isToday()).isTrue()
        assertThat(mood.mood).isEqualTo(1)
    }

    @Test
    fun when_PressingSettings_Expect_NavigationToAbout() {
        onView(withId(R.id.settings)).perform(scrollTo(), click())

        intended(hasComponent(AboutActivity::class.java.name))
    }

    @Test
    fun when_NoCrashReportingPreference_Expect_Card() {
        container.moodViewModel.updateCrashReportingPreference(null)

        onView(withText(R.string.crash_report_prompt)).check(matches(isDisplayed()))
    }

    @Test
    fun when_PressingYesToCrashReporting_Expect_ShouldLogSetToTrue() {
        container.moodViewModel.updateCrashReportingPreference(null)

        onView(withText(R.string.crash_report_prompt)).check(matches(isDisplayed()))

        onView(withText(R.string.yes)).perform(click())

        assertThat(container.repository.shouldReportCrashes).isTrue()
    }

    @Test
    fun when_PressingNoToCrashReporting_Expect_ShouldLogSetToFalse() {
        container.moodViewModel.updateCrashReportingPreference(null)

        onView(withText(R.string.crash_report_prompt)).check(matches(isDisplayed()))

        onView(withText(R.string.no)).perform(click())

        assertThat(container.repository.shouldReportCrashes).isFalse()
    }

}

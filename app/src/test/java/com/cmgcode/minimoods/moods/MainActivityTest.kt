package com.cmgcode.minimoods.moods

import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.cmgcode.minimoods.R
import com.cmgcode.minimoods.about.AboutActivity
import com.cmgcode.minimoods.fakes.FakeMoodRepository
import com.cmgcode.minimoods.fakes.FakePreferencesDao
import com.cmgcode.minimoods.util.DateHelpers.isToday
import com.cmgcode.minimoods.util.getTestValue
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject


@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29], application = HiltTestApplication::class)
class MainActivityTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: FakeMoodRepository

    @Inject
    lateinit var preferences: FakePreferencesDao

    @Before
    fun setUp() {
        hiltRule.inject()

        launch(MainActivity::class.java)

        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun when_PressingMood_Expect_MoodAddedToDatabase() = runTest {
        // WHEN
        onView(withId(R.id.mood_1)).perform(click())

        // THEN
        val mood = repository.getAllMoods().first()

        assertThat(mood.date.isToday()).isTrue()
        assertThat(mood.mood).isEqualTo(1)
    }

    @Test
    fun when_PressingMoodTwice_Expect_MoodAddedAndRemovedFromDatabase() {
        // WHEN
        onView(withId(R.id.mood_1)).perform(click())
        onView(withId(R.id.mood_1)).perform(click())

        // THEN
        assertThat(repository.moods.getTestValue()).isEmpty()
    }

    @Test
    fun when_PressingSettings_Expect_NavigationToAbout() {
        // WHEN
        onView(withId(R.id.settings))
            .perform(scrollTo(), click())

        // THEN
        intended(hasComponent(AboutActivity::class.java.name))
    }

    @Test
    fun when_NoCrashReportingPreference_Expect_Card() {
        // WHEN
        onView(withText(R.string.crash_report_prompt))
            // THEN
            .check(matches(isDisplayed()))
    }

    @Test
    fun when_PressingYesToCrashReporting_Expect_ShouldLogSetToTrue() = runTest {
        // WHEN
        onView(withText(R.string.yes)).perform(click())

        // THEN
        assertThat(preferences.shouldReportCrashes.value).isTrue()
    }

    @Test
    fun when_PressingNoToCrashReporting_Expect_ShouldLogSetToFalse() = runTest {
        // WHEN
        onView(withText(R.string.no)).perform(click())

        // THEN
        assertThat(preferences.shouldReportCrashes.value).isFalse()
    }
}
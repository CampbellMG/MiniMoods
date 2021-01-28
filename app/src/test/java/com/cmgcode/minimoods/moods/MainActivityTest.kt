package com.cmgcode.minimoods.moods

import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.cmgcode.minimoods.MiniMoodsApplication
import com.cmgcode.minimoods.R
import com.cmgcode.minimoods.mock.MockContainer
import com.cmgcode.minimoods.util.DateHelpers.isToday
import com.cmgcode.minimoods.util.getTestValue
import com.google.common.truth.Truth.assertThat
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
    }

    @Test
    fun when_PressingMood_Expect_MoodAddedToDatabase() {
        onView(withId(R.id.mood_1)).perform(click())

        val mood = container.repository.moods.getTestValue().first()

        assertThat(mood.date.isToday()).isTrue()
        assertThat(mood.mood).isEqualTo(1)
    }
}

package com.cmgcode.minimoods.features.dateselection

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cmgcode.minimoods.R
import com.cmgcode.minimoods.util.assertScreenshot
import com.cmgcode.minimoods.util.toText
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = RobolectricDeviceQualifiers.Pixel7Pro)
class DateRangeSelectorKtTest {

    @get:Rule
    val composeTest = createComposeRule()

    @Test
    fun `WHEN initialised EXPECT dates and actions visible`() {
        composeTest.setContent {
            DateRangeSelector(onFinish = {})
        }

        composeTest.onRoot().assertScreenshot()
    }

    @Test
    fun `WHEN cancelling EXPECT null returned`() {
        val onFinishObserver = mockk<(Pair<Long, Long>?) -> Unit>(relaxed = true)

        composeTest.setContent {
            DateRangeSelector(onFinish = onFinishObserver)
        }

        composeTest
            .onNodeWithContentDescription(R.string.cancel.toText())
            .performClick()

        verify { onFinishObserver(null) }
    }

    @Test
    fun `WHEN selecting dates EXPECT save disabled until both selected and range returned`() {
        val onFinishObserver = mockk<(Pair<Long, Long>?) -> Unit>(relaxed = true)

        composeTest.setContent {
            DateRangeSelector(onFinish = onFinishObserver)
        }

        composeTest
            .onNodeWithText(R.string.save.toText())
            .assertIsNotEnabled()

        composeTest.onRoot().assertScreenshot("none_selected")

        composeTest
            .onAllNodesWithText("Monday, ", substring = true)[0]
            .performClick()

        composeTest
            .onNodeWithText(R.string.save.toText())
            .assertIsNotEnabled()

        composeTest.onRoot().assertScreenshot("one_selected")

        composeTest
            .onAllNodesWithText("Monday, ", substring = true)[1]
            .performClick()

        composeTest.onRoot().assertScreenshot("both_selected")

        composeTest
            .onNodeWithText(R.string.save.toText())
            .assertIsEnabled()
            .performClick()

        verify { onFinishObserver(match { it.first > 0 && it.second > 0 }) }
    }
}
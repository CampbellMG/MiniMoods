package com.cmgcode.minimoods.moods.tiles

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cmgcode.minimoods.R
import com.cmgcode.minimoods.features.moods.DateSelection
import com.cmgcode.minimoods.features.moods.tiles.DateSelector
import com.cmgcode.minimoods.util.assertScreenshot
import com.cmgcode.minimoods.util.toDate
import com.cmgcode.minimoods.util.toText
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@Suppress("TestFunctionName")
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = RobolectricDeviceQualifiers.Pixel7Pro)
class DateSelectorKtTest {

    @get:Rule
    val composeTest = createComposeRule()

    @Test
    fun `WHEN initialised EXPECT text matches date selection`() {
        val dateSelections = listOf(
            DateSelection.Today,
            DateSelection.ThisWeek,
            DateSelection.ThisMonth,
            DateSelection.Custom(837133200000.toDate(), 818211600000.toDate())
        )

        composeTest.setContent {
            DateSelectorList(dateSelections)
        }

        composeTest.onRoot().assertScreenshot()
    }

    @Test
    fun `WHEN selecting EXPECT options opened with current at top then closed`() {
        composeTest.setContent {
            DateSelector(
                dateSelection = DateSelection.ThisWeek,
                onDateSelected = {},
                openDateSelector = {}
            )
        }

        composeTest
            .onNodeWithText(R.string.this_week.toText())
            .performClick()

        composeTest.onRoot().assertScreenshot("opened")

        composeTest
            .onNodeWithText(R.string.this_month.toText())
            .performClick()

        composeTest.onRoot().assertScreenshot("closed")
    }

    @Test
    fun `WHEN selecting preset ranges EXPECT matching selection`() {
        val dateSelectionObserver = mockk<(DateSelection) -> Unit>(relaxed = true)
        val options = mapOf(
            DateSelection.Today to R.string.today,
            DateSelection.ThisWeek to R.string.this_week,
            DateSelection.ThisMonth to R.string.this_month
        )

        composeTest.setContent {
            DateSelector(
                dateSelection = DateSelection.ThisWeek,
                onDateSelected = dateSelectionObserver,
                openDateSelector = {}
            )
        }

        options.forEach { (dateRange, textRes) ->
            composeTest
                .onNodeWithText(R.string.this_week.toText())
                .performClick()

            composeTest
                .onNodeWithText(textRes.toText())
                .performClick()


            verify { dateSelectionObserver(dateRange) }
        }
    }

    @Test
    fun `WHEN selecting custom EXPECT openDateSelector`() {
        val openDateSelectorObserver = mockk<() -> Unit>(relaxed = true)

        composeTest.setContent {
            DateSelector(
                dateSelection = DateSelection.Today,
                onDateSelected = {},
                openDateSelector = openDateSelectorObserver
            )
        }

        composeTest
            .onNodeWithText(R.string.today.toText())
            .performClick()

        composeTest
            .onNodeWithText(R.string.custom.toText())
            .performClick()

        verify { openDateSelectorObserver() }
    }

    @Composable
    private fun DateSelectorList(dateSelections: List<DateSelection>) {
        Column {
            dateSelections.forEach {
                DateSelector(
                    dateSelection = it,
                    onDateSelected = {},
                    openDateSelector = {}
                )
            }
        }
    }
}
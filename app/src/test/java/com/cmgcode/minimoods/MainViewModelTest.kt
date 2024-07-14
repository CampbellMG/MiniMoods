package com.cmgcode.minimoods

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class MainViewModelTest {
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        viewModel = MainViewModel()
    }

    @Test
    fun `WHEN initialised EXPECT date range selection null`() = runTest {
        viewModel.state.test {
            val state = awaitItem()

            assertThat(state.dateRangeSelection).isNull()
        }
    }

    @Test
    fun `WHEN date range selected EXPECT date range selection`() = runTest {
        val dateRangeSelection = 1L to 2L
        viewModel.selectDateRange(dateRangeSelection)

        viewModel.state.test {
            val state = awaitItem()

            assertThat(state.dateRangeSelection).isEqualTo(dateRangeSelection)
        }
    }

    @Test
    fun `WHEN date range selected and cleared EXPECT date range selection null`() = runTest {
        viewModel.selectDateRange(1L to 2L)
        viewModel.clearDateRangeSelection()

        viewModel.state.test {
            val state = awaitItem()

            assertThat(state.dateRangeSelection).isNull()
        }
    }

}
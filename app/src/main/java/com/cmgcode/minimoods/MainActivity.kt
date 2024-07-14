package com.cmgcode.minimoods

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.cmgcode.minimoods.features.Destinations
import com.cmgcode.minimoods.features.dateselection.DateRangeSelector
import com.cmgcode.minimoods.features.moods.DashboardScreen
import com.cmgcode.minimoods.features.theme.MiniMoodsTheme
import com.cmgcode.minimoods.util.DarkModePreferenceWatcher
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val darkModePreferenceWatcher by lazy {
        DarkModePreferenceWatcher(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(darkModePreferenceWatcher)

        setContent {
            val navController = rememberNavController()
            val viewModel: MainViewModel = viewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()

            MiniMoodsTheme {
                NavHost(
                    navController = navController,
                    startDestination = Destinations.dashboard
                ) {
                    composable(Destinations.dashboard) {
                        DashboardScreen(
                            dateRangeSelection = state.dateRangeSelection,
                            dateRangeSelectionConsumed = viewModel::clearDateRangeSelection,
                            openDateSelector = { navController.navigate(Destinations.dateRangeSelector) }
                        )
                    }

                    composable(Destinations.dateRangeSelector) {
                        DateRangeSelector(onFinish = { selection ->
                            if (selection != null) {
                                viewModel.selectDateRange(selection)
                            }

                            navController.popBackStack()
                        })
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(darkModePreferenceWatcher)
    }
}

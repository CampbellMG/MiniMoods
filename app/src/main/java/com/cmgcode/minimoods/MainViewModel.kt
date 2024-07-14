package com.cmgcode.minimoods

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


data class MainState(
    val dateRangeSelection: Pair<Long, Long>?
)

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow(initialState())
    val state = _state.asStateFlow()

    fun selectDateRange(selection: Pair<Long, Long>) {
        _state.value = _state.value.copy(dateRangeSelection = selection)
    }

    fun clearDateRangeSelection() {
        _state.value = _state.value.copy(dateRangeSelection = null)
    }

    private fun initialState() = MainState(dateRangeSelection = null)
}
package com.cmgcode.minimoods.features.dateselection

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.cmgcode.minimoods.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSelector(onFinish: (Pair<Long, Long>?) -> Unit) {
    val dateRangePickerState = rememberDateRangePickerState()
    val isComplete = dateRangePickerState.selectedStartDateMillis != null
            && dateRangePickerState.selectedEndDateMillis != null

    fun finish() {
        val start = dateRangePickerState.selectedStartDateMillis
        val end = dateRangePickerState.selectedEndDateMillis

        val result = if (start != null && end != null) {
            Pair(start, end)
        } else {
            null
        }

        println("finishing")
        onFinish(result)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { finish() }) {
                        Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.cancel))
                    }
                },
                actions = {
                    TextButton(
                        enabled = isComplete,
                        onClick = { finish() }
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            )
        }
    ) {
        DateRangePicker(
            modifier = Modifier
                .fillMaxSize()
                .padding(it), state = dateRangePickerState
        )
    }
}
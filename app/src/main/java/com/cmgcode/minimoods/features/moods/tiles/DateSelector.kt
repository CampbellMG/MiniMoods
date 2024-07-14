package com.cmgcode.minimoods.features.moods.tiles

import android.content.Context
import android.text.format.DateFormat.getMediumDateFormat
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.cmgcode.minimoods.R
import com.cmgcode.minimoods.features.moods.DateSelection
import com.cmgcode.minimoods.features.moods.DateSelection.Custom
import com.cmgcode.minimoods.features.moods.tiles.DateSelectionOption.RawDateSelectionOption
import com.cmgcode.minimoods.features.moods.tiles.DateSelectionOption.ResDateSelectionOption
import java.util.Date

private sealed interface DateSelectionOption {
    val dateSelection: DateSelection

    fun getText(resources: Context): String
    data class ResDateSelectionOption(
        @StringRes private val text: Int,
        override val dateSelection: DateSelection
    ) : DateSelectionOption {
        override fun getText(resources: Context): String {
            return resources.getString(text)
        }
    }

    data class RawDateSelectionOption(
        private val rawText: String,
        override val dateSelection: DateSelection
    ) : DateSelectionOption {
        override fun getText(resources: Context): String {
            return rawText
        }
    }
}

private val dateSelectionOptions = listOf(
    ResDateSelectionOption(R.string.today, dateSelection = DateSelection.Today),
    ResDateSelectionOption(R.string.this_week, dateSelection = DateSelection.ThisWeek),
    ResDateSelectionOption(R.string.this_month, dateSelection = DateSelection.ThisMonth),
    ResDateSelectionOption(R.string.custom, dateSelection = Custom(Date(), Date()))
)

@Composable
fun DateSelector(
    dateSelection: DateSelection,
    onDateSelected: (DateSelection) -> Unit,
    openDateSelector: () -> Unit
) {
    val currentOpenDateSelector by rememberUpdatedState(openDateSelector)
    val dateFormat = getMediumDateFormat(LocalContext.current)
    var menuOpen by remember { mutableStateOf(false) }

    fun formatDate(date: Date) = dateFormat.format(date)
    fun itemClicked(option: DateSelectionOption) {
        if (menuOpen) {
            if (option.dateSelection is Custom) {
                currentOpenDateSelector()
            } else {
                onDateSelected(option.dateSelection)
            }
        }

        menuOpen = !menuOpen
    }

    val topOption = dateSelectionOptions
        .first { dateSelection.isSameTypeAs(it.dateSelection) }
        .let {
            val selection = it.dateSelection

            if (selection is Custom) {
                RawDateSelectionOption(
                    rawText = "${formatDate(selection.start)} to ${formatDate(selection.end)}",
                    dateSelection = it.dateSelection
                )
            } else {
                it
            }
        }

    val bottomOptions = dateSelectionOptions
        .filter { !dateSelection.isSameTypeAs(it.dateSelection) }

    Column {
        Card(
            shape = if (menuOpen) {
                RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
            } else {
                RoundedCornerShape(8.dp)
            },
            modifier = Modifier
                .padding(bottom = if (menuOpen) 1.dp else 0.dp)
                .fillMaxWidth()
                .clickable { itemClicked(topOption) }
        ) {
            Text(
                text = topOption.getText(LocalContext.current),
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        AnimatedVisibility(
            visible = menuOpen,
            enter = expandIn(initialSize = { IntSize(it.width, 0) }),
            exit = shrinkOut(targetSize = { IntSize(it.width, 0) })
        ) {
            Column {
                bottomOptions.forEachIndexed { i, it ->
                    val isLast = i == bottomOptions.size - 1

                    Card(
                        shape = if (isLast) {
                            RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                        } else {
                            RectangleShape
                        },
                        modifier = Modifier
                            .padding(bottom = if (isLast) 0.dp else 1.dp)
                            .fillMaxWidth()
                            .clickable { itemClicked(it) }
                    ) {
                        Text(
                            text = it.getText(LocalContext.current),
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

private fun DateSelection.isSameTypeAs(other: DateSelection): Boolean {
    return this == other || (this is Custom && other is Custom)
}
package com.cmgcode.minimoods.features.moods

import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cmgcode.minimoods.R
import com.cmgcode.minimoods.features.moods.tiles.DateSelector
import com.cmgcode.minimoods.features.moods.tiles.Graphs
import com.cmgcode.minimoods.util.toDate

@Composable
fun DashboardScreen(
    dateRangeSelection: Pair<Long, Long>?,
    dateRangeSelectionConsumed: () -> Unit,
    openDateSelector: () -> Unit
) {
    val viewModel = hiltViewModel<MoodViewModel>()
    val currentDateRangeCallback by rememberUpdatedState(dateRangeSelectionConsumed)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(dateRangeSelection) {
        if (dateRangeSelection != null) {
            val (start, end) = dateRangeSelection
            viewModel.selectDate(DateSelection.Custom(start.toDate(), end.toDate()))
            currentDateRangeCallback()
        }
    }

    Dashboard(state, viewModel::selectDate, openDateSelector)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(
    state: MoodState,
    selectDate: (DateSelection) -> Unit,
    openDateSelector: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val scaffoldState = rememberBottomSheetScaffoldState(
        rememberStandardBottomSheetState(
            confirmValueChange = {
                val isTargetingExpanded =
                    !listOf(SheetValue.Hidden, SheetValue.PartiallyExpanded).contains(it)
                if (isTargetingExpanded != expanded) {
                    expanded = isTargetingExpanded
                }

                true
            }
        )
    )


    val offset = try {
        scaffoldState.bottomSheetState.requireOffset()
    } catch (e: IllegalStateException) {
        0f
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 100.dp,
        sheetContent = { MoodEditor(expanded, offset) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.size(16.dp))

            Text(stringResource(R.string.date_range))

            Spacer(Modifier.size(16.dp))

            DateSelector(
                dateSelection = state.selectedDateRange,
                onDateSelected = { selectDate(it) },
                openDateSelector = openDateSelector
            )

            Spacer(Modifier.size(32.dp))

            Graphs()

            Spacer(Modifier.size(16.dp))

//            MoodList()
        }
    }
}

@Composable
@Preview
fun MoodList() {
    RoundedLazyColumn(listOf(1, 2, 3)) { item, shape ->
        Card(shape = shape) {
            Text(text = item.toString())
        }
    }
}

@Composable
private fun <T> RoundedLazyColumn(items: List<T>, itemContent: @Composable (T, Shape) -> Unit) {
    LazyColumn {
        itemsIndexed(items) { i, it ->
            val (shape, bottomPadding) = when {
                items.size == 1 -> RoundedCornerShape(8.dp) to 0.dp
                i == 0 -> RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp) to 1.dp
                i == items.size - 1 -> RoundedCornerShape(
                    bottomStart = 8.dp,
                    bottomEnd = 8.dp
                ) to 0.dp

                else -> RectangleShape to 1.dp
            }

            itemContent(it, shape)
            Spacer(Modifier.size(bottomPadding))
        }
    }
}

@Composable
fun MoodEditor(hasExpandedState: Boolean, offset: Float) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.toPx
    var maxOffset by remember { mutableFloatStateOf(offset) }
    val visibility = (maxOffset - offset) / (screenHeight / 4)

    LaunchedEffect(offset) {
        if (offset > maxOffset) {
            maxOffset = offset
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.height(((visibility * 24).dp))) {
            Text("12th July 1996")
        }
        Row {
            Button(
                onClick = { /*TODO*/ },
                shape = RoundedCornerShape(8.dp, 0.dp, 0.dp, 8.dp),
                modifier = Modifier.padding(end = 1.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_mood_1),
                    contentDescription = "Ecstatic",
                    modifier = Modifier.size(24.dp)
                )
            }
            Button(
                onClick = { /*TODO*/ },
                shape = RectangleShape,
                modifier = Modifier.padding(end = 1.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_mood_2),
                    contentDescription = "Content",
                    modifier = Modifier.size(24.dp)
                )
            }
            Button(
                onClick = { /*TODO*/ },
                shape = RectangleShape,
                modifier = Modifier.padding(end = 1.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_mood_3),
                    contentDescription = "Neutral",
                    modifier = Modifier.size(24.dp)
                )
            }
            Button(
                onClick = { /*TODO*/ },
                shape = RectangleShape,
                modifier = Modifier.padding(end = 1.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_mood_4),
                    contentDescription = "Disappointed",
                    modifier = Modifier.size(24.dp)
                )
            }
            Button(onClick = { /*TODO*/ }, shape = RoundedCornerShape(0.dp, 8.dp, 8.dp, 0.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_mood_5),
                    contentDescription = "Devastated",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Row(
            modifier = Modifier
                .height(200.dp)
                .alpha(visibility)
        ) {
            TextField(value = "", onValueChange = {})
            Button(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_mood_5),
                    contentDescription = "Devastated",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
@Preview
fun ExpandedMoodEditor() {
    MoodEditor(
        hasExpandedState = true,
        offset = 0f
    )
}

@Composable
@Preview
fun CollapsedMoodEditor() {
    MoodEditor(
        hasExpandedState = false,
        offset = 100f
    )
}

val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )
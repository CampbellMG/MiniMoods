package com.cmgcode.minimoods.features.moods.tiles

import android.graphics.PointF
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.cmgcode.minimoods.R
import com.cmgcode.minimoods.data.Mood
import com.cmgcode.minimoods.features.moods.tiles.GraphOption.Calendar
import com.cmgcode.minimoods.features.moods.tiles.GraphOption.LineGraph
import com.cmgcode.minimoods.util.toLocalDateTime
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle.FULL
import java.time.format.TextStyle.NARROW
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.math.roundToLong

sealed interface GraphOption {
    object LineGraph : GraphOption
    object Calendar : GraphOption
}


@Composable
fun Graphs() {
    val graphOption = remember { mutableStateOf<GraphOption>(LineGraph) }
    val zoneOffset = ZoneId
        .systemDefault()
        .rules
        .getOffset(Instant.now())

    val now = LocalDateTime.now()
    val moods = (1..100).map {
        val range = when (it) {
            in (0..30) -> (5..5)
            in (30..60) -> (3..5)
            in (60..90) -> (1..2)
            else -> (1..1)
        }
        Mood(
            date = now.plusDays(it.toLong()).toInstant(zoneOffset).toEpochMilli(),
            mood = range.random()
        )
    }

    val color = LocalContentColor

    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(.5f)
//                    .background(color)
                    .clickable { graphOption.value = LineGraph },
                shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
            ) {
                Text(modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally), text = "Line graph")
            }
            
            Spacer(modifier = Modifier.width(1.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { graphOption.value = Calendar },
                shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
            ) {
                Text(modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally), text = "Calendar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if(graphOption.value == LineGraph) {
            LineGraph(moods)
        } else {
            DateGraph(moods)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DateGraph(moods: List<Mood>) {
    // TODO - Average values on same day
    val moodValues = moods.associate { it.date.toLocalDateTime().with(LocalTime.MIN) to it.mood }

    val locale = Locale.getDefault()
    // TODO - add this as a preference
    val mondayIsFirst = true
    val firstDayOfWeek = if (mondayIsFirst) 1 else WeekFields.of(locale).firstDayOfWeek.value
    val weekdays = DayOfWeek
        .values()
        .map { it.getDisplayName(FULL, locale) to it.getDisplayName(NARROW, locale) }

    val dayNames = arrayListOf<Pair<String, String>>()
    // Start with firstDayOfWeek - 1 as the days are 1-based.
    for (i in firstDayOfWeek - 1 until weekdays.size) {
        dayNames.add(weekdays[i])
    }
    for (i in 0 until firstDayOfWeek - 1) {
        dayNames.add(weekdays[i])
    }
    var size by remember { mutableStateOf(IntSize.Zero) }


    val (start, end) = moods
        .sortedBy { it.date }
        .run { first().date.toLocalDateTime() to last().date.toLocalDateTime() }

    val totalMonths = ChronoUnit.MONTHS.between(start, end).toInt()
    val maxCalendarRows = 6
    val daysInWeek = 7
    val state = rememberLazyListState()

    BoxWithConstraints {
        val cellSize = this.maxWidth / daysInWeek
        LazyRow(
            state = state,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = state)
        ) {
            items(totalMonths) { month ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = )
                    Row {
                        dayNames.forEach { (description, text) ->
                            Box(
                                modifier = Modifier
                                    .clearAndSetSemantics { contentDescription = description }
                                    .size(cellSize),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text)
                            }
                        }
                    }

                    var cellIndex = 0
                    val currentMonth = start.plusMonths(month.toLong()).withDayOfMonth(1)
                    Column {
                        repeat(maxCalendarRows) {
                            Row {
                                repeat(daysInWeek) {
                                    val difference = currentMonth.dayOfWeek.value - firstDayOfWeek
                                    val daysFromStartOfWeekToFirstOfMonth = if (difference < 0) {
                                        difference + daysInWeek
                                    } else {
                                        difference
                                    }
                                    val dayNumber =
                                        (cellIndex - daysFromStartOfWeekToFirstOfMonth) + 1

                                    if (cellIndex < daysFromStartOfWeekToFirstOfMonth || cellIndex >= (daysFromStartOfWeekToFirstOfMonth + currentMonth.toLocalDate()
                                            .lengthOfMonth())
                                    ) {
                                        Spacer(Modifier.size(cellSize))
                                    } else {
                                        val day = currentMonth.withDayOfMonth(dayNumber)
                                            .with(LocalTime.MIN)
                                        val mood = moodValues[day]
                                        val color = moodColor(mood)

                                        Box(
                                            modifier = Modifier
                                                .size(cellSize)
                                                .background(colorResource(color)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(dayNumber.toString())
                                        }
                                    }

                                    cellIndex++
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun moodColor(mood: Int?): Int {
    return when (mood) {
        1 -> R.color.colorMood1
        2 -> R.color.colorMood2
        3 -> R.color.colorMood3
        4 -> R.color.colorMood4
        5 -> R.color.colorMood5
        else -> R.color.colorBackground
    }
}

@Composable
fun LineGraph(moods: List<Mood>) {
    val sampleCount = 10f
    val sortedMoods = moods.sortedBy { it.date }
    val start = sortedMoods.first().date
    val end = sortedMoods.last().date
    val interval = ((end - start) / sampleCount).roundToLong()
    val intervals = (start..end step interval).map { it + interval }
    val points = sortedMoods
        .groupBy { mood -> intervals.first { mood.date <= it } }
        .map { it.value.map { mood -> mood.mood }.average().roundToInt() }
        .map { 6 - it }
    val colors =
        (1..5).associateWith { Color(ContextCompat.getColor(LocalContext.current, moodColor(it))) }
    val yValues = (1..5).toList()

    val height = LocalConfiguration.current.screenHeightDp / 3
    Column(
        Modifier
            .fillMaxWidth()
            .height(height.dp)
    ) {
        Row(Modifier.weight(1f)) {
            Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                Image(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(id = R.drawable.ic_mood_1),
                    colorFilter = ColorFilter.tint(colorResource(id = R.color.colorMood1)),
                    contentDescription = "Very happy"
                )

                Image(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(id = R.drawable.ic_mood_3),
                    colorFilter = ColorFilter.tint(colorResource(id = R.color.colorMood3)),
                    contentDescription = "Neutral"
                )

                Image(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(id = R.drawable.ic_mood_5),
                    colorFilter = ColorFilter.tint(colorResource(id = R.color.colorMood5)),
                    contentDescription = "Very unhappy"
                )
            }

            Box(Modifier.padding(vertical = 16.dp, horizontal = 8.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val controlPoints1 = mutableListOf<PointF>()
                    val controlPoints2 = mutableListOf<PointF>()
                    val coordinates = mutableListOf<PointF>()
                    val xAxisSpace = size.width / (points.size - 1)
                    val yAxisSpace = size.height / (yValues.size - 1)

                    for (i in points.indices) {
                        val x1 = xAxisSpace * i
                        val y1 = size.height - (yAxisSpace * (points[i] - 1))

                        coordinates.add(PointF(x1, y1))
                    }

                    for (i in 1 until coordinates.size) {
                        controlPoints1.add(
                            PointF(
                                (coordinates[i].x + coordinates[i - 1].x) / 2,
                                coordinates[i - 1].y
                            )
                        )
                        controlPoints2.add(
                            PointF(
                                (coordinates[i].x + coordinates[i - 1].x) / 2,
                                coordinates[i].y
                            )
                        )
                    }

                    val stroke = Path().apply {
                        reset()
                        moveTo(coordinates.first().x, coordinates.first().y)
                        for (i in 0 until coordinates.size - 1) {
                            cubicTo(
                                controlPoints1[i].x, controlPoints1[i].y,
                                controlPoints2[i].x, controlPoints2[i].y,
                                coordinates[i + 1].x, coordinates[i + 1].y
                            )
                        }
                    }

                    drawPath(
                        stroke,
                        brush = Brush.linearGradient(
                            colors.values.toList().reversed(),
                            start = Offset(0f, size.height),
                            end = Offset.Zero
                        ),
                        style = Stroke(
                            width = 15f,
                            cap = StrokeCap.Butt
                        )
                    )
                }
            }

        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val midWay = start + ((end - start) / 2)
            val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LocalContext.current.resources.configuration.locales.get(0)
            } else {
                LocalContext.current.resources.configuration.locale
            }

            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                .withLocale(locale)
            val modifier = Modifier.rotate(25f)

            Text(modifier = modifier, text = formatter.format(start.toLocalDateTime()))
            Text(modifier = modifier, text = formatter.format(midWay.toLocalDateTime()))
            Text(modifier = modifier, text = formatter.format(end.toLocalDateTime()))
        }
    }
}
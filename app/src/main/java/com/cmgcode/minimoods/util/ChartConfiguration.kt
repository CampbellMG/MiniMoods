package com.cmgcode.minimoods.util

import com.cmgcode.minimoods.data.Mood
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

object ChartConfiguration {
    fun LineChart.configure(noDataText: String) {
        setDrawBorders(false)
        setNoDataText(noDataText)
        configureYAxis()

        xAxis.isEnabled = false
        legend.isEnabled = false
        description.isEnabled = false
    }

    fun LineChart.addMoods(moods: List<Mood>, lineColor: Int) {
        val moodData = moods.map { Entry(it.date.time.toFloat(), it.getInvertedMood().toFloat()) }
        val data = LineDataSet(moodData, "").apply { configureLineDataSet(lineColor) }

        this.data = LineData(data).apply { setDrawValues(false) }

        animateY(600, Easing.EaseInBack)
    }

    private fun LineDataSet.configureLineDataSet(lineColor: Int) {
        setDrawCircles(false)

        mode = LineDataSet.Mode.CUBIC_BEZIER
        cubicIntensity = 0.18f
        color = lineColor
        lineWidth = 5f
    }

    private fun LineChart.configureYAxis() {
        axisRight.isEnabled = false
        axisLeft.apply {
            axisMaximum = 6f
            axisMinimum = 0f
            granularity = 1f
            setLabelCount(4, false)
            setDrawAxisLine(false)
            setDrawLabels(false)
        }
    }

}

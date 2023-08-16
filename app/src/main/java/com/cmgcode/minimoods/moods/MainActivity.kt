package com.cmgcode.minimoods.moods

import android.appwidget.AppWidgetManager
import android.content.ClipData
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.content.FileProvider
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.cmgcode.minimoods.R
import com.cmgcode.minimoods.about.AboutActivity
import com.cmgcode.minimoods.data.Mood
import com.cmgcode.minimoods.databinding.ActivityMainBinding
import com.cmgcode.minimoods.util.ChartConfiguration.addMoods
import com.cmgcode.minimoods.util.ChartConfiguration.configure
import com.cmgcode.minimoods.util.Constants.TAG_DATE_PICKER
import com.cmgcode.minimoods.util.DarkModePreferenceWatcher
import com.cmgcode.minimoods.util.DateHelpers
import com.cmgcode.minimoods.util.DateHelpers.toCalendar
import com.cmgcode.minimoods.util.Event
import com.cmgcode.minimoods.widget.MiniMoodsWidgetProvider
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.Calendar
import java.util.Date


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MoodViewModel by viewModels()
    private val darkModePreferenceWatcher by lazy {
        DarkModePreferenceWatcher(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding
            .inflate(layoutInflater)
            .also {
                it.viewModel = viewModel
                it.lifecycleOwner = this
            }

        setContentView(binding.root)

        initialiseView()
        watchData()

        getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(darkModePreferenceWatcher)
    }

    override fun onDestroy() {
        super.onDestroy()

        getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(darkModePreferenceWatcher)
    }

    private fun initialiseView() {
        binding.settings.setOnClickListener { openAbout() }
        binding.date.setOnClickListener { showDatePicker() }

        configureCalendar()

        binding.chart.configure(getString(R.string.no_moods))
    }

    private fun watchData() {
        viewModel.moods.observe(this) { moods ->
            updateCalendar(moods)
            binding.chart.addMoods(moods, getColor(this, R.color.colorPrimary))
        }

        viewModel.selectedDate.observe(this) { initialiseView() }
        viewModel.exportEvent.observe(this) { exportMoodFile(it) }
        viewModel.currentMood.observe(this) { updateWidget() }
    }

    private fun showDatePicker() {
        MaterialDatePicker.Builder
            .datePicker()
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    viewModel.selectedDate.value = Date().apply { time = it }
                }
            }
            .show(supportFragmentManager, TAG_DATE_PICKER)
    }

    private fun configureCalendar() {
        val date = viewModel.selectedDate.value ?: return
        val range = DateHelpers.getMonthRange(date)
        val from = Calendar.getInstance().apply { timeInMillis = range.first }
        val to = Calendar.getInstance().apply { timeInMillis = range.second }

        binding.calendar.apply {
            setMinimumDate(from)
            setMaximumDate(to)
            setDate(date.toCalendar())
            setSwipeEnabled(false)
            setOnDayClickListener(object : OnDayClickListener {
                override fun onDayClick(eventDay: EventDay) {
                    viewModel.selectedDate.value = eventDay.calendar.time
                }
            })
        }
    }

    private fun updateCalendar(moods: List<Mood>) {
        val days = moods.map { mood ->
            CalendarDay(mood.date.toCalendar()).also { day ->
                day.backgroundDrawable = getDrawable(this, R.drawable.calendar_event)
                    ?.also {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            it.setTint(getColor(this, mood.getMoodColor()))
                        }
                    }
            }
        }

        binding.calendar.setCalendarDays(days)
    }

    private fun exportMoodFile(event: Event<String>?) {
        createMoodFile(event)?.let { exportFile(it) }
    }

    private fun createMoodFile(event: Event<String>?): File? {
        return event?.unhandledData?.let {
            val exportsDir = File(filesDir, "exports").apply { mkdirs() }
            File(exportsDir, "${Date()}.csv").apply { writeBytes(it.toByteArray()) }
        }
    }

    private fun exportFile(file: File) {
        val uri = FileProvider.getUriForFile(this, packageName, file)
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/csv"
            clipData = ClipData("Mood Export", arrayOf(type), ClipData.Item(uri))
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.export))
        startActivity(shareIntent)
    }

    private fun updateWidget() {
        val widgetName = MiniMoodsWidgetProvider.componentName(this)
        val ids = AppWidgetManager.getInstance(this).getAppWidgetIds(widgetName)
        val intent = Intent(this, MiniMoodsWidgetProvider::class.java).also {
            it.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }

        sendBroadcast(intent)
    }

    private fun openAbout() {
        startActivity(Intent(this, AboutActivity::class.java))
    }
}

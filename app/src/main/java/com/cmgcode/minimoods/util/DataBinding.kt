package com.cmgcode.minimoods.util

import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import com.cmgcode.minimoods.util.DateHelpers.toCalendar
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.DAY_OF_MONTH

object DataBinding {

    @JvmStatic
    @BindingAdapter("app:dayOfTheMonth")
    fun loadError(inputLayout: TextView, date: Date?) {
        val dayOfMonth = date?.toCalendar()?.get(DAY_OF_MONTH)
        val suffix = getDaySuffix(dayOfMonth)
        val format = SimpleDateFormat("d'${suffix} of' MMMM", Locale.getDefault())

        inputLayout.text = date?.let { format.format(it) }
    }

    @JvmStatic
    @BindingAdapter("app:tint")
    fun setImageTint(imageView: ImageView, @ColorInt color: Int) {
        imageView.setColorFilter(color)
    }

    private fun getDaySuffix(dayOfMonth: Int?): String {
        if (dayOfMonth == null) {
            return ""
        }

        if (dayOfMonth in 11..13) {
            return "th"
        }

        return when (dayOfMonth % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }
}

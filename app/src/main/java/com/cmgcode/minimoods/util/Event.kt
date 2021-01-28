package com.cmgcode.minimoods.util

class Event<out T>(private val data: T) {

    private var handled = false

    val unhandledData: T?
        get() = if (handled) {
            null
        } else {
            handled = true
            data
        }

    class Observer<T>(private val eventHandler: (T) -> Unit) : androidx.lifecycle.Observer<Event<T>?> {
        override fun onChanged(event: Event<T>?) {
            event?.unhandledData?.let(eventHandler)
        }
    }
}

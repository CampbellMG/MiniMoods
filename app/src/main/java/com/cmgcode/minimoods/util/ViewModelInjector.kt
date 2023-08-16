package com.cmgcode.minimoods.util

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider


interface ViewModelFactory<T> {
    fun create(): T

    companion object {
        @MainThread
        inline fun <reified VM : ViewModel> ComponentActivity.viewModelBuilder(
            factory: ViewModelFactory<VM>
        ): Lazy<VM> {
            return ViewModelLazy(
                viewModelClass = VM::class,
                storeProducer = { viewModelStore },
                factoryProducer = {
                    return@ViewModelLazy object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return factory.create() as T
                        }
                    }
                }
            )
        }
    }
}

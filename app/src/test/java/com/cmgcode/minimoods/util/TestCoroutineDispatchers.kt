package com.cmgcode.minimoods.util

import com.cmgcode.minimoods.dependencies.CoroutineDispatchers
import kotlinx.coroutines.Dispatchers

fun TestCoroutineDispatchers() =
    CoroutineDispatchers(Dispatchers.Unconfined, Dispatchers.Unconfined)

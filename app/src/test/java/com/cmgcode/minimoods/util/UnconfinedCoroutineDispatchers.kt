package com.cmgcode.minimoods.util

import com.cmgcode.minimoods.dependencies.CoroutineDispatchers
import kotlinx.coroutines.Dispatchers

fun UnconfinedCoroutineDispatchers() = CoroutineDispatchers(
    main = Dispatchers.Unconfined,
    io = Dispatchers.Unconfined
)
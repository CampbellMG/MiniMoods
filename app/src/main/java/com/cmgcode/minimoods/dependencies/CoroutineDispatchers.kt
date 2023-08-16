package com.cmgcode.minimoods.dependencies

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object CoroutineDispatchers {
    val io: CoroutineDispatcher = Dispatchers.IO
}
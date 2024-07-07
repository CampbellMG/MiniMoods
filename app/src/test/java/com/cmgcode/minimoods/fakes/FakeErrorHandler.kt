package com.cmgcode.minimoods.fakes

import com.cmgcode.minimoods.handlers.error.ErrorHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeErrorHandler @Inject constructor() : ErrorHandler {
    override fun init() = Unit
}

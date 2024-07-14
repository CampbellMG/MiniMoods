package com.cmgcode.minimoods.util

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation


fun Int.toText() = getInstrumentation().targetContext.getString(this)

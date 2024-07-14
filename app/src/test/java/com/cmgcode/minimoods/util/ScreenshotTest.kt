package com.cmgcode.minimoods.util

import androidx.compose.ui.test.SemanticsNodeInteraction
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test

fun SemanticsNodeInteraction.assertScreenshot(stateDescription: String? = null) {
    captureRoboImage(generateName(stateDescription))
}

// Adapted from com.github.takahirom.roborazzi.DefaultFileNameGenerator
private fun generateName(stateDescription: String? = null): String {
    val stackTraceElement = Thread.getAllStackTraces()
        .asSequence()
        .filter { isRobolectricMainOrTestThread(it.key) }
        .flatMap { it.value.toList() }
        .firstOrNull { isTestFunction(it) }
        ?: throw RuntimeException("Could not find stack trace element for snapshot name")

    val directory = stackTraceElement.fileName.replace(" ", "_")
    val fileName = stackTraceElement.methodName.replace(" ", "_")
    val description = stateDescription
        ?.let { "-${it}" }
        .orEmpty()

    return "./src/test/snapshots/$directory/$fileName${description}.png"
}


// https://github.com/robolectric/robolectric/blob/40832ada4a0651ecbb0151ebed2c99e9d1d71032/robolectric/src/main/java/org/robolectric/internal/AndroidSandbox.java#L67
private fun isRobolectricMainOrTestThread(it: Thread) =
    it.name.contains("Main Thread") || it.name.contains("Test worker")

private fun isTestFunction(it: StackTraceElement) = try {
    Class.forName(it.className)
        .getMethod(it.methodName)
        .getAnnotation(Test::class.java) != null
} catch (e: Exception) {
    false
}

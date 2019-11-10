package com.zestworks.helpers

import android.widget.EditText

fun EditText.getTextAsDouble(): Double = if (text.isBlank()) {
    0.0
} else {
    val double = try {
        text.toString().toDouble()
    } catch (e: NumberFormatException) {
        0.0
    }
    double
}

fun EditText.setNonZeroDoubleOrEmpty(value: Double) {
    if (value == 0.0) {
        setText("")
    } else {
        editableText.replace(
            0,
            editableText.length,
            String.format("%.3f", value)
        )
    }
}
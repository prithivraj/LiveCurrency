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
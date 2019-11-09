package com.zestworks.helpers

import android.widget.EditText

fun EditText.getTextAsDouble(): Double = if(text.isBlank()){
    0.0
} else {
    text.toString().toDouble()
}
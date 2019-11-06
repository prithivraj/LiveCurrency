package com.zestworks.helpers

sealed class LCEState<out T : Any> {
    object Loading : LCEState<Nothing>()
    data class Content<out T : Any>(val data: T) : LCEState<T>()
    data class Error(val errorMessage: String) : LCEState<Nothing>()
}
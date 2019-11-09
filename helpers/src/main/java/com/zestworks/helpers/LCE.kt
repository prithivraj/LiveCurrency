package com.zestworks.helpers

/*
L - Loading
C - Content
E - Error
 */
sealed class LCE<out T : Any> {
    object Loading : LCE<Nothing>()
    data class Content<out T : Any>(val data: T) : LCE<T>()
    data class Error(val errorMessage: String) : LCE<Nothing>()
}
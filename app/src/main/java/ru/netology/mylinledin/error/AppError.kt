package ru.netology.mylinledin.error

import android.database.SQLException
import ru.netology.mylinledin.error.DbError.code
import java.io.IOException

sealed class AppError(var code: String) : RuntimeException() {
    companion object {
        fun from(e: Throwable): AppError = when (e) {
            is AppError -> e
            is SQLException -> DbError
            is IOException -> NetworkError
            else -> UnknownError
        }
    }
}

class ApiError(val status: Int, code: String) : AppError(code)
object NetworkError : AppError(code)
object DbError : AppError("error_db")
object UnknownError : AppError("error_unknown")
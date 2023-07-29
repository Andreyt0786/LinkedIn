package ru.netology.mylinledin.model

data class RegisterModelState(
    val firstView: Boolean = false,
    val errorApi: Boolean = false,
    val error: Boolean = false,
    val complete: Boolean = false,
    val errorPass: Boolean = false,
)
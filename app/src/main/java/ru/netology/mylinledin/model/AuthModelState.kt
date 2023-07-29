package ru.netology.mylinledin.model

data class AuthModelState(
    val firstView: Boolean = false,
    val errorApi: Boolean = false,
    val error: Boolean = false,
    val complete:Boolean = false,
    val signOut:Boolean=false,
)


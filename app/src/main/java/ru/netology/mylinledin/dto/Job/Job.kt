package ru.netology.mylinledin.dto.Job

data class Job(
    val id: Int,
    val name: String,
    val position: String,
    val start: String,
    val finish: String?,
    val link: String?,
    val ownedByMe: Boolean = false
)

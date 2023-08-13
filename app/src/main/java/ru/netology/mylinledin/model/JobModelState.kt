package ru.netology.mylinledin.model

import ru.netology.mylinledin.dto.Job.Job

class JobModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
)

data class JobModel(
    val jobs: List<Job> = emptyList(),
    val empty: Boolean = false,
    val emptyUser: Boolean = false,
)
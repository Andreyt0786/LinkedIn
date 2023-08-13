package ru.netology.mylinledin.entity.job

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.mylinledin.dto.Job.Job

@Entity
data class JobEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val position: String,
    val start: String,
    val finish: String?,
    val link: String?,
    ) {

    fun toDto() = Job(
        id = id,
        name = name,
        position = position,
        start = start,
        finish = finish,
        link = link,
    )

    companion object {
        fun fromDto(dto: Job) =
            JobEntity(
                id = dto.id,
                name = dto.name,
                position = dto.position,
                start = dto.start,
                finish = dto.finish,
                link = dto.link,
            )
    }

}

fun List<JobEntity>.toDto(): List<Job> = map(JobEntity::toDto)
fun List<Job>.toJobEntity(): List<JobEntity> = map(JobEntity::fromDto)

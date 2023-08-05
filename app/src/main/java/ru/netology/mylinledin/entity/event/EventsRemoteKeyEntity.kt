package ru.netology.mylinledin.entity.event

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class EventsRemoteKeyEntity(
    @PrimaryKey
    val type: KeyType,
    val key: Int,
) {
    enum class KeyType {
        AFTER,
        BEFORE,
    }
}

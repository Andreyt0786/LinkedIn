package ru.netology.mylinledin.dao.Post.event

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.mylinledin.entity.event.EventsRemoteKeyEntity

@Dao
interface EventsRemoteKeyDao {

    @Query("SELECT COUNT(*) == 0 FROM EventsRemoteKeyEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT MAX(key) FROM EventsRemoteKeyEntity")
    suspend fun max(): Int?

    @Query("SELECT MIN(key) FROM EventsRemoteKeyEntity")
    suspend fun min(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key: EventsRemoteKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: List<EventsRemoteKeyEntity>)

    @Query("DELETE FROM EventsRemoteKeyEntity")
    suspend fun removeAll()
}

package ru.netology.mylinledin.dao.Post.event

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.mylinledin.entity.event.EventEntity

@Dao
interface EventsDao {
    @Query("SELECT * FROM EventEntity ORDER BY id DESC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM EventEntity ORDER BY id DESC")// WHERE hidden = 0 ORDER BY id DESC" убрал пока не работает обновление
    fun getPagingSourceEvents(): PagingSource<Int, EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Query("UPDATE EventEntity SET content = :content, link=:link, authorJob =:authorJob WHERE id = :id")
    suspend fun updateContentByIdEvents(id:Int, content: String, link: String?, authorJob: String?)

    suspend fun saveEvents(event: EventEntity) =
        if (event.id == 0) insertEvents(event) else updateContentByIdEvents(event.id, event.content, event.link,event.authorJob)

    @Query(
        """
        UPDATE EventEntity SET
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """
    )
    suspend fun likeByIdEvents(id: Int)//В реализации поменял аргумент на post

    @Query("DELETE FROM EventEntity WHERE id = :id")
    suspend fun removeByIdEvents(id: Int)

    @Query("DELETE FROM EventEntity")
    suspend fun clearEvents()

    @Query("SELECT COUNT(*) == 0 FROM EventEntity")
    suspend fun isEmptyEvents(): Boolean


}

package ru.netology.mylinledin.dao.wall

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.mylinledin.entity.wallEntity.WallEntity

@Dao
interface WallDao {
    @Query("SELECT * FROM WallEntity ORDER BY id DESC")
    fun getAllWall(): Flow<List<WallEntity>>

    @Query("SELECT * FROM WallEntity WHERE authorId = :authorId ORDER BY id DESC")
    fun getForWall(authorId: Int): Flow<List<WallEntity>>

    @Query("SELECT * FROM WallEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, WallEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWall(post: WallEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWall(posts: List<WallEntity>)

    @Query("UPDATE WallEntity SET content = :content WHERE id = :id")
    suspend fun updateContentByIdWall(id: Int, content: String)

    suspend fun saveWall(post: WallEntity) =
        if (post.id == 0) insertWall(post) else updateContentByIdWall(post.id, post.content)

    @Query(
        """
        UPDATE WallEntity SET
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """
    )
    suspend fun likeByIdWall(id: Int)//В реализации поменял аргумент на post

    @Query("DELETE FROM WallEntity WHERE id = :id")
    suspend fun removeByIdWall(id: Int)

    @Query("DELETE FROM WallEntity")
    suspend fun clearWall()

    @Query("SELECT COUNT(*) == 0 FROM WallEntity")
    suspend fun isEmptyWall(): Boolean


}

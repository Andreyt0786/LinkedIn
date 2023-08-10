package ru.netology.mylinledin.dao.job

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.mylinledin.entity.WallEntity.WallEntity
import ru.netology.mylinledin.entity.job.JobEntity

@Dao
interface JobDao {
    @Query("SELECT * FROM JobEntity ORDER BY id DESC")
    fun getAllJob(): Flow<List<JobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(job: JobEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(jobs: List<JobEntity>)

    @Query("DELETE FROM JobEntity WHERE id = :id")
    suspend fun removeByIdJob(id: Int)

    @Query("DELETE FROM JobEntity")
    suspend fun clear()

    @Query("SELECT COUNT(*) == 0 FROM JobEntity")
    suspend fun isEmptyWall(): Boolean


}

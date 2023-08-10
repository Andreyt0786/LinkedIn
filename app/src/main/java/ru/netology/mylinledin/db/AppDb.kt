package ru.netology.mylinledin.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.mylinledin.dao.Post.PostDao
import ru.netology.mylinledin.dao.Post.PostRemoteKeyDao
import ru.netology.mylinledin.dao.Post.event.EventsDao
import ru.netology.mylinledin.dao.Post.event.EventsRemoteKeyDao
import ru.netology.mylinledin.dao.job.JobDao
import ru.netology.mylinledin.dao.wall.WallDao
import ru.netology.mylinledin.entity.PostEntity
import ru.netology.mylinledin.entity.PostRemoteKeyEntity
import ru.netology.mylinledin.entity.WallEntity.WallEntity
import ru.netology.mylinledin.entity.event.EventEntity
import ru.netology.mylinledin.entity.event.EventsRemoteKeyEntity
import ru.netology.mylinledin.entity.job.JobEntity

@Database(
    entities = [PostEntity::class, PostRemoteKeyEntity::class, EventEntity::class, EventsRemoteKeyEntity::class, WallEntity::class, JobEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao

    abstract fun eventDao(): EventsDao

    abstract fun eventRemoteKeyDao(): EventsRemoteKeyDao

    abstract fun wallDao(): WallDao

    abstract fun jobDao(): JobDao
}
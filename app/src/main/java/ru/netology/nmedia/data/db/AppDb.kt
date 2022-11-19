package ru.netology.nmedia.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.nmedia.data.dao.PostDao
import ru.netology.nmedia.data.dao.PostRemoteKeyDao
import ru.netology.nmedia.data.entity.PostEntity
import ru.netology.nmedia.data.entity.PostRemoteKeyEntity

@Database(
    entities = [PostEntity::class, PostRemoteKeyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
}
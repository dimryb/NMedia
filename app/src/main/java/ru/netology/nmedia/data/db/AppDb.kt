package ru.netology.nmedia.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.nmedia.data.dao.PostDao
import ru.netology.nmedia.data.entity.PostEntity

@Database(entities = [PostEntity::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
}
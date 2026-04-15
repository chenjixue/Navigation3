package com.example.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.database.dao.NewsResourceDao
import com.example.database.model.NewsResourceEntity
import com.example.database.model.NewsResourceTopicCrossRef
import com.example.database.model.TopicEntity
//import com.example.database.model.InstantConverter

@Database(
    entities = [
        NewsResourceEntity::class,
        NewsResourceTopicCrossRef::class,
        TopicEntity::class,
    ],
    version = 14,
    exportSchema = true,
)


internal abstract class NiaDatabase : RoomDatabase() {
    abstract fun newsResourceDao(): NewsResourceDao
}

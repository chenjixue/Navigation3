package com.example.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.database.dao.ChouhuResourceDao
import com.example.database.dao.NewsResourceDao
import com.example.database.dao.NoSaleResourceDao
import com.example.database.dao.OtherExpenseResourceDao
import com.example.database.dao.PeopleExpenseResourceDao
import com.example.database.model.SaleResourceEntity
import com.example.database.dao.SaleResourceDao
import com.example.database.dao.TopicDao
import com.example.database.model.ChouhuResourceEntity
import com.example.database.model.NewsResourceEntity
import com.example.database.model.NewsResourceTopicCrossRef
import com.example.database.model.NoSaleResourceEntity
import com.example.database.model.PeopleExpenseResourceEntity
import com.example.database.model.OtherExpenseResourceEntity
import com.example.database.model.TopicEntity

//import com.example.database.model.InstantConverter

@Database(
    entities = [
        NewsResourceEntity::class,
        NewsResourceTopicCrossRef::class,
        TopicEntity::class,
        ChouhuResourceEntity::class,
        PeopleExpenseResourceEntity::class,
        OtherExpenseResourceEntity::class,
        SaleResourceEntity::class,
        NoSaleResourceEntity::class,
    ],
    version = 17,
    exportSchema = true,
)


internal abstract class NiaDatabase : RoomDatabase() {
    //    abstract fun topicDao(): TopicDao
//    abstract fun newsResourceDao(): NewsResourceDao
    abstract fun saleResourceDao(): SaleResourceDao
    abstract fun noSaleResourceDao(): NoSaleResourceDao
    abstract fun chouhuResourceDao(): ChouhuResourceDao
    abstract fun peopleExpenseResourceDao(): PeopleExpenseResourceDao
    abstract fun otherExpenseResourceDao(): OtherExpenseResourceDao
}

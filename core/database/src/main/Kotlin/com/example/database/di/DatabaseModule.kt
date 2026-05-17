/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.database.NiaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val MIGRATION_16_17 = object : Migration(16, 17) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // chouhu_resources
        database.execSQL("ALTER TABLE chouhu_resources RENAME TO chouhu_resources_old")
        database.execSQL("CREATE TABLE chouhu_resources (`key` TEXT NOT NULL, `name` TEXT NOT NULL, `unit_price` REAL NOT NULL, `count` INTEGER NOT NULL, `data_time` TEXT NOT NULL, PRIMARY KEY(`key`))")
        database.execSQL("INSERT INTO chouhu_resources (`key`, `name`, `unit_price`, `count`, `data_time`) SELECT `key`, `name`, CAST(`unit_price` AS REAL), `count`, `data_time` FROM chouhu_resources_old")
        database.execSQL("DROP TABLE chouhu_resources_old")
        
        // sale_resources
        database.execSQL("ALTER TABLE sale_resources RENAME TO sale_resources_old")
        database.execSQL("CREATE TABLE sale_resources (`key` TEXT NOT NULL, `level` TEXT NOT NULL, `unit_price` REAL NOT NULL, `count` INTEGER NOT NULL, `data_time` TEXT NOT NULL, PRIMARY KEY(`key`))")
        database.execSQL("INSERT INTO sale_resources (`key`, `level`, `unit_price`, `count`, `data_time`) SELECT `key`, `level`, CAST(`unit_price` AS REAL), `count`, `data_time` FROM sale_resources_old")
        database.execSQL("DROP TABLE sale_resources_old")
        
        // no_sale_resources
        database.execSQL("ALTER TABLE no_sale_resources RENAME TO no_sale_resources_old")
        database.execSQL("CREATE TABLE no_sale_resources (`key` TEXT NOT NULL, `level` TEXT NOT NULL, `unit_price` REAL NOT NULL, `count` INTEGER NOT NULL, `data_time` TEXT NOT NULL, PRIMARY KEY(`key`))")
        database.execSQL("INSERT INTO no_sale_resources (`key`, `level`, `unit_price`, `count`, `data_time`) SELECT `key`, `level`, CAST(`unit_price` AS REAL), `count`, `data_time` FROM no_sale_resources_old")
        database.execSQL("DROP TABLE no_sale_resources_old")
        
        // people_expense_resources
        database.execSQL("ALTER TABLE people_expense_resources RENAME TO people_expense_resources_old")
        database.execSQL("CREATE TABLE people_expense_resources (`key` TEXT NOT NULL, `name` TEXT NOT NULL, `price` REAL NOT NULL, `gender` TEXT NOT NULL, `data_time` TEXT NOT NULL, PRIMARY KEY(`key`))")
        database.execSQL("INSERT INTO people_expense_resources (`key`, `name`, `price`, `gender`, `data_time`) SELECT `key`, `name`, CAST(`price` AS REAL), `gender`, `data_time` FROM people_expense_resources_old")
        database.execSQL("DROP TABLE people_expense_resources_old")
        
        // other_expense_resources
        database.execSQL("ALTER TABLE other_expense_resources RENAME TO other_expense_resources_old")
        database.execSQL("CREATE TABLE other_expense_resources (`key` TEXT NOT NULL, `name` TEXT NOT NULL, `unit_price` REAL NOT NULL, `count` INTEGER NOT NULL, `data_time` TEXT NOT NULL, PRIMARY KEY(`key`))")
        database.execSQL("INSERT INTO other_expense_resources (`key`, `name`, `unit_price`, `count`, `data_time`) SELECT `key`, `name`, CAST(`unit_price` AS REAL), `count`, `data_time` FROM other_expense_resources_old")
        database.execSQL("DROP TABLE other_expense_resources_old")
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesNiaDatabase(
        @ApplicationContext context: Context,
    ): NiaDatabase = Room.databaseBuilder(
        context,
        NiaDatabase::class.java,
        "nia-database",
    )
    .addMigrations(MIGRATION_16_17)
    .build()
}

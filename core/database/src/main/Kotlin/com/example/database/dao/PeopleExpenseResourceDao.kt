package com.example.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.database.model.PeopleExpenseResourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PeopleExpenseResourceDao {
    @Transaction
    @Query(
        value = """
            SELECT * FROM people_expense_resources
            WHERE data_time = :dataTime
        """,
    )
    fun getPeopleExpenseResources(
        dataTime: String
    ): Flow<List<PeopleExpenseResourceEntity>>

    @Query(value = "SELECT * FROM people_expense_resources")
    fun getPeopleExpenseResources(): Flow<List<PeopleExpenseResourceEntity>>

    @Upsert
    suspend fun upsertPeopleExpenseResources(peopleExpenseResourceEntities: List<PeopleExpenseResourceEntity>)

    @Query(
        value = """
            DELETE FROM people_expense_resources
            WHERE `key` in (:keys)
        """,
    )
    suspend fun deletePeopleExpenseResources(keys: List<String>)
}
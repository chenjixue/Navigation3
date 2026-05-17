package com.example.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.database.model.OtherExpenseResourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OtherExpenseResourceDao {
    @Transaction
    @Query(
        value = """
            SELECT * FROM other_expense_resources
            WHERE data_time = :dataTime
        """,
    )
    fun getOtherExpenseResources(
        dataTime: String
    ): Flow<List<OtherExpenseResourceEntity>>

    @Query(value = "SELECT * FROM other_expense_resources")
    fun getOtherExpenseResources(): Flow<List<OtherExpenseResourceEntity>>

    @Upsert
    suspend fun upsertOtherExpenseResources(otherExpenseResourceEntities: List<OtherExpenseResourceEntity>)

    @Query(
        value = """
            DELETE FROM other_expense_resources
            WHERE `key` in (:keys)
        """,
    )
    suspend fun deleteOtherExpenseResources(keys: List<String>)
}
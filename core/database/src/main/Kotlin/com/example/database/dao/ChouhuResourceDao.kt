package com.example.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.database.model.ChouhuResourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChouhuResourceDao {
    @Transaction
    @Query(
        value = """
            SELECT * FROM chouhu_resources
            WHERE data_time = :dataTime
        """,
    )
    fun getChouhuResources(
        dataTime: String
    ): Flow<List<ChouhuResourceEntity>>

    @Query(value = "SELECT * FROM chouhu_resources")
    fun getChouhuResources(): Flow<List<ChouhuResourceEntity>>

    @Upsert
    suspend fun upsertChouhuResources(chouhuResourceEntities: List<ChouhuResourceEntity>)

    @Query(
        value = """
            DELETE FROM chouhu_resources
            WHERE `key` in (:keys)
        """,
    )
    suspend fun deleteChouhuResources(keys: List<String>)
}
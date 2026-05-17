package com.example.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.database.model.ChouhuResourceEntity
import com.example.database.model.SaleResourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleResourceDao {
    @Transaction
    @Query(
        value = """
            SELECT * FROM sale_resources
            WHERE data_time = :dataTime
        """,
    )
    fun getSaleResources(
        dataTime: String
    ): Flow<List<SaleResourceEntity>>

    @Query(value = "SELECT * FROM sale_resources")
    fun getSaleResources(): Flow<List<SaleResourceEntity>>

    @Upsert
    suspend fun upsertSaleResources(saleResourceEntities: List<SaleResourceEntity>)

    @Query(
        value = """
            DELETE FROM sale_resources
            WHERE `key` in (:keys)
        """,
    )
    suspend fun deleteSaleResources(keys: List<String>)
}
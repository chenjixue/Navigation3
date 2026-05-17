package com.example.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.database.model.ChouhuResourceEntity
import com.example.database.model.NoSaleResourceEntity
import com.example.database.model.SaleResourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoSaleResourceDao {
    @Transaction
    @Query(
        value = """
            SELECT * FROM no_sale_resources
            WHERE data_time = :dataTime
        """,
    )
    fun getNoSaleResources(
        dataTime: String
    ): Flow<List<NoSaleResourceEntity>>

    @Query(value = "SELECT * FROM no_sale_resources")
    fun getNoSaleResources(): Flow<List<NoSaleResourceEntity>>

    @Upsert
    suspend fun upsertNoSaleResources(saleResourceEntities: List<NoSaleResourceEntity>)

    @Query(
        value = """
            DELETE FROM no_sale_resources
            WHERE `key` in (:keys)
        """,
    )
    suspend fun deleteNoSaleResources(keys: List<String>)
}
package com.example.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.NewsResource
import com.example.model.ChouhuResource
import com.example.model.NoSaleResource
import kotlin.String
/**
 * Defines an NiA news resource.
 */
@Entity(
    tableName = "no_sale_resources",
)
data class NoSaleResourceEntity(
    @PrimaryKey
    val key: String,
    val level: String,
    val count: Int,
    @ColumnInfo(name = "data_time")
    val dataTime: String,
)

fun NoSaleResourceEntity.asExternalModel() = NoSaleResource(
    key = key,
    level = level,
    count = count,
    dataTime = dataTime
)

fun NoSaleResource.asNoSaleEntity() = NoSaleResourceEntity(
    key = key,
    level = level,
    count = count,
    dataTime = dataTime
)
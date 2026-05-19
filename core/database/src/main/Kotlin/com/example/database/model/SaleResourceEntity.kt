package com.example.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.NewsResource
import com.example.model.ChouhuResource
import com.example.model.SaleResource
import kotlin.String
/**
 * Defines an NiA news resource.
 */
@Entity(
    tableName = "sale_resources",
)
data class SaleResourceEntity(
    @PrimaryKey
    val key: String,
    @ColumnInfo(defaultValue = "")
    val name: String,
    val level: String,
    @ColumnInfo(name = "unit_price")
    val unitPrice: Double,
    val count: Int,
    @ColumnInfo(name = "data_time")
    val dataTime: String,
)

fun SaleResourceEntity.asExternalModel() = SaleResource(
    key = key,
    name = name,
    level = level,
    unitPrice = unitPrice,
    count = count,
    dataTime = dataTime
)
fun SaleResource.asEntity() = SaleResourceEntity(
    key = key,
    name = name,
    level = level,  
    unitPrice = unitPrice,
    count = count,
    dataTime = dataTime
)
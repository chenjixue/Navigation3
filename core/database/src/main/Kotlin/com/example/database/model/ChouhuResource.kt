package com.example.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.NewsResource
import com.example.model.ChouhuResource
import kotlin.String
/**
 * Defines an NiA news resource.
 */
@Entity(
    tableName = "chouhu_resources",
)
data class ChouhuResourceEntity(
    @PrimaryKey
    val key: String,
    val name: String,
    @ColumnInfo(name = "unit_price")
    val unitPrice: Double,
    val count: Int,
    @ColumnInfo(name = "data_time")
    val dataTime: String,
)

fun ChouhuResourceEntity.asExternalModel() = ChouhuResource(
    key = key,
    name = name,
    unitPrice = unitPrice,
    count = count,
    dataTime = dataTime
)
fun ChouhuResource.asEntity() = ChouhuResourceEntity(
    key = key,
    name = name,
    unitPrice = unitPrice,
    count = count,
    dataTime = dataTime
)
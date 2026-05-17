package com.example.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.OtherExpenseResource
import kotlin.String
/**
 * Defines an NiA news resource.
 */
@Entity(
    tableName = "other_expense_resources",
)
data class OtherExpenseResourceEntity(
    @PrimaryKey
    val key: String,
    val name: String,
    @ColumnInfo(name = "unit_price")
    val unitPrice: Double,
    val count: Int,
    @ColumnInfo(name = "data_time")
    val dataTime: String,
)

fun OtherExpenseResourceEntity.asExternalModel() = OtherExpenseResource(
    key = key,
    name = name,
    unitPrice = unitPrice,
    count = count,
    dataTime = dataTime
)
fun OtherExpenseResource.asEntity() = OtherExpenseResourceEntity(
    key = key,
    name = name,
    unitPrice = unitPrice,
    count = count,
    dataTime = dataTime
)
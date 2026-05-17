package com.example.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.PeopleExpenseResource
import kotlin.String

/**
 * Defines an NiA news resource.
 */
@Entity(
    tableName = "people_expense_resources",
)
data class PeopleExpenseResourceEntity(
    @PrimaryKey
    val key: String,
    val name: String,
    val price: Double,
    val gender: String,
    @ColumnInfo(name = "data_time")
    val dataTime: String,
)

fun PeopleExpenseResourceEntity.asExternalModel() = PeopleExpenseResource(
    key = key,
    name = name,
    price = price,
    gender = gender,
    dataTime = dataTime
)

fun PeopleExpenseResource.asEntity() = PeopleExpenseResourceEntity(
    key = key,
    name = name,
    price = price,
    gender = gender,
    dataTime = dataTime
)
package com.example.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.NewsResource

/**
 * Defines an NiA news resource.
 */
@Entity(
    tableName = "news_resources",
)
data class NewsResourceEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val url: String,
    @ColumnInfo(name = "header_image_url")
    val headerImageUrl: String?,
    @ColumnInfo(name = "publish_date")
    val type: String,
)

fun NewsResourceEntity.asExternalModel() = NewsResource(
    id = id,
    title = title,
    content = content,
    url = url,
    headerImageUrl = headerImageUrl,
    type = type,
    topics = emptyList(),
)

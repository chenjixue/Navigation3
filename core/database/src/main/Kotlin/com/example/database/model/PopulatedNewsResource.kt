package com.example.database.model

import com.example.model.NewsResource
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * External data layer representation of a fully populated NiA news resource
 */
data class PopulatedNewsResource(
    @Embedded
    val entity: NewsResourceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = NewsResourceTopicCrossRef::class,
            parentColumn = "news_resource_id",
            entityColumn = "topic_id",
        ),
    )
    val topics: List<TopicEntity>,
)

fun PopulatedNewsResource.asExternalModel() = NewsResource(
    id = entity.id,
    title = entity.title,
    content = entity.content,
    url = entity.url,
    headerImageUrl = entity.headerImageUrl,
    type = entity.type,
    topics = topics.map(TopicEntity::asExternalModel),
)
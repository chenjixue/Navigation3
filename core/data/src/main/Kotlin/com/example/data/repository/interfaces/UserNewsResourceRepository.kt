package com.example.data.repository.interfaces

import com.example.model.UserNewsResource
import kotlinx.coroutines.flow.Flow

/**
 * Data layer implementation for [com.example.model.UserNewsResource]
 */
interface UserNewsResourceRepository {
    fun observeAll(
        query: NewsResourceQuery = NewsResourceQuery(
            filterTopicIds = null,
            filterNewsIds = null,
        ),
    ): Flow<List<UserNewsResource>>

    fun observeAllForFollowedTopics(): Flow<List<UserNewsResource>>
}
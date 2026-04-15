package com.example.data.repository

import com.example.data.repository.interfaces.NewsRepository
import com.example.data.repository.interfaces.NewsResourceQuery
import com.example.data.repository.interfaces.UserDataRepository
import com.example.data.repository.interfaces.UserNewsResourceRepository
import com.example.model.NewsResource
import com.example.model.UserData
import com.example.model.UserNewsResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map


fun List<NewsResource>.mapToUserNewsResources(userData: UserData): List<UserNewsResource> =
    map { UserNewsResource(it, userData) }

class CompositeUserNewsResourceRepository @Inject constructor(
    val newsRepository: NewsRepository,
    val userDataRepository: UserDataRepository,
) : UserNewsResourceRepository {

    override fun observeAll(
        query: NewsResourceQuery,
    ): Flow<List<UserNewsResource>> =
        newsRepository.getNewsResources(query)
            .combine(userDataRepository.userData) { newsResources, userData ->
                newsResources.mapToUserNewsResources(userData)
            }

    override fun observeAllForFollowedTopics(): Flow<List<UserNewsResource>> =
        userDataRepository.userData.map { it.followedTopics }.distinctUntilChanged()
            .flatMapLatest { followedTopics ->
                flowOf(emptyList())
//                when {
//                    followedTopics.isEmpty() -> flowOf(emptyList())
//                    else -> observeAll(NewsResourceQuery(filterTopicIds = followedTopics))
//                }
            }

}

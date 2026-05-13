 package com.example.domain

import com.example.data.repository.TopicsRepository
import com.example.data.repository.UserDataRepository
import com.example.domain.TopicSortField.NAME
import com.example.domain.TopicSortField.NONE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import com.example.model.FollowableTopic
import javax.inject.Inject

class GetFollowableTopicsUseCase @Inject constructor(
    private val topicsRepository: TopicsRepository,
    private val userDataRepository: UserDataRepository,
) {
    operator fun invoke(sortBy: TopicSortField = NONE): Flow<List<FollowableTopic>> = combine(
        userDataRepository.userData,
        topicsRepository.getTopics(),
    ) { userData, topics ->
        val followedTopics = topics
            .map { topic ->
                FollowableTopic(
                    topic = topic,
                    isFollowed = topic.id in userData.followedTopics,
                )
            }
        when (sortBy) {
            NAME -> followedTopics.sortedBy { it.topic.name }
            else -> followedTopics
        }
    }
}

enum class TopicSortField {
    NONE,
    NAME,
}

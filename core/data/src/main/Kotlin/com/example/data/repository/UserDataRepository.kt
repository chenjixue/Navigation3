package com.example.data.repository

import com.example.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val userData: Flow<UserData>

    suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean)

    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean)
}

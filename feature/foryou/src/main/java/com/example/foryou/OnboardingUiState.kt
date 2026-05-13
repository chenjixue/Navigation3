package com.example.foryou

import com.example.model.FollowableTopic

sealed interface OnboardingUiState {

    data object Loading : OnboardingUiState


    data object LoadFailed : OnboardingUiState


    data object NotShown : OnboardingUiState


    data class Shown(
        val topics: List<FollowableTopic>,
    ) : OnboardingUiState {

        val isDismissable: Boolean get() = topics.any { it.isFollowed }
    }
}

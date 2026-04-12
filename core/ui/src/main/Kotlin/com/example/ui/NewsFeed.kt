package com.example.ui

sealed interface NewsFeedUiState {
    /**
     * The feed is still loading.
     */
    data object Loading : NewsFeedUiState

    /**
     * The feed is loaded with the given list of news resources.
     */
    data class Success(
        /**
         * The list of news resources contained in this feed.
         */
        val feed: List<String>,
    ) : NewsFeedUiState
}
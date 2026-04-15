package com.example.model

data class UserNewsResource internal constructor(
    val id: String
) {
    constructor(newsResource: NewsResource, userData: UserData) : this(
        id = newsResource.id
    )
}



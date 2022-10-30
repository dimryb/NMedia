package ru.netology.nmedia.presentation.model

import ru.netology.nmedia.domain.dto.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false,
    val swipeRefresh: Boolean = false,
)

package ru.netology.nmedia.presentation

import ru.netology.nmedia.domain.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val loading: Boolean = false,
    val error: Boolean = false,
    val empty: Boolean = false,
)

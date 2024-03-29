package ru.netology.nmedia.presentation.model

sealed interface FeedModelState{
    object Idle: FeedModelState
    object Loading: FeedModelState
    object Refresh: FeedModelState
    object Error: FeedModelState
}

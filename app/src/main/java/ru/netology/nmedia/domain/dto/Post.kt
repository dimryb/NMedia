package ru.netology.nmedia.domain.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.netology.nmedia.domain.Attachment

sealed class FeedItem{
    abstract val id: Long
}

data class Ad(
    override val id: Long,
    val image: String,
) : FeedItem()

@Parcelize
data class Post(
    override val id: Long = 0,
    val author: String,
    val authorId: Long,
    val authorAvatar: String,
    val published: String,
    val content: String,
    val likedByMe: Boolean = false,
    val likes: Long = 0,
    val sharedCount: Long = 0,
    val viewCount: Long = 0,
    val video: String? = null,
    val attachment: Attachment? = null,
    val ownerByMe: Boolean = false,

    val isLocal: Boolean = false,
    val visible: Boolean = true,
) : Parcelable, FeedItem()


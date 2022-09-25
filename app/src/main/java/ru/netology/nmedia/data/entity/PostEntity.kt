package ru.netology.nmedia.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.domain.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val author: String,
    val authorAvatar: String,
    val published: String,
    val content: String,
    val likedByMe: Boolean = false,
    val likes: Long = 0,
    val sharedCount: Long = 0,
    val viewCount: Long = 0,
    val video: String? = null,
) {
    fun toDto(): Post = Post(
        id = id,
        author = author,
        authorAvatar = authorAvatar,
        published = published,
        content = content,
        likedByMe = likedByMe,
        likes = likes,
        sharedCount = sharedCount,
        viewCount = viewCount,
        video = video,
    )

    companion object {
        fun fromDto(post: Post): PostEntity =
            with(post){
            return PostEntity(
                id = id,
                author = author,
                authorAvatar = authorAvatar,
                published = published,
                content = content,
                likedByMe = likedByMe,
                likes = likes,
                sharedCount = sharedCount,
                viewCount = viewCount,
                video = video,
            )
        }
    }
}

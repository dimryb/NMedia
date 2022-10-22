package ru.netology.nmedia.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.domain.Attachment
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
    val attachmentUrl: String? = null,
    val attachmentDescription: String? = null,
    val attachmentType: String? = null,

    val isLocal: Boolean = false,
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
        attachment = attachmentUrl?.let { url->
            attachmentDescription?.let { description ->
                attachmentType?.let { type ->
                    Attachment(
                        url = url,
                        description = description,
                        type = type
                    )
                }
            }
        },

        isLocal = isLocal,
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
                attachmentUrl = attachment?.url,
                attachmentDescription = attachment?.description,
                attachmentType = attachment?.type,

                isLocal = isLocal,
            )
        }
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)
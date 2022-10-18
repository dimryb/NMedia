package ru.netology.nmedia.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.data.dao.PostDao
import ru.netology.nmedia.data.api.PostsApi
import ru.netology.nmedia.data.entity.PostEntity
import ru.netology.nmedia.domain.Post

class PostRepositoryImpl(private val postDao: PostDao) : PostRepository {
    override val data: LiveData<List<Post>> = postDao.getAll().map {
        it.map(PostEntity::toDto)
    }

    override suspend fun getAll(): List<Post> {
        val posts = PostsApi.retrofitService.getAll()
        postDao.insert(posts.map(PostEntity::fromDto))
        return posts
    }

    override suspend fun save(post: Post): Post {
        TODO("Not yet implemented")
    }

    override suspend fun removeById(id: Long) {

        TODO("Not yet implemented")
    }

    override suspend fun likeById(id: Long, likedByMe: Boolean): Post {
        postDao.likeById(id)
        val post = if (likedByMe)
            PostsApi.retrofitService.likeById(id)
        else
            PostsApi.retrofitService.dislikeById(id)
        postDao.insert(PostEntity.fromDto(post))
        return post
    }

    override suspend fun shareById(id: Long): Post {
        TODO("Not yet implemented")
    }

}
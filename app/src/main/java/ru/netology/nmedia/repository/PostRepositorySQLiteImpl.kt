package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.data.PostDao
import ru.netology.nmedia.data.PostEntity
import ru.netology.nmedia.domain.Post

class PostRepositorySQLiteImpl(
    private val dao: PostDao
) : PostRepository {
    override fun get(): LiveData<List<Post>> = dao.getAll().map {
        it.map (PostEntity::toDto)
    }

    override fun like(postId: Long) {
        dao.likeById(postId)
    }

    override fun share(postId: Long) {
        dao.shareById(postId)
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
    }

    override fun save(post: Post) {
        dao.save(PostEntity.fromDto(post))
    }

}
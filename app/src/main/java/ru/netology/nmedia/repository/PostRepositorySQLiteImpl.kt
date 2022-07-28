package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.data.PostDao
import ru.netology.nmedia.domain.Post

class PostRepositorySQLiteImpl(
    private val dao: PostDao
) : PostRepository {

    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        posts = dao.getAll()
        data.value = posts
    }

    override fun get(): LiveData<List<Post>> = data

    override fun like(postId: Long) {
        dao.likeById(postId)
        posts = dao.getAll()
        data.value = posts
    }

    override fun share(postId: Long) {
        dao.shareById(postId)
        posts = dao.getAll()
        data.value = posts
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
        posts = dao.getAll()
        data.value = posts
    }

    override fun save(post: Post) {
        val id = post.id
        val saved = dao.save(post)
        posts = if (id == 0L) {
            listOf(saved) + posts
        } else {
            posts.map {
                if (it.id != id) it else saved
            }
        }
        data.value = posts
    }

}
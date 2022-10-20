package ru.netology.nmedia.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import ru.netology.nmedia.data.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    fun assignPostId(post: PostEntity) = if (!post.isLocal) post.copy(localId = post.id) else post

    @Insert(onConflict = REPLACE)
    suspend fun insertDao(post: PostEntity)
    suspend fun insert(post: PostEntity) = insertDao(assignPostId(post))

    @Insert(onConflict = REPLACE)
    suspend fun insertDao(posts: List<PostEntity>)
    suspend fun insert(posts: List<PostEntity>) = insertDao(posts.map(::assignPostId))

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContentById(id: Long, content: String)

    suspend fun save(post: PostEntity) =
        if (post.id == 0L) insert(post) else updateContentById(post.id, post.content)

    @Query(
        """
        UPDATE PostEntity SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
    """
    )
    suspend fun likeById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query(
        """
        UPDATE PostEntity SET
        sharedCount = sharedCount + 1
        WHERE id = :id
    """
    )
    suspend fun shareById(id: Long)
}
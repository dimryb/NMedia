package ru.netology.nmedia.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.data.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity WHERE visible = 1 ORDER BY id DESC")
    fun getVisible(): Flow<List<PostEntity>>

    @Query("SELECT MAX(id) FROM PostEntity")
    suspend fun getMaxId(): Long

    @Insert(onConflict = REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContentById(id: Long, content: String)

    @Query("UPDATE PostEntity SET visible = 1")
    suspend fun updateVisibleAll()

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
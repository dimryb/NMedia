package ru.netology.nmedia.data

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import ru.netology.nmedia.domain.Post

class PostDaoImpl(private val db: SQLiteDatabase) : PostDao {

    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()
        db.query(
            TableName.POSTS,
            Column.ALL_COLUMNS,
            null,
            null,
            null,
            null,
            "${Column.ID} DESC"
        ).use {
            while (it.moveToNext()) {
                posts.add(map(it))
            }
        }
        return posts
    }

    override fun save(post: Post): Post {
        val values = ContentValues().apply {
            if (post.id != 0L) {
                put(Column.ID, post.id)
            }
            put(Column.AUTHOR, "Me")
            put(Column.CONTENT, post.content)
            put(Column.PUBLISHED, "now")
        }
        val id = db.replace(TableName.POSTS, null, values)
        db.query(
            TableName.POSTS,
            Column.ALL_COLUMNS,
            "${Column.ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null,
        ).use {
            it.moveToNext()
            return map(it)
        }
    }

    override fun likeById(id: Long) {
        db.execSQL(
            """
                UPDATE ${TableName.POSTS} SET
                    ${Column.LIKES} = ${Column.LIKES} + CASE WHEN ${Column.LIKED_BY_ME} THEN -1 ELSE 1 END,
                    ${Column.LIKED_BY_ME} = CASE WHEN ${Column.LIKED_BY_ME} THEN 0 ELSE 1 END
                WHERE ${Column.ID} = ?;
            """.trimIndent(), arrayOf(id)
        )
    }

    override fun removeById(id: Long) {
        db.delete(
            TableName.POSTS,
            "${Column.ID} = ?",
            arrayOf(id.toString())
        )
    }

    private fun map(cursor: Cursor): Post {
        with(cursor) {
            return Post(
                id = getLong(getColumnIndexOrThrow(Column.ID)),
                author = getString(getColumnIndexOrThrow(Column.AUTHOR)),
                authorAvatar = "",
                published = getString(getColumnIndexOrThrow(Column.PUBLISHED)),
                content = getString(getColumnIndexOrThrow(Column.CONTENT)),
                likedByMe = getInt(getColumnIndexOrThrow(Column.LIKED_BY_ME)) != 0,
                likesCount = getLong(getColumnIndexOrThrow(Column.LIKES)),
                sharedCount = 0,
                viewCount = 0,
                video = null
            )
        }
    }

    companion object {
        val DDL: String = """
            CREATE TABLE ${TableName.POSTS} (
            	${Column.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            	${Column.AUTHOR} TEXT NOT NULL,
            	${Column.CONTENT} TEXT NOT NULL,
            	${Column.PUBLISHED} TEXT NOT NULL,
            	${Column.LIKED_BY_ME} BOOLEAN NOT NULL DEFAULT false,
            	${Column.LIKES} INTEGER NOT NULL DEFAULT 0
            );
        """.trimIndent()

        object TableName {
            const val POSTS = "posts"
        }

        object Column {
            const val ID = "id"
            const val AUTHOR = "author"
            const val CONTENT = "content"
            const val PUBLISHED = "published"
            const val LIKED_BY_ME = "likedByMe"
            const val LIKES = "likes"
            val ALL_COLUMNS = arrayOf(
                ID,
                AUTHOR,
                CONTENT,
                PUBLISHED,
                LIKED_BY_ME,
                LIKES,
            )
        }
    }
}
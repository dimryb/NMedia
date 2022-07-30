package ru.netology.nmedia.data

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import ru.netology.nmedia.domain.Post

//class PostDaoImpl(private val db: SQLiteDatabase) : PostDao {
//
//    override fun getAll(): List<Post> {
//        val posts = mutableListOf<Post>()
//        db.query(
//            TableName.POSTS,
//            Column.ALL_COLUMNS,
//            null,
//            null,
//            null,
//            null,
//            "${Column.ID} DESC"
//        ).use {
//            while (it.moveToNext()) {
//                posts.add(map(it))
//            }
//        }
//        return posts
//    }
//
//    override fun save(post: Post): Post {
//        val values = ContentValues().apply {
//            if (post.id != 0L) {
//                put(Column.ID, post.id)
//            }
//            put(Column.AUTHOR, "Me")
//            put(Column.CONTENT, post.content)
//            put(Column.PUBLISHED, "now")
//        }
//        val id = db.replace(TableName.POSTS, null, values)
//        db.query(
//            TableName.POSTS,
//            Column.ALL_COLUMNS,
//            "${Column.ID} = ?",
//            arrayOf(id.toString()),
//            null,
//            null,
//            null,
//        ).use {
//            it.moveToNext()
//            return map(it)
//        }
//    }
//
//    override fun likeById(id: Long) {
//        db.execSQL(
//            """
//                UPDATE ${TableName.POSTS} SET
//                    ${Column.LIKES} = ${Column.LIKES} + CASE WHEN ${Column.LIKED_BY_ME} THEN -1 ELSE 1 END,
//                    ${Column.LIKED_BY_ME} = CASE WHEN ${Column.LIKED_BY_ME} THEN 0 ELSE 1 END
//                WHERE ${Column.ID} = ?;
//            """.trimIndent(), arrayOf(id)
//        )
//    }
//
//    override fun removeById(id: Long) {
//        db.delete(
//            TableName.POSTS,
//            "${Column.ID} = ?",
//            arrayOf(id.toString())
//        )
//    }
//
//    override fun shareById(id: Long) {
//        db.execSQL(
//            """
//                UPDATE ${TableName.POSTS} SET
//                    ${Column.SHARES} = ${Column.SHARES} + 1
//                WHERE ${Column.ID} = ?;
//            """.trimIndent(), arrayOf(id)
//        )
//    }
//
//    private fun map(cursor: Cursor): Post {
//        with(cursor) {
//            return Post(
//                id = getLong(getColumnIndexOrThrow(Column.ID)),
//                author = getString(getColumnIndexOrThrow(Column.AUTHOR)),
//                authorAvatar = getString(getColumnIndexOrThrow(Column.AUTHOR_AVATAR)),
//                published = getString(getColumnIndexOrThrow(Column.PUBLISHED)),
//                content = getString(getColumnIndexOrThrow(Column.CONTENT)),
//                likedByMe = getInt(getColumnIndexOrThrow(Column.LIKED_BY_ME)) != 0,
//                likesCount = getLong(getColumnIndexOrThrow(Column.LIKES)),
//                sharedCount = getLong(getColumnIndexOrThrow(Column.SHARES)),
//                viewCount = getLong(getColumnIndexOrThrow(Column.VIEWS)),
//                video = getString(getColumnIndexOrThrow(Column.VIDEO)),
//            )
//        }
//    }
//
//    companion object {
//        val DDL: String = """
//            CREATE TABLE ${TableName.POSTS} (
//            	${Column.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
//            	${Column.AUTHOR} TEXT NOT NULL,
//                ${Column.AUTHOR_AVATAR} TEXT NOT NULL DEFAULT "",
//                ${Column.PUBLISHED} TEXT NOT NULL,
//            	${Column.CONTENT} TEXT NOT NULL,
//            	${Column.LIKED_BY_ME} BOOLEAN NOT NULL DEFAULT false,
//            	${Column.LIKES} INTEGER NOT NULL DEFAULT 0,
//                ${Column.SHARES} INTEGER NOT NULL DEFAULT 0,
//                ${Column.VIEWS} INTEGER NOT NULL DEFAULT 0,
//                ${Column.VIDEO} TEXT DEFAULT NULL
//            );
//        """.trimIndent()
//
//        // Demo
//        val DDls: Array<String> = arrayOf(
//            DDL,
//            """
//                INSERT INTO ${TableName.POSTS}
//                    (${Column.AUTHOR}, ${Column.PUBLISHED}, ${Column.CONTENT}) VALUES (
//                    "Нетология. Университет интернет-профессий",
//                    "24 июля в 20:08",
//                    |"Привет, это новая нетология! Когда-то Нетология начиналась с интенсивов по
//                    |онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и
//                    |управлению. Мы растем сами и помогаем расти студентам: от новичков до уверенный
//                    |профессионалов. Но самое важное остается с нами: мы верим, что в каждом уже есть
//                    |сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия
//                    |- помочь встать на путь роста и начать цепочку перемен
//                    |→ http://netolo.gy/fyb"
//                );
//            """.trimMargin(),
//            """
//                INSERT INTO ${TableName.POSTS}
//                    (${Column.AUTHOR}, ${Column.PUBLISHED}, ${Column.CONTENT}) VALUES (
//                    "Нетология. Университет интернет-профессий",
//                    "03 июля в 15:17",
//                    "Знаний хватит на всех: на следующей неделе разбираемся с.."
//                );
//            """.trimIndent(),
//            """
//                INSERT INTO ${TableName.POSTS}
//                    (${Column.AUTHOR}, ${Column.PUBLISHED}, ${Column.CONTENT}, ${Column.VIDEO})
//                    VALUES (
//                    "Нетология. Университет интернет-профессий",
//                    "16 июля в 18:20",
//                    "Пост с иллюстрацией в медиа",
//                    "https://www.youtube.com/watch?v=WhWc3b3KhnY"
//                );
//            """.trimIndent()
//        )
//
//        object TableName {
//            const val POSTS = "posts"
//        }
//
//        object Column {
//            const val ID = "id"
//            const val AUTHOR = "author"
//            const val AUTHOR_AVATAR = "authorAvatar"
//            const val PUBLISHED = "published"
//            const val CONTENT = "content"
//            const val LIKED_BY_ME = "likedByMe"
//            const val LIKES = "likes"
//            const val SHARES = "shares"
//            const val VIEWS = "views"
//            const val VIDEO = "video"
//            val ALL_COLUMNS = arrayOf(
//                ID,
//                AUTHOR,
//                AUTHOR_AVATAR,
//                PUBLISHED,
//                CONTENT,
//                LIKED_BY_ME,
//                LIKES,
//                SHARES,
//                VIEWS,
//                VIDEO
//            )
//        }
//    }
//}
package ru.netology.nmedia.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.domain.dto.Push
import javax.inject.Inject
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()

    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.data.values.forEach {
            checkPush(
                appAuth.authStateFlow.value.id,
                gson.fromJson(it, Push::class.java)
            )
        }
    }

    override fun onNewToken(token: String) {
        appAuth.sendPushToken(token)
    }

    private fun handleLike(content: Like) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.userName,
                    content.postAuthor,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private fun handleNewPost(content: NewPost) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentText(
                getString(
                    R.string.notification_new_post_short_content,
                    content.content.slice(0..25)
                )
            )
            .setContentTitle(
                getString(
                    R.string.notification_new_post,
                    content.userName
                )
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        content.content,
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private fun checkPush(id: Long, push: Push) {
        if ((push.recipientId == id) || (push.recipientId == null)) {
            handlePush(push)
        } else if (
            ((push.recipientId == 0L) && (id != 0L)) ||
            ((push.recipientId != 0L) && (push.recipientId != id))
        ) {
            appAuth.sendPushToken()
        } else {
            throw RuntimeException("Unaccounted combination")
        }
    }

    private fun handlePush(push: Push) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(if (push.recipientId == null) "Broadcast" else "To you")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentText(push.content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }
}
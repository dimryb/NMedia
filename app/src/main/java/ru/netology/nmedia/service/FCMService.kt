package ru.netology.nmedia.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.data.api.PostsApi
import ru.netology.nmedia.domain.dto.Push
import ru.netology.nmedia.domain.dto.PushToken
import ru.netology.nmedia.workers.SendPushTokenWorker
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()

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
                AppAuth.getInstance().authStateFlow.value.id,
                gson.fromJson(it, Push::class.java)
            )
        }
    }

    override fun onNewToken(token: String) {
        AppAuth.getInstance().sendPushToken(token)
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
        if ((push.recipientId == id)||(push.recipientId == null)){
            handlePush(push)
        } else if (
            ((push.recipientId == 0L) && (id != 0L)) ||
            ((push.recipientId != 0L) && (push.recipientId != id))
        ) {
            AppAuth.getInstance().sendPushToken()
        } else {
            throw RuntimeException("Unaccounted combination")
        }
    }

    private fun handlePush(push: Push) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(if(push.recipientId == null) "Broadcast" else "To you")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentText(push.content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }
}
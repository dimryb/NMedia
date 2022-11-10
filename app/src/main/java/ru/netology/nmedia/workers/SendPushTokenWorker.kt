package ru.netology.nmedia.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.data.api.ApiService
import ru.netology.nmedia.domain.dto.PushToken

class SendPushTokenWorker(
    @ApplicationContext
    private val context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters) {

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun getApiService(): ApiService
    }

    override suspend fun doWork(): Result =
        try {
            val pushToken = PushToken(
                inputData.getString(TOKEN_KEY) ?: FirebaseMessaging.getInstance().token.await()
            )
            val entryPoint =
            EntryPointAccessors.fromApplication(context, AppAuthEntryPoint::class.java)
            entryPoint.getApiService().sendPushToken(pushToken)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }

    companion object {
        const val TOKEN_KEY = "TOKEN_KEY"
    }

}
package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import androidx.work.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nmedia.domain.dto.AuthState
import ru.netology.nmedia.workers.SendPushTokenWorker

class AppAuth (context: Context) {

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    private val _authStateFlow: MutableStateFlow<AuthState>

    private val workManager: WorkManager = WorkManager.getInstance(context)

    init {
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getLong(ID_KEY, 0L)

        if (token == null || id == 0L) {
            _authStateFlow = MutableStateFlow(AuthState())
            with(prefs.edit()) {
                clear()
                apply()
            }
        } else {
            _authStateFlow = MutableStateFlow(AuthState(id, token))
        }
    }

    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        prefs.edit {
            remove(TOKEN_KEY)
            remove(ID_KEY)
        }
        sendPushToken()
    }

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authStateFlow.value = AuthState(id, token)
        prefs.edit {
            putString(TOKEN_KEY, token)
            putLong(ID_KEY, id)
        }
        sendPushToken()
    }

    fun sendPushToken(token: String? = null) {
        val data = workDataOf(SendPushTokenWorker.TOKEN_KEY to token)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = OneTimeWorkRequestBuilder<SendPushTokenWorker>()
            .setInputData(data)
            .setConstraints(constraints)
            .build()
        workManager.enqueue(request)
    }

    companion object {
        private const val ID_KEY = "ID_KEY"
        private const val TOKEN_KEY = "TOKEN_KEY"
    }
}
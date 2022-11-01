package ru.netology.nmedia.presentation.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.data.db.AppDb
import ru.netology.nmedia.data.repository.AuthRepository
import ru.netology.nmedia.data.repository.AuthRepositoryImpl
import ru.netology.nmedia.data.repository.PostRepository
import ru.netology.nmedia.data.repository.PostRepositoryImpl
import ru.netology.nmedia.domain.dto.Token
import ru.netology.nmedia.presentation.model.FeedModelState

class AuthViewModel : ViewModel() {

    val data: LiveData<Token?> = AppAuth.getInstance().data.asLiveData(Dispatchers.Default)

    val authorized: Boolean
        get() = AppAuth.getInstance().data.value?.token != null

    private val _token = MutableLiveData<Token>()
    val token: LiveData<Token>
        get() = _token

    private val repository: AuthRepository =
        AuthRepositoryImpl()

    fun updateUser(login: String, pass: String) {
        println("Sign In: Login: $login, Password: $pass ")
        viewModelScope.launch {
            try {
                _token.value = repository.updateUser(login, pass)
            } catch (e: Exception) {
                TODO("Exception updateUser")
            }
        }
    }
}
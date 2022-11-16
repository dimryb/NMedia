package ru.netology.nmedia.presentation.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.data.repository.AuthRepository
import ru.netology.nmedia.domain.dto.AuthState
import ru.netology.nmedia.domain.dto.Token
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val appAuth: AppAuth,
) : ViewModel() {

    val data: LiveData<AuthState> = appAuth
        .authStateFlow
        .asLiveData(Dispatchers.Default)

    val authorized: Boolean
        get() = appAuth.authStateFlow.value.token != null

    private val _token = MutableLiveData<Token>()
    val token: LiveData<Token>
        get() = _token

    private val _loginError = MutableLiveData<Unit>()
    val loginError: LiveData<Unit>
        get() = _loginError

    private val _registerError = MutableLiveData<Unit>()
    val registerError: LiveData<Unit>
        get() = _registerError

    private val _signIn = MutableLiveData<Unit>()
    val signIn: LiveData<Unit>
        get() = _signIn

    private val _signUp = MutableLiveData<Unit>()
    val signUp: LiveData<Unit>
        get() = _signUp

    private val _signOutAsk = MutableLiveData<Boolean>()
    val signOutAsk: LiveData<Boolean>
        get() = _signOutAsk

    var signOutAskMode = false

    fun signIn() {
        _signIn.value = Unit
    }

    fun signOut() {
        println("Sign Out")
        _signOutAsk.value = false
        appAuth.removeAuth()
    }

    fun signOutAsk() {
        if (signOutAskMode) {
            _signOutAsk.value = true
        } else {
            signOut()
        }
    }

    fun signUp() {
        _signUp.value = Unit
    }

    fun updateUser(login: String, pass: String) {
        println("Sign In: Login: $login, Password: $pass ")
        viewModelScope.launch {
            try {
                _token.value = repository.authUser(login, pass)
            } catch (e: Exception) {
                _loginError.value = Unit
            }
        }
    }

    fun registerUser(login: String, pass: String, repeatPass: String, name: String) {
        if (pass != repeatPass) {
            _registerError.value = Unit
            return
        }
        println("Sign Up: Login: $login, Password: $pass, Name: $name")
        viewModelScope.launch {
            try {
                _token.value = repository.registerUser(login, pass, name)
            } catch (e: Exception) {
                _registerError.value = Unit
            }
        }
    }
}
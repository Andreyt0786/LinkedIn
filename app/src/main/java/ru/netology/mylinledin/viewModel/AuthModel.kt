package ru.netology.mylinledin.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import ru.netology.mylinledin.auth.AppAuth
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth
) : ViewModel() {

    val authLiveData = appAuth.authStateFlow.asLiveData(Dispatchers.Default)

    val isAuthorized: Boolean
        get() = appAuth.authStateFlow.value.token != null

}
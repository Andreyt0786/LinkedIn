package ru.netology.mylinledin.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.mylinledin.auth.AppAuth
import ru.netology.mylinledin.error.ApiError
import ru.netology.mylinledin.model.AuthModel
import ru.netology.mylinledin.model.AuthModelState
import ru.netology.mylinledin.model.RegisterModelState
import ru.netology.mylinledin.repository.di.postRepository.PostRepository
import ru.netology.mylinledin.util.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class IdenticViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    private val _tokenServer = SingleLiveEvent<AuthModelState>()
    val tokenServer: LiveData<AuthModelState>
        get() = _tokenServer

    private val _newTokenServer = SingleLiveEvent<RegisterModelState>()
    val newTokenServer: LiveData<RegisterModelState>
        get() = _newTokenServer

    fun getIdToken(login: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.getToken(login, password)
                response.token?.let {
                    appAuth.setUser(AuthModel(response.id, response.token))
                    _tokenServer.value = AuthModelState(firstView = false, complete = true)
                }
            } catch (e: ApiError) {
                _tokenServer.value = AuthModelState(firstView = false, errorApi = true)
            } catch (e: Exception) {
                _tokenServer.value = AuthModelState(firstView = false, error = true)
            }
        }
    }

    fun getNewUser(login: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                val response = repository.newUser(login, password, name)
                response.token?.let {
                    appAuth.setUser(AuthModel(response.id, response.token))
                    _newTokenServer.value = RegisterModelState(firstView = false, complete = true)
                }
            } catch (e: ApiError) {
                _newTokenServer.value = RegisterModelState(firstView = false, errorApi = true)
            } catch (e: Exception) {
                _newTokenServer.value = RegisterModelState(firstView = false, error = true)
            }
        }
    }
}



package ru.netology.mylinledin.auth

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.mylinledin.model.AuthModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context,

    ) {//inject  создает и предоставляет класс в другие классы

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _authStateFlow: MutableStateFlow<AuthModel>


    init {
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getInt(ID_KEY, 0)

        if (id == 0 || token == null) {
            prefs.edit { clear() }
            _authStateFlow = MutableStateFlow(AuthModel())
        } else {
            _authStateFlow = MutableStateFlow(AuthModel(id, token))
        }
    }

    val authStateFlow = _authStateFlow.asStateFlow()

    fun setUser(user: AuthModel) {
        _authStateFlow.value = user
        prefs.edit {
            putInt(ID_KEY, user.id)
            putString(TOKEN_KEY, user.token)
        }
    }

    fun removeUser() {
        _authStateFlow.value = AuthModel()
        prefs.edit { clear() }
    }

    companion object {
        private const val ID_KEY = "ID_KEY"
        private const val TOKEN_KEY = "TOKEN_KEY"
    }
}
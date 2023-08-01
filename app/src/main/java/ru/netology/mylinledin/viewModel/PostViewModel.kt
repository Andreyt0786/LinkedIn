package ru.netology.mylinledin.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.mylinledin.auth.AppAuth
import ru.netology.mylinledin.dto.posts.Post
import ru.netology.mylinledin.model.FeedModelState
import ru.netology.mylinledin.model.MediaModel
import ru.netology.mylinledin.repository.di.PostRepository.PostRepository
import ru.netology.mylinledin.util.SingleLiveEvent
import javax.inject.Inject


private val empty = Post(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = null,
    authorJob = null,
    content = "",
    published = "",
    //coords = null,
    link = null,
   // likeOwnerIds = emptyList<Int>(),
   // mentionIds = emptyList<Int>(),
    mentiondMe = false,
    likedByMe = false,
    attachment = null,
    ownedByMe = false,
)

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    appAuth: AppAuth,
) : ViewModel() {


    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state
    val data: Flow<PagingData<Post>> = appAuth.authStateFlow.flatMapLatest { (id, _) ->
        repository.data.map { posts ->
            posts.map { it.copy(ownedByMe = it.authorId == id) }
        }
    }.flowOn(Dispatchers.Default)


    private val _mediaState = MutableLiveData<MediaModel?>()
    val mediaState: LiveData<MediaModel?>
        get() = _mediaState

    val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated


    init {
        loadPosts()
    }



    fun loadPosts() = viewModelScope.launch {
        try {
            _state.value = FeedModelState(loading = true)
            //repository.getAll()
            _state.value = FeedModelState()
        } catch (e: Exception) {
            _state.value = FeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _state.value = FeedModelState(refreshing = true)
            val posts = repository.getAll()
            _state.value = FeedModelState()
        } catch (e: Exception) {
            _state.value = FeedModelState(error = true)
        }
    }

    fun changeMedia(mediaModel: MediaModel?) {
        _mediaState.value = mediaModel
    }


    fun save() {
        edited.value?.let { post ->
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    _mediaState.value?.let { mediaModel ->
                       repository.saveWithAttachment(mediaModel.file, post)
                    } ?: repository.save(post)
                    _state.value = FeedModelState()
                } catch (e: Exception) {
                    _state.value = FeedModelState(error = true)
                }

            }
            _mediaState.value = null
            edited.value = empty
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(post: Post) {
        viewModelScope.launch {
            repository.likeById(post)
            _state.value = FeedModelState()
        }
    }

        fun removeById(id: Int) {
            viewModelScope.launch {
                repository.removeById(id)
            }
        }
    }

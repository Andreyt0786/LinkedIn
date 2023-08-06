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
import ru.netology.mylinledin.dto.event.Event
import ru.netology.mylinledin.model.EventFeedModelState
import ru.netology.mylinledin.model.MediaModel
import ru.netology.mylinledin.repository.di.event.EventRepository
import ru.netology.mylinledin.util.SingleLiveEvent
import javax.inject.Inject

private val empty = Event(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = null,
    authorJob = null,
    content = "",
    datetime = "",
    published = "",
    //type = null,
    link = null,
//  val coords: Coordinates?,
//val likeOwnerIds:List<Int>, узнать и добавить
// val mentionIds:List<Int>,
    likedByMe = false,
    participantedByMe = false,
    attachment = null,
    ownedByMe = false,
)

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository,
    appAuth: AppAuth,
) : ViewModel() {


    private val _state = MutableLiveData(EventFeedModelState())
    val state: LiveData<EventFeedModelState>
        get() = _state
    val data: Flow<PagingData<Event>> = appAuth.authStateFlow.flatMapLatest { (id, _) ->
        repository.dataEvent.map { events ->
            events.map { it.copy(ownedByMe = it.authorId == id) }
        }
    }.flowOn(Dispatchers.Default)


    private val _mediaState = MutableLiveData<MediaModel?>()
    val mediaState: LiveData<MediaModel?>
        get() = _mediaState

    val edited = MutableLiveData(empty)

    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated


    init {
        loadEvents()
    }


    fun loadEvents() = viewModelScope.launch {
        try {
            _state.value = EventFeedModelState(loading = true)
            //repository.getAll()
            _state.value = EventFeedModelState()
        } catch (e: Exception) {
            _state.value = EventFeedModelState(error = true)
        }
    }

    /*fun refreshPosts() = viewModelScope.launch {
        try {
            _state.value = FeedModelState(refreshing = true)
            val posts = repository.getAll()
            _state.value = FeedModelState()
        } catch (e: Exception) {
            _state.value = FeedModelState(error = true)
        }
    }*/

    fun changeMediaEvents(mediaModel: MediaModel?) {
        _mediaState.value = mediaModel
    }


    fun saveEvents() {
        edited.value?.let { event ->
            _eventCreated.value = Unit
            viewModelScope.launch {
                try {
                    _mediaState.value?.let { mediaModel ->
                        repository.saveWithAttachmentEvents(mediaModel.file, event)
                    } ?: repository.saveEvents(event)
                    _state.value = EventFeedModelState()
                } catch (e: Exception) {
                    _state.value = EventFeedModelState(error = true)
                }

            }
            _mediaState.value = null
            edited.value = empty
        }
    }

    fun editEvent(event: Event) {
        edited.value = event
    }

    fun changeContentEvent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun changeJobEvent(jobAuthor: String) {
        val text = jobAuthor.trim()
        if (edited.value?.authorJob == text || edited.value?.authorJob =="") {
            return
        }
        edited.value = edited.value?.copy(authorJob = text)
    }

    fun changeLinkEvent(linkAuthor: String) {
        val text = linkAuthor.trim()
        if (edited.value?.link == text || edited.value?.link =="") {
            return
        }
        edited.value = edited.value?.copy(link = text)
    }



    fun likeByIdEvent(event: Event) {
        viewModelScope.launch {
            repository.likeByIdEvents(event)
            _state.value = EventFeedModelState()
        }
    }

    fun removeByIdEvent(id: Int) {
        viewModelScope.launch {
            repository.removeByIdEvents(id)
        }
    }
}
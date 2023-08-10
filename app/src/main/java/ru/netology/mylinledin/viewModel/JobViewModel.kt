package ru.netology.mylinledin.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.mylinledin.auth.AppAuth
import ru.netology.mylinledin.dto.Job.Job
import ru.netology.mylinledin.dto.posts.Post
import ru.netology.mylinledin.model.JobModel
import ru.netology.mylinledin.model.JobModelState
import ru.netology.mylinledin.model.MediaModel
import ru.netology.mylinledin.model.WallModelState
import ru.netology.mylinledin.model.WallPosts
import ru.netology.mylinledin.repository.di.job.JobRepository
import ru.netology.mylinledin.repository.di.wall.WallRepository
import ru.netology.mylinledin.util.SingleLiveEvent
import javax.inject.Inject

private val empty = Job(
    id = 0,
    name = "",
    position = "",
    start = "",
    finish = null,
    link = null,
)

@HiltViewModel
class JobViewModel @Inject constructor(
    private val repository: JobRepository,
    appAuth: AppAuth,
) : ViewModel() {


    private val _state = MutableLiveData(JobModelState())
    val state: LiveData<JobModelState>
        get() = _state

    val dataMyJob: LiveData<JobModel> =
        repository.dataJob
            .map { jobs ->
              JobModel(
                    jobs.map { it.copy(ownedByMe = true) },
                    jobs.isEmpty()
                )
    }.asLiveData(Dispatchers.Default)


    val dataUserJob: LiveData<JobModel> = repository.dataJob.map { jobs ->
        JobModel(
            jobs,
            jobs.isEmpty()
        )
    }.asLiveData(Dispatchers.Default)


    val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated


    fun loadUserJobs(id: Int) = viewModelScope.launch {
        try {
            _state.value = JobModelState(loading = true)
            repository.getUserJob(id)
            _state.value = JobModelState()
        } catch (e: Exception) {
            _state.value = JobModelState(error = true)
        }
    }

    fun loadMyJobs() = viewModelScope.launch {
        try {
            _state.value = JobModelState(loading = true)
            repository.getMyJob()
            _state.value = JobModelState()
        } catch (e: Exception) {
            _state.value = JobModelState(error = true)
        }
    }

    /* fun refreshPosts(id: Int) = viewModelScope.launch {
         try {
             _state.value = WallModelState(loading = true)
             repository.getAllForWall(id)
             _state.value = WallModelState()
         } catch (e: Exception) {
             _state.value = WallModelState(error = true)
         }
     }*/

    fun save() {
        edited.value?.let { post ->
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    repository.save(post)
                    _state.value = JobModelState()
                } catch (e: Exception) {
                    _state.value = JobModelState(error = true)
                }

            }
            edited.value = empty
        }
    }

    fun changeJob(nameJob: String, position: String,startJob : String) {
        val text = nameJob.trim()
        val newPosition = position.trim()
        val newStart = startJob.trim()
        edited.value = edited.value?.copy(name = text, position =  newPosition, start = newStart)
    }

    fun changeFinish(finish: String) {
        val text = finish.trim()
        if (finish.isEmpty()) {
            return
        }
        edited.value = edited.value?.copy(finish = text)
    }

    fun changeLink(link: String) {
        val text = link.trim()
        if (link.isEmpty()) {
            return
        }
        edited.value = edited.value?.copy(link = text)
    }

    fun removeById(id: Int) {
        viewModelScope.launch {
            repository.removeById(id)
        }
    }
}
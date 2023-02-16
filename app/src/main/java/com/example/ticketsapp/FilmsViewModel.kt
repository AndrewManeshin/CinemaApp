package com.example.ticketsapp

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FilmsViewModel(
    private val communications: FilmsCommunications,
    private val interactor: FilmsInteractor,
    private val dispatchers: DispatchersList
) : ViewModel() {

    fun init(isFirstRun: Boolean) {
        if (isFirstRun) {
            communications.map(FilmsResult.Progress)
            viewModelScope.launch(dispatchers.io()) {
                val result = interactor.fetchFilms()
                communications.map(result)
            }
        }
    }
}

interface FilmsCommunications {

    fun observe(owner: LifecycleOwner, observer: Observer<FilmsResult>)

    fun map(filmsResult: FilmsResult)
}

interface FilmsInteractor {

    suspend fun fetchFilms(): FilmsResult
}
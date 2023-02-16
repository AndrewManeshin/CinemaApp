package com.example.ticketsapp

sealed class FilmsResult() {

    data class Success(private val filmsUi: List<FilmUi>) : FilmsResult()

    data class Error(private val message: String) : FilmsResult()

    object Empty : FilmsResult()
    object Progress : FilmsResult()
}

data class FilmUi(
    private val id: Long,
    private val name: String,
    private val year: String,
    private val country: String,
    private val genre: String
)
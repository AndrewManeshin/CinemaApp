package com.example.ticketsapp

import android.view.View
import org.junit.Test
import org.junit.Assert.*

class FilmsViewModelTest {

    @Test
    fun `test init and re-init`() {
        val communications = TestFilmsCommunications.Base()
        val interactor = TestFilmsInteractor.Base(
            FilmsResult.Success(
                filmsUi = listOf(
                    FilmsUi(
                        id = 1, name = "avatar", year = "2022", country = "USA", genre = "action"
                    )
                )
            )
        )
        val viewModel = FilmsViewModel(
            communications = communications,
            interactor = interactor
        )

        viewModel.init(isFirstRun = true)

        assertEquals(View.VISIBLE, communications.showProgressCalledList[0])
        assertEquals(View.GONE, communications.showProgressCalledList[1])
        assertEquals(2, communications.showProgressCalledList.size)
        assertEquals(1, interactor.fetchFilmsCalledCount)
        assertEquals(
            FilmsResult.Success(
                filmsUi = listOf(
                    FilmsUi(
                        id = 1, name = "avatar", year = "2022", country = "USA", genre = "action"
                    )
                )
            ),
            communications.filmsResultList[0]
        )

        viewModel.init(isFirstRun = false)

        assertEquals(2, communications.showProgressCalledList.size)
        assertEquals(1, interactor.fetchFilmsCalledCount)
        assertEquals(
            FilmsResult.Success(
                filmsUi = listOf(
                    FilmsUi(
                        id = 1, name = "avatar", year = "2022", country = "USA", genre = "action"
                    )
                )
            ),
            communications.filmsResultList[0]
        )
    }
}

private class TestFilmsCommunications : FilmsCommunications {

    val filmsResultList = mutableListOf<FilmsResult>()
    val showProgressCalledList = mutableListOf<Int>()
    var showFilmsResultCalledCount = 0

    override fun showFilmsResult(filmsResult: FilmsResult) {
        showFilmsResultCalledCount++
        filmsResultList.add(filmsResult)
    }

    override fun showProgress(view: Int) {
        showProgressCalledList.add(view)
    }
}

private class TestFilmsInteractor(
    private val expectedResult: FilmsResult
) : FilmsInteractor {

    var fetchFilmsCalledCount = 0

    override fun fetchFilms(): FilmsResult {
        fetchFilmsCalledCount++
        return expectedResult
    }
}
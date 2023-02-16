package com.example.ticketsapp

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class FilmsViewModelTest {

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun init() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @Test
    fun `test init and re-init`() {
        val communications = TestFilmsCommunications()
        val interactor = TestFilmsInteractor(
            FilmsResult.Success(
                filmsUi = listOf(
                    FilmUi(
                        id = 1,
                        name = "avatar",
                        year = "2022",
                        country = "USA",
                        genre = "action"
                    )
                )
            )
        )
        val viewModel = FilmsViewModel(
            communications = communications,
            interactor = interactor,
            dispatchers = TestDispatchersList()
        )

        viewModel.init(isFirstRun = true)

        assertEquals(true, communications.filmsResultList[0] is FilmsResult.Progress)
        assertEquals(1, interactor.fetchFilmsCalledCount)
        assertEquals(
            FilmsResult.Success(
                filmsUi = listOf(
                    FilmUi(
                        id = 1,
                        name = "avatar",
                        year = "2022",
                        country = "USA",
                        genre = "action"
                    )
                )
            ),
            communications.filmsResultList[1]
        )

        viewModel.init(isFirstRun = false)

        assertEquals(1, interactor.fetchFilmsCalledCount)
        assertEquals(2, communications.filmsResultList.size)
    }
}

private class TestFilmsCommunications : FilmsCommunications {

    val filmsResultList = mutableListOf<FilmsResult>()
    var showFilmsResultCalledCount = 0

    override fun map(filmsResult: FilmsResult) {
        showFilmsResultCalledCount++
        filmsResultList.add(filmsResult)
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<FilmsResult>) = Unit
}

private class TestFilmsInteractor(
    private val expectedResult: FilmsResult
) : FilmsInteractor {

    var fetchFilmsCalledCount = 0

    override suspend fun fetchFilms(): FilmsResult {
        fetchFilmsCalledCount++
        return expectedResult
    }
}

private class TestDispatchersList : DispatchersList {
    override fun io() = TestCoroutineDispatcher()
    override fun ui() = TestCoroutineDispatcher()
}
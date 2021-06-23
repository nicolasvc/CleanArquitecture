package com.example.cleanarquitecture.bussiness.interactors

import com.example.cleanarquitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.cleanarquitecture.business.domain.model.Note
import com.example.cleanarquitecture.business.domain.model.NoteFactory
import com.example.cleanarquitecture.business.domain.state.DataState
import com.example.cleanarquitecture.business.interactors.notelist.SearchNotes
import com.example.cleanarquitecture.business.interactors.notelist.SearchNotes.Companion.SEARCH_NOTES_SUCCESS
import com.example.cleanarquitecture.bussiness.di.DependencyContainer
import com.example.cleanarquitecture.framework.datasource.database.ORDER_BY_ASC_DATE_UPDATED
import com.example.cleanarquitecture.framework.presentation.notelist.state.NoteListStateEvent
import com.example.cleanarquitecture.framework.presentation.notelist.state.NoteListViewState
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/*
Test cases:
1. blankQuery_success_confirmNotesRetrieved()
    a) query with some default search options
    b) listen for SEARCH_NOTES_SUCCESS emitted from flow
    c) confirm notes were retrieved
    d) confirm notes in cache match with notes that were retrieved
2. randomQuery_success_confirmNoResults()
    a) query with something that will yield no results
    b) listen for SEARCH_NOTES_NO_MATCHING_RESULTS emitted from flow
    c) confirm nothing was retrieved
    d) confirm there is notes in the cache
3. searchNotes_fail_confirmNoResults()
    a) force an exception to be thrown
    b) listen for CACHE_ERROR_UNKNOWN emitted from flow
    c) confirm nothing was retrieved
    d) confirm there is notes in the cache
 */
@InternalCoroutinesApi
class SearchNoteTest {

    //system in test
    private val searchNotes: SearchNotes

    // dependencies
    private val dependencyContainer: DependencyContainer
    private val noteCacheDataSource: NoteCacheDataSource
    private val noteFactory: NoteFactory

    init {
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteFactory = dependencyContainer.noteFactory
        searchNotes = SearchNotes(
            noteCacheDataSource = noteCacheDataSource
        )
    }

    @Test
    fun blankQuery_success_confirmNotesRetrieved() = runBlocking {

        val query = ""
        var results: ArrayList<Note>? = null
        searchNotes.searchNotes(
            query = query,
            filterAndOrder = ORDER_BY_ASC_DATE_UPDATED,
            page = 1,
            stateEvent = NoteListStateEvent.SearchNotesEvent()
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(
                    value?.stateMessage?.response?.message,
                    SEARCH_NOTES_SUCCESS
                )
                value?.data?.noteList?.let { list ->
                    results = ArrayList(list)
                }
            }
        })

        //confirm notes were retrieved
        assertTrue { results != null }

        //confimr notes in cache match with notes that were retrivede
        val notesInCache = noteCacheDataSource.searchNotes(
            query = query,
            filterAndOrder = ORDER_BY_ASC_DATE_UPDATED,
            page = 1
        )
        assertTrue { results?.containsAll(notesInCache) ?: false }

    }

}
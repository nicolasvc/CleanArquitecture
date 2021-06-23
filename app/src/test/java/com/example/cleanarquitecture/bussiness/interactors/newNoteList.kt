package com.example.cleanarquitecture.bussiness.interactors

import com.example.cleanarquitecture.business.data.cache.CacheErrors
import com.example.cleanarquitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.cleanarquitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.example.cleanarquitecture.business.domain.model.NoteFactory
import com.example.cleanarquitecture.business.domain.state.DataState
import com.example.cleanarquitecture.business.interactors.notelist.InsertNewNote
import com.example.cleanarquitecture.business.interactors.notelist.InsertNewNote.Companion.INSERT_NOTE_FAILED
import com.example.cleanarquitecture.business.interactors.notelist.InsertNewNote.Companion.INSERT_NOTE_SUCCESS
import com.example.cleanarquitecture.bussiness.data.cache.FORCE_GENERAL_FAILURE
import com.example.cleanarquitecture.bussiness.data.cache.FORCE_NEW_NOTE_EXCEPTION
import com.example.cleanarquitecture.bussiness.di.DependencyContainer
import com.example.cleanarquitecture.framework.presentation.notelist.state.NoteListStateEvent
import com.example.cleanarquitecture.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

/*
Test cases:
1. insertNote_success_confirmNetworkAndCacheUpdated()
    a) insert a new note
    b) listen for INSERT_NOTE_SUCCESS emission from flow
    c) confirm cache was updated with new note
    d) confirm network was updated with new note
2. insertNote_fail_confirmNetworkAndCacheUnchanged()
    a) insert a new note
    b) force a failure (return -1 from db operation)
    c) listen for INSERT_NOTE_FAILED emission from flow
    e) confirm cache was not updated
    e) confirm network was not updated
3. throwException_checkGenericError_confirmNetworkAndCacheUnchanged()
    a) insert a new note
    b) force an exception
    c) listen for CACHE_ERROR_UNKNOWN emission from flow
    e) confirm cache was not updated
    e) confirm network was not updated
 */
@InternalCoroutinesApi
class InsertNewNoteTest {

    // system in test Or use case we wanna test
    private val insertNewNote: InsertNewNote

    // dependencies
    private val dependencyContainer: DependencyContainer
    private val noteCacheDataSource: NoteCacheDataSource
    private val noteNetworkDataSource: NoteNetworkDataSource
    private val noteFactory: NoteFactory

    init {
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteNetworkDataSource = dependencyContainer.noteNetworkDataSource
        noteFactory = dependencyContainer.noteFactory
        insertNewNote = InsertNewNote(
            noteCacheDataSource = noteCacheDataSource,
            noteNetworkDataSource = noteNetworkDataSource,
            noteFactory = noteFactory
        )
    }


    @Test
    fun insertNote_success_confirmNetworkAndCacheUpdated() = runBlocking {

        val newNote = noteFactory.createSingleNote(
            id = null,
            title = UUID.randomUUID().toString()
        )
        insertNewNote.insertNewNote(newNote.id, newNote.title,stateEvent = NoteListStateEvent.InsertNewNoteEvent(title = newNote.title)
        ).collect(object :FlowCollector<DataState<NoteListViewState>?>{
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(
                    value?.stateMessage?.response?.message,
                    INSERT_NOTE_SUCCESS
                )
            }
        })

        //Confirm cache was updated
        val cacheNoteThastWasInserted = noteCacheDataSource.searchNoteById(newNote.id)
        assertTrue { cacheNoteThastWasInserted?.id   == newNote.id }

        //COnfimr network was updated
        val networkNoteThatWasInserted = noteNetworkDataSource.searchNote(newNote)
        assertTrue { networkNoteThatWasInserted?.id   == newNote.id }
    }

    @Test
    fun  insertNote_fail_confirmNetworkAndCacheUnchanged() = runBlocking {
        val newNote = noteFactory.createSingleNote(
            id = FORCE_GENERAL_FAILURE,
            title = UUID.randomUUID().toString()
        )
        insertNewNote.insertNewNote(newNote.id, newNote.title,stateEvent = NoteListStateEvent.InsertNewNoteEvent(title = newNote.title)
        ).collect(object :FlowCollector<DataState<NoteListViewState>?>{
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(
                    value?.stateMessage?.response?.message,
                    INSERT_NOTE_FAILED
                )
            }
        })

        //Confirm cache was not updated
        val cacheNoteWasNotInserted = noteCacheDataSource.searchNoteById(newNote.id)
        assertTrue { cacheNoteWasNotInserted  == null }

        //Confirm network was not updated
        val networkNoteWasNotInserted = noteNetworkDataSource.searchNote(newNote)
        assertTrue { networkNoteWasNotInserted == null }
    }


    @Test
    fun throwException_checkGenericError_confirmNetworkAndCacheUnchanged() = runBlocking {

        val newNote = noteFactory.createSingleNote(
            id = FORCE_NEW_NOTE_EXCEPTION,
            title = UUID.randomUUID().toString()
        )
        insertNewNote.insertNewNote(newNote.id, newNote.title,stateEvent = NoteListStateEvent.InsertNewNoteEvent(title = newNote.title)
        ).collect(object :FlowCollector<DataState<NoteListViewState>?>{
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assert(
                    value?.stateMessage?.response?.message?.contains(CacheErrors.CACHE_ERROR_UNKNOWN)?:false
                )
            }
        })

        //Confirm cache was updated
        val cacheNoteThastWasInserted = noteCacheDataSource.searchNoteById(newNote.id)
        assertTrue { cacheNoteThastWasInserted?.id   == newNote.id }

        //COnfimr network was updated
        val networkNoteThatWasInserted = noteNetworkDataSource.searchNote(newNote)
        assertTrue { networkNoteThatWasInserted?.id   == newNote.id }
    }
}
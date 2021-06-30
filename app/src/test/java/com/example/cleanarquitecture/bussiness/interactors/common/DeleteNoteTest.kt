package com.example.cleanarquitecture.bussiness.interactors.common

import com.example.cleanarquitecture.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import com.example.cleanarquitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.cleanarquitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.example.cleanarquitecture.business.domain.model.Note
import com.example.cleanarquitecture.business.domain.model.NoteFactory
import com.example.cleanarquitecture.business.domain.state.DataState
import com.example.cleanarquitecture.business.interactors.common.DeleteNote
import com.example.cleanarquitecture.business.interactors.common.DeleteNote.Companion.DELETE_NOTE_FAILURE
import com.example.cleanarquitecture.business.interactors.common.DeleteNote.Companion.DELETE_NOTE_SUCCESS
import com.example.cleanarquitecture.bussiness.data.cache.FORCE_DELETE_NOTE_EXCEPTION
import com.example.cleanarquitecture.bussiness.di.DependencyContainer
import com.example.cleanarquitecture.framework.presentation.notelist.state.NoteListStateEvent.*
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
1. deleteNote_success_confirmNetworkUpdated()
    a) delete a note
    b) check for success message from flow emission
    c) confirm note was deleted from "notes" node in network
    d) confirm note was added to "deletes" node in network
2. deleteNote_fail_confirmNetworkUnchanged()
    a) attempt to delete a note, fail since does not exist
    b) check for failure message from flow emission
    c) confirm network was not changed
3. throwException_checkGenericError_confirmNetworkUnchanged()
    a) attempt to delete a note, force an exception to throw
    b) check for failure message from flow emission
    c) confirm network was not changed
 */
@InternalCoroutinesApi
class DeleteNoteTest {

    // system in test Or use case we wanna test
    private val deleteNote: DeleteNote<NoteListViewState>

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
        deleteNote = DeleteNote(
            noteCacheDataSource = noteCacheDataSource,
            noteNetworkDataSource = noteNetworkDataSource
        )
    }


    @Test
    fun deleteNote_success_confirmNetworkUpdated() = runBlocking {

        val noteToDelete = noteCacheDataSource.searchNotes(
            query = "",
            filterAndOrder = "",
            page = 1
        )[0]

        deleteNote.deleteNote(
            note = noteToDelete,
            stateEvent = DeleteNoteEvent(noteToDelete)
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(
                    value?.stateMessage?.response?.message,
                    DELETE_NOTE_SUCCESS
                )
            }
        })

        //confirm was delete from 'notes node'
        val wasNoteDelete = !noteNetworkDataSource.getAllNotes().contains(noteToDelete)
        assertTrue { wasNoteDelete }


        //confirm was inserted into 'deletes' node
        val deleteInsertNodeDelete = noteNetworkDataSource.getDeletedNotes().contains(noteToDelete)
        assertTrue { deleteInsertNodeDelete }
    }

    @Test
    fun deleteNote_fail_confirmNetworkUnchanged() = runBlocking {

        val noteToDelete = Note(
            id = UUID.randomUUID().toString(),
            title = "YOLO",
            body = "YOLOX2",
            created_at = "YOLOX2",
            update_at = "YOLOX2"
        )

        deleteNote.deleteNote(
            note = noteToDelete,
            stateEvent = DeleteNoteEvent(noteToDelete)
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(
                    value?.stateMessage?.response?.message,
                    DELETE_NOTE_FAILURE
                )
            }
        })

        //confirm was NOT delete from 'notes node'
        val notes = noteNetworkDataSource.getAllNotes()
        val numNotesInCache = noteCacheDataSource.getNumNotes()
        assertTrue { numNotesInCache == notes.size }

        //confirm was NOT inserted into 'deletes' node
        val deleteInsertNodeDelete = !noteNetworkDataSource.getAllNotes().contains(noteToDelete)
        assertTrue { deleteInsertNodeDelete }
    }


    @Test
     fun throwException_checkGenericError_confirmNetworkUnchanged() = runBlocking {

        val noteToDelete = Note(
            id = FORCE_DELETE_NOTE_EXCEPTION,
            title = "YOLO",
            body = "YOLOX2",
            created_at = "YOLOX2",
            update_at = "YOLOX2"
        )

        deleteNote.deleteNote(
            note = noteToDelete,
            stateEvent = DeleteNoteEvent(noteToDelete)
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assert( value?.stateMessage?.response?.message?.contains(CACHE_ERROR_UNKNOWN) ?: false)
            }
        })

        //confirm was NOT delete from 'notes node'
        val notes = noteNetworkDataSource.getAllNotes()
        val numNotesInCache = noteCacheDataSource.getNumNotes()
        assertTrue { numNotesInCache == notes.size }

        //confirm was NOT inserted into 'deletes' node
        val deleteInsertNodeDelete = !noteNetworkDataSource.getAllNotes().contains(noteToDelete)
        assertTrue { deleteInsertNodeDelete }


    }

}
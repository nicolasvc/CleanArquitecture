package com.example.cleanarquitecture.business.interactors.notelist

import android.os.Message
import com.example.cleanarquitecture.business.data.cache.CacheResponseHandler
import com.example.cleanarquitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.cleanarquitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.example.cleanarquitecture.business.data.util.safeApiCall
import com.example.cleanarquitecture.business.data.util.safeCacheCall
import com.example.cleanarquitecture.business.domain.model.Note
import com.example.cleanarquitecture.business.domain.state.*
import com.example.cleanarquitecture.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteMultipleNotes(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource
) {

    private var onDeleteError: Boolean = false

    fun deleteNotes(
        notes: List<Note>,
        stateEvent: StateEvent
    ): Flow<DataState<NoteListViewState>?> = flow {

        val succesfulDeletes: ArrayList<Note> = ArrayList()

        for (note in notes) {
            val cacheResult = safeCacheCall(IO) {
                noteCacheDataSource.deleteNote(note.id)
            }

            val response = object : CacheResponseHandler<NoteListViewState, Long>(
                response = cacheResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: Long): DataState<NoteListViewState>? {
                    if (resultObj < 0)
                        onDeleteError = true
                    else
                        succesfulDeletes.add(note)
                    return null
                }
            }.getResult()

            if (response?.stateMessage?.response?.message?.contains(stateEvent.errorInfo()) == true)
                onDeleteError = true
        }

        if(onDeleteError){
            emit(DataState.data<NoteListViewState>(
                response = Response(
                    message = DELETE_NOTES_ERRORS,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Success()
                ),
                data = null,
                stateEvent = stateEvent
            ))
        }else{
            emit(DataState.data<NoteListViewState>(
                response = Response(
                    message = DELETE_NOTES_SUCCESS,
                    uiComponentType = UIComponentType.Toast(),
                    messageType = MessageType.Success()
                ),
                data = null,
                stateEvent = stateEvent
            ))
        }

        updateNetwork(succesfulDeletes)
    }

    private suspend fun updateNetwork(succesfulDeletes: java.util.ArrayList<Note>) {
        for (note in succesfulDeletes){
            //Delete from notes node
            noteNetworkDataSource.deleteNote(note.id)
            //Insert into deletes node
            safeApiCall(IO){
                noteNetworkDataSource.insertDeleteNote(note)
            }

        }
    }

    companion object{
        val DELETE_NOTES_SUCCESS = "Successfully deleted notes."
        val DELETE_NOTES_ERRORS = "Not all the notes you selected were deleted. There was some errors."
        val DELETE_NOTES_YOU_MUST_SELECT = "You haven't selected any notes to delete."
        val DELETE_NOTES_ARE_YOU_SURE = "Are you sure you want to delete these?"
    }


}
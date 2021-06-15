package com.example.cleanarquitecture.business.interactors.notelist

import com.example.cleanarquitecture.business.data.cache.CacheRespondeHandler
import com.example.cleanarquitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.cleanarquitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.example.cleanarquitecture.business.data.util.safeApiCall
import com.example.cleanarquitecture.business.data.util.safeCacheCall
import com.example.cleanarquitecture.business.domain.model.Note
import com.example.cleanarquitecture.business.domain.model.NoteFactory
import com.example.cleanarquitecture.business.domain.state.*
import com.example.cleanarquitecture.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InsertNewNote(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource,
    private val noteFactory: NoteFactory
) {

    fun insertNewNote(
        id: String? = null,
        title: String,
        stateEvent: StateEvent
    ): Flow<DataState<NoteListViewState>> = flow {
        val newNote = noteFactory.createSingleNote(
            id = id,
            title = title,
            body = ""
        )
        val cacheResult = safeCacheCall(Dispatchers.IO) {
            noteCacheDataSource.insertNote(newNote)
        }

        val cacheResponse = object : CacheRespondeHandler<NoteListViewState, Long>(
            response = cacheResult,
            stateEvent = stateEvent
        ) {
            override fun handleSucces(resultObject: Long): DataState<NoteListViewState> {
                return if (resultObject > 0) {
                    val viewState = NoteListViewState(
                        newNote = newNote
                    )
                    DataState.data(
                        response = Response(
                            message = INSERT_NOTE_SUCCESS,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Success()
                        ),
                        data = viewState,
                        stateEvent = stateEvent
                    )
                } else {
                    DataState.data(
                        response = Response(
                            message = INSERT_NOTE_FAILED,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Error()
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                }
            }
        }.getResult()
        emit(cacheResponse)
        updateNetwork(cacheResponse.stateMessage?.response!!.message, newNote)
    }

    private suspend fun updateNetwork(message: String?, newNote: Note) {
        if (message.equals(INSERT_NOTE_SUCCESS)) {
            safeApiCall(Dispatchers.IO){
                noteNetworkDataSource.insertOrUpdateNote(newNote)
            }
        }
    }


    companion object {
        //TODO VOLVER MULTILENGUAJE App
        const val INSERT_NOTE_SUCCESS = "Successfully inserted new note."
        const val INSERT_NOTE_FAILED = "Failed to insert new note"
    }

}
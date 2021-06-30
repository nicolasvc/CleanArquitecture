package com.example.cleanarquitecture.business.interactors.common

import com.example.cleanarquitecture.business.data.cache.CacheResponseHandler
import com.example.cleanarquitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.cleanarquitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.example.cleanarquitecture.business.data.util.safeApiCall
import com.example.cleanarquitecture.business.data.util.safeCacheCall
import com.example.cleanarquitecture.business.domain.model.Note
import com.example.cleanarquitecture.business.domain.state.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class DeleteNote<ViewState>(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource
) {

    fun deleteNote(
        note: Note,
        stateEvent: StateEvent
    ): Flow<DataState<ViewState>?> = flow {

        val cacheResult = safeCacheCall(IO){
            noteCacheDataSource.deleteNote(note.id)
        }

        val response = object: CacheResponseHandler<ViewState, Long>(
            response = cacheResult,
            stateEvent = stateEvent
        ){
            override suspend fun handleSuccess(resultObj: Long): DataState<ViewState>? {
                return if(resultObj > 0){
                    DataState.data(
                        response = Response(
                            message = DELETE_NOTE_SUCCESS,
                            uiComponentType = UIComponentType.None(),
                            messageType = MessageType.Success()
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                }
                else{
                    DataState.data(
                        response = Response(
                            message = DELETE_NOTE_FAILED,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Error()
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                }
            }
        }.getResult()

        emit(response)

        // update network
        if(response?.stateMessage?.response?.message.equals(DELETE_NOTE_SUCCESS)){

            // delete from 'notes' node
            safeApiCall(IO){
                noteNetworkDataSource.deleteNote(note.id)
            }

            // insert into 'deletes' node
            safeApiCall(IO){
                noteNetworkDataSource.insertDeleteNote(note)
            }

        }
    }


    private suspend fun updateNetwork(message:String?, note:Note){
        if(message.equals(DELETE_NOTE_SUCCESS)){

            //Delete form notes node
            safeApiCall(IO){
                noteNetworkDataSource.deleteNote(note.id)
            }
            //Insert into delete node
            safeApiCall(IO){
                noteNetworkDataSource.insertDeleteNote(note)
            }
        }
    }



    companion object {
        const val  DELETE_NOTE_SUCCESS = "Successfully deleted the note"
        const val  DELETE_NOTE_FAILURE = "Failed to deleted deleted the note"
        const val DELETE_NOTE_FAILED = "Failed to delete note."
    }
}
package com.example.cleanarquitecture.bussiness.interactors.notelist

import com.example.cleanarquitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.cleanarquitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.example.cleanarquitecture.business.domain.model.NoteFactory
import com.example.cleanarquitecture.business.domain.state.DataState
import com.example.cleanarquitecture.business.interactors.notelist.GetNumNotes
import com.example.cleanarquitecture.bussiness.di.DependencyContainer
import com.example.cleanarquitecture.framework.presentation.notelist.state.NoteListStateEvent
import com.example.cleanarquitecture.framework.presentation.notelist.state.NoteListStateEvent.GetNumNotesInCacheEvent
import com.example.cleanarquitecture.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


/**
 * 1. getNumNotes_success_confirmCorrect()
 *  a) get the number of notes in cache
 *  b) listen for GET_NUM_NOTES_SUCCESS from flow emission
 *  c) compare with the number of notes in the fake data set
 * */

@InternalCoroutinesApi
class GetNumNotesTest {


    //System in test
    private val getNumNotes:GetNumNotes


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
        getNumNotes = GetNumNotes(
            noteCacheDataSource = noteCacheDataSource
        )
    }

    @Test
    fun getNumNotes_success_confirmCorrect() = runBlocking {

        var numNotes = 0
        getNumNotes.getNumNotes(
            stateEvent = GetNumNotesInCacheEvent()
        ).collect(object : FlowCollector<DataState<NoteListViewState>?>{
            override suspend fun emit(value: DataState<NoteListViewState>?) {
               Assertions.assertEquals(
               value?.stateMessage?.response?.message,
                   GetNumNotes.GET_NUM_NOTES_SUCCESS
               )
                numNotes = value?.data?.numNotesInCache?: 0
            }
        })
        val actualNumNotesInCache = noteCacheDataSource.getNumNotes()
        assertTrue{ actualNumNotesInCache == numNotes}

    }

}
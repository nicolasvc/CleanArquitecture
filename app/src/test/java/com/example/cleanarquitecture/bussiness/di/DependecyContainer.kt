package com.example.cleanarquitecture.bussiness.di

import com.example.cleanarquitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.cleanarquitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.example.cleanarquitecture.business.domain.model.Note
import com.example.cleanarquitecture.business.domain.model.NoteFactory
import com.example.cleanarquitecture.business.domain.util.DateUtil
import com.example.cleanarquitecture.bussiness.data.NoteDataFactory
import com.example.cleanarquitecture.bussiness.data.cache.FakeNoteCacheDataSourceImpl
import com.example.cleanarquitecture.bussiness.data.network.FakeNoteNetworkDataSourceImpl
import com.example.cleanarquitecture.util.isUnitTest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

//Esta clase permite simular la Inyeccion de dependencias en pruebas unitarias
class DependencyContainer {


    private val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH)
    val dateUtil =
        DateUtil(dateFormat)
    lateinit var noteNetworkDataSource: NoteNetworkDataSource
    lateinit var noteCacheDataSource: NoteCacheDataSource
    lateinit var noteFactory: NoteFactory
    lateinit var noteDataFactory: NoteDataFactory

    init {
        isUnitTest = true // for Logger.kt
    }

    // data sets
    lateinit var notesData: HashMap<String, Note>

    fun build() {
        this.javaClass.classLoader?.let { classLoader ->
            noteDataFactory = NoteDataFactory(classLoader)

            // fake data set
            notesData = noteDataFactory.produceHashMapOfNotes(
                noteDataFactory.produceListOfNotes()
            )
        }
        noteFactory = NoteFactory(dateUtil)
        noteNetworkDataSource = FakeNoteNetworkDataSourceImpl(
            notesData = notesData,
            deletedNotesData = HashMap()
        )
        noteCacheDataSource = FakeNoteCacheDataSourceImpl(
            notesData = notesData,
            dateUtil = dateUtil
        )
    }

}
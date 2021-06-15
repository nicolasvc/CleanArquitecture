package com.example.cleanarquitecture.framework.datasource.cache.abstraction

import com.example.cleanarquitecture.business.domain.model.Note
import com.example.cleanarquitecture.framework.datasource.database.NOTE_PAGINATION_PAGE_SIZE

interface NoteDaoService {

    suspend fun insertNote(note: Note):Long

    suspend fun deleteNote(primary :String):Long

    suspend fun deletesNotes(note:List<Note>):Int

    suspend fun updateNote(primary: String,newTitle:String, newBody:String)

    suspend fun searchNotes():List<Note>

    suspend fun searchNotesOrderByDateDESC(
        query: String,
        page: Int,
        pageSize: Int = NOTE_PAGINATION_PAGE_SIZE
    ): List<Note>

    suspend fun searchNotesOrderByDateASC(
        query: String,
        page: Int,
        pageSize: Int = NOTE_PAGINATION_PAGE_SIZE
    ): List<Note>

    suspend fun searchNotesOrderByTitleDESC(
        query: String,
        page: Int,
        pageSize: Int = NOTE_PAGINATION_PAGE_SIZE
    ): List<Note>

    suspend fun searchNotesOrderByTitleASC(
        query: String,
        page: Int,
        pageSize: Int = NOTE_PAGINATION_PAGE_SIZE
    ): List<Note>


    suspend fun searchNoteById(primaryKey: String): Note?

    suspend fun getNumNotes():Int

    suspend fun insertNotes(notes:List<Note>):LongArray
}
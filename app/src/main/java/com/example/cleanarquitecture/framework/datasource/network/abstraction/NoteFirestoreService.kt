package com.example.cleanarquitecture.framework.datasource.network.abstraction

import com.example.cleanarquitecture.business.domain.model.Note

interface NoteFirestoreService{

    suspend fun insertOrUpdateNote(note: Note)

    suspend fun deleteNote(primary :String)

    suspend fun insertDeleteNote(note: Note)

    suspend fun insertDeleteNote(notes: List<Note>)

    suspend fun deleteDeleteNote(note: Note)

    suspend fun getDeletedNote():List<Note>

    suspend fun deleteAllNote()

    suspend fun searchNote(note: Note): Note?

    suspend fun getAllNote():List<Note>

    suspend fun insertOrUpdateNotes(notes: List<Note>)

}
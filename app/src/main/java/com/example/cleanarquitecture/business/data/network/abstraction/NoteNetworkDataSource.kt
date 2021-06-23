package com.example.cleanarquitecture.business.data.network.abstraction

import com.example.cleanarquitecture.business.domain.model.Note

interface NoteNetworkDataSource {

    suspend fun insertOrUpdateNote(note: Note)

    suspend fun deleteNote(primary :String)

    suspend fun insertDeleteNote(note: Note)

    suspend fun insertDeleteNotes(notes: List<Note>)

    suspend fun deleteDeleteNote(note: Note)

    suspend fun getDeletedNotes():List<Note>

    suspend fun deleteAllNotes()

    suspend fun searchNote(note: Note):Note?

    suspend fun getAllNotes():List<Note>

    suspend fun insertOrUpdateNotes(notes: List<Note>)

}
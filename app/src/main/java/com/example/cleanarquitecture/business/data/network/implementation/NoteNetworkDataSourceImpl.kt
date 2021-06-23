package com.example.cleanarquitecture.business.data.network.implementation

import com.example.cleanarquitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.example.cleanarquitecture.business.domain.model.Note
import com.example.cleanarquitecture.framework.datasource.network.abstraction.NoteFirestoreService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteNetworkDataSourceImpl @Inject constructor(
    private val firestoreService: NoteFirestoreService
) : NoteNetworkDataSource{
    override suspend fun insertOrUpdateNote(note: Note) = firestoreService.insertOrUpdateNote(note);

    override suspend fun deleteNote(primary: String) = firestoreService.deleteNote(primary);

    override suspend fun insertDeleteNote(note: Note)  = firestoreService.insertDeleteNote(note);

    override suspend fun insertDeleteNotes(notes: List<Note>)  = firestoreService.insertDeleteNote(notes);

    override suspend fun deleteDeleteNote(note: Note)  = firestoreService.deleteDeleteNote(note);

    override suspend fun getDeletedNotes(): List<Note> = firestoreService.getDeletedNote();

    override suspend fun deleteAllNotes() = firestoreService.deleteAllNote();

    override suspend fun searchNote(note: Note): Note?  = firestoreService.searchNote(note);

    override suspend fun getAllNotes(): List<Note>  = firestoreService.getAllNote();

    override suspend fun insertOrUpdateNotes(notes: List<Note>)  = firestoreService.insertOrUpdateNotes(notes);
}
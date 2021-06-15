package com.example.cleanarquitecture.business.data.cache.implementation

import com.example.cleanarquitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.cleanarquitecture.business.domain.model.Note
import com.example.cleanarquitecture.framework.datasource.cache.abstraction.NoteDaoService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteCacheDataSourceImpl @Inject constructor(
    private val noteDaoService: NoteDaoService
) :NoteCacheDataSource{
    override suspend fun insertNote(note: Note) = noteDaoService.insertNote(note)

    override suspend fun deleteNote(primary: String) = noteDaoService.deleteNote(primary)

    override suspend fun deletesNotes(note: List<Note>) = noteDaoService.deletesNotes(note)

    override suspend fun updateNote(primary: String, newTitle: String, newBody: String)  = noteDaoService.updateNote(primary,
            newTitle,
            newBody)

    override suspend fun searchNotes(query: String, filterAndOrder: String, page: Int): List<Note> = noteDaoService.searchNotes()

    override suspend fun searchNoteById(primaryKey: String) = noteDaoService.searchNoteById(primaryKey)

    override suspend fun getNumNotes() = noteDaoService.getNumNotes()

    override suspend fun insertNotes(notes: List<Note>) = noteDaoService.insertNotes(notes)


}
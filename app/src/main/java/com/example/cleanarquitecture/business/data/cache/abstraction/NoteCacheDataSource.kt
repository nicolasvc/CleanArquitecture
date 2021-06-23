package com.example.cleanarquitecture.business.data.cache.abstraction

import com.example.cleanarquitecture.business.domain.model.Note

interface NoteCacheDataSource {

    //Cuando se hace la insercion  a una base de datos retorna Long
    suspend fun insertNote(note: Note):Long

    suspend fun deleteNote(primary :String):Long

    suspend fun deletesNotes(note:List<Note>):Int

    suspend fun updateNote(primary: String,newTitle:String, newBody:String) : Int

    suspend fun searchNotes(query:String,filterAndOrder:String,page:Int):List<Note>

    suspend fun searchNoteById(primaryKey: String):Note?

    suspend fun getNumNotes():Int

    suspend fun insertNotes(notes:List<Note>):LongArray
}
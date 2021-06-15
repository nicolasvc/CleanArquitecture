package com.example.cleanarquitecture.business.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Note(
    val id:String,
    val title:String,
    val body:String,
    val update_at:String,
    val created_at:String,
):Parcelable
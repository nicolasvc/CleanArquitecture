package com.example.cleanarquitecture.business.domain.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateUtil @Inject constructor(
    private val dateFormate: SimpleDateFormat
) {
    //Date format : "2019-07-23 HH:mm:ss"

    fun removeTimeFromDateString(sd: String) = sd.substring(0, sd.indexOf(" "))

    fun convertFirebaseTimeStampToStringDate(timeStamp: Timestamp) =
        dateFormate.format(timeStamp.toDate())

    fun convertStringDateToFirebaseTimeStamp(date: String) = Timestamp(dateFormate.parse(date)!!)

    fun getCurrentTimeStamp() = dateFormate.format(Date())

}
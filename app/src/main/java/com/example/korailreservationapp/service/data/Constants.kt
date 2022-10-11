package com.example.korailreservationapp.service.data

sealed class Train(val name: String) {
    object MugungHwa : Train("무궁화")
    object SaeMauel : Train("새마을")
    object KTX : Train("KTX")
}
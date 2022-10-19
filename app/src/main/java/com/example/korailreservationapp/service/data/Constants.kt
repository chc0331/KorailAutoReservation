package com.example.korailreservationapp.service.data

sealed class Train(val name: String) {
    object MugungHwa : Train("무궁화")
    object SaeMauel : Train("새마을")
    object KTX : Train("KTX")
}

const val KORAIL_PKG = "com.korail.talk"
const val BEFORE_DAY = "이전날"
const val AFTER_DAY = "다음날"

const val SOLD_OUT = "매진"
const val WAIT = "입석+좌석"
package com.example.korailreservationapp.utils

import com.example.korailreservationapp.service.data.Ticket

fun List<Ticket>.containsTicket(ticket: Ticket): Boolean {
    for (item in this) {
        if (item == ticket)
            return true
    }
    return false
}

fun List<Pair<Ticket, Int>>.containsTicket(ticket: Ticket): Int {
    var state = 0
    for ((_ticket, _state) in this) {
        if (ticket == _ticket) {
            state = _state
            break
        }
    }
    return state
}

fun ArrayList<Int>.isNormalSeat(idx: Int): Boolean {
    return this[idx] and (1 shl 0) > 0
}

fun ArrayList<Int>.isSpecialSeat(idx: Int): Boolean {
    return this[idx] and (1 shl 1) > 0
}

fun Int.isNormalSeat(): Boolean {
    return this and (1 shl 0) > 0
}

fun Int.isSpecialSeat(): Boolean {
    return this and (1 shl 1) > 0
}
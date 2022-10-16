package com.example.korailreservationapp.utils

import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import com.example.korailreservationapp.service.data.Ticket
import com.example.korailreservationapp.service.data.Train
import kotlin.math.abs

fun AccessibilityNodeInfo.getPosition(): Pair<Float, Float> {
    val rect = Rect()
    this.getBoundsInScreen(rect)
    return Pair(rect.centerX().toFloat(), rect.centerY().toFloat())
}

fun AccessibilityNodeInfo.isTrainInfoNode(): Boolean {
    val text = this.text
    return text.contains(Train.KTX.name) or
            text.contains(Train.SaeMauel.name) or
            text.contains(Train.MugungHwa.name)
}

fun List<AccessibilityNodeInfo>.makeTicket(idx: Int): Ticket {
    val train = this[idx].text
    val startInfo = this[idx + 1].text
    val destinationInfo = this[idx + 2].text
    val seat = this[idx + 3]
    var specialSeat = this[idx + 4]

    if (abs(seat.getPosition().first - specialSeat.getPosition().first) < 5) {
        specialSeat = this[idx + 5]
    }
    return Ticket(idx, train, startInfo, destinationInfo, seat, specialSeat)
}
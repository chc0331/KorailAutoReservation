package com.example.korailreservationapp.service.data

import android.view.accessibility.AccessibilityNodeInfo

data class Ticket(
    val id: Int,
    val train: CharSequence,
    val startInfo: CharSequence,
    val destinationInfo: CharSequence,
    val seat: AccessibilityNodeInfo,
    val specialSeat: AccessibilityNodeInfo
) {
    override fun equals(other: Any?): Boolean {
        if (other is Ticket) {
            if (train != other.train)
                return false
            if (startInfo != other.startInfo)
                return false
            if (destinationInfo != other.destinationInfo)
                return false
        }
        return true
    }
}
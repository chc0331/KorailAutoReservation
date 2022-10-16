package com.example.korailreservationapp.service.ui

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.example.korailreservationapp.service.KorailReservationService
import com.example.korailreservationapp.service.data.Ticket
import com.example.korailreservationapp.utils.isTrainInfoNode
import com.example.korailreservationapp.utils.makeTicket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

@RequiresApi(Build.VERSION_CODES.N)
class ReservationUiController {

    companion object {
        fun collectData(service: KorailReservationService) = flow {
            val list = service.getAllNodeInfos()
                .filter {
                    !it.text.isNullOrBlank()
                }
            emit(getTicketInfo(list))
        }.flowOn(Dispatchers.IO)

        private fun getTicketInfo(list: List<AccessibilityNodeInfo>): List<Ticket> {
            var ticketList = ArrayList<Ticket>()

            for (idx: Int in list.indices) {
                if (list[idx].isTrainInfoNode()) {
                    ticketList.add(list.makeTicket(idx))
                }
            }
            return ticketList
        }

    }
}


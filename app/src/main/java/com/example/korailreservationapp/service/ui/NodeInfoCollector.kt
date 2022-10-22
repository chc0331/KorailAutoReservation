package com.example.korailreservationapp.service.ui

import android.os.Build
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.example.korailreservationapp.service.KorailReservationService
import com.example.korailreservationapp.service.data.AFTER_DAY
import com.example.korailreservationapp.service.data.BEFORE_DAY
import com.example.korailreservationapp.service.data.Ticket
import com.example.korailreservationapp.utils.getPosition
import com.example.korailreservationapp.utils.isTrainInfoNode
import com.example.korailreservationapp.utils.makeTicket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

@RequiresApi(Build.VERSION_CODES.N)
class NodeInfoCollector(private val service: KorailReservationService) {
    private var beforeNode: AccessibilityNodeInfo? = null
    private var afterNode: AccessibilityNodeInfo? = null

    private suspend fun collectNodeInfo() = flow {
        val list = service.getAllNodeInfos()
            .filter {
                !it.text.isNullOrBlank()
            }
        emit(list)
    }.flowOn(Dispatchers.IO)

    suspend fun collectTickets(): Flow<List<Ticket>> = collectNodeInfo().map {
        getTicketInfo(it)
    }

    suspend fun collectPrevNextNode(): Flow<List<AccessibilityNodeInfo>> =
        collectNodeInfo().map { node ->
            node.filter {
                it.text.contains(BEFORE_DAY) or it.text.contains(AFTER_DAY)
            }
        }

    private fun getTicketInfo(list: List<AccessibilityNodeInfo>): List<Ticket> {
        var ticketList = ArrayList<Ticket>()

        for (idx: Int in list.indices) {
            if (list[idx].text.contains(BEFORE_DAY)) {
                beforeNode = list[idx]
            } else if (list[idx].text.contains(AFTER_DAY)) {
                afterNode = list[idx]
            } else if (list[idx].isTrainInfoNode() && list[idx].getPosition().second < 2100f) {
                val ticket = list.makeTicket(idx)
                ticket?.let { ticketList.add(it) }
            }
        }
        return ticketList
    }

}


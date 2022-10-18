package com.example.korailreservationapp.service.ui

import android.os.Build
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.example.korailreservationapp.service.KorailReservationService
import com.example.korailreservationapp.service.autoclick.AutoClickCommand
import com.example.korailreservationapp.service.data.AFTER_DAY
import com.example.korailreservationapp.service.data.BEFORE_DAY
import com.example.korailreservationapp.service.data.Ticket
import com.example.korailreservationapp.utils.containsTicket
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

@RequiresApi(Build.VERSION_CODES.N)
class ReservationController(private val service: KorailReservationService) {

    private var collector = NodeInfoCollector(service)
    private var command = AutoClickCommand(service)
    private var beforeNode: AccessibilityNodeInfo? = null
    private var afterNode: AccessibilityNodeInfo? = null

    fun startAutoReservation(checkedTicket: List<Pair<Ticket, Int>>) =
        CoroutineScope(Dispatchers.Main).launch {
            //1. get next, prev nodeInfo
            delay(200)
            command.moveLongScrollUp()
            delay(500)
            collectPrefNextNode()

            //2. find correct screen
            findScreen(checkedTicket)
        }

    private suspend fun collectPrefNextNode() = withContext(Dispatchers.Default) {
        collector.collectPrevNextNode().collectLatest {
            for (node: AccessibilityNodeInfo in it) {
                if (node.text.equals(BEFORE_DAY)) {
                    beforeNode = node
                }
                if (node.text.equals(AFTER_DAY)) {
                    afterNode = node
                }
            }
        }
    }

    private suspend fun findScreen(checkedTicket: List<Pair<Ticket, Int>>) =
        withContext(Dispatchers.Default) {
            repeat(25) {
                collector.collectTickets().collect {
                    var contain = true
                    for (item in checkedTicket) {
                        val ticket = item.first
                        if (!it.containsTicket(ticket)) {
                            contain = false
                            break
                        }
                    }

                    if (contain)
                        cancel()
                    else {
                        command.moveShortcutScrollDown()
                        delay(500)
                    }
                }
            }
        }

}
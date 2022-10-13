package com.example.korailreservationapp.service.ui

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.korailreservationapp.service.KorailReservationService
import com.example.korailreservationapp.service.data.Ticket
import com.example.korailreservationapp.service.data.Train
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

@RequiresApi(Build.VERSION_CODES.N)
class ReservationController {

    companion object {
        fun collectData(service: KorailReservationService) = flow<List<Ticket>> {
            val list = service.getAllNodeInfos()
                .filter {
                    !it.text.isNullOrBlank()
                }
            var ticketList = ArrayList<Ticket>()
            for (idx: Int in list.indices) {
                val text = list[idx].text
                if (text.contains(Train.KTX.name) or text.contains(Train.SaeMauel.name) or text.contains(
                        Train.MugungHwa.name
                    )
                ) {
                    val startInfo = list[idx + 1].text
                    val destinationInfo = list[idx + 2].text
                    val seat = list[idx + 3]
                    val specialSeat = list[idx + 4]
                    ticketList.add(
                        Ticket(
                            idx,
                            text,
                            startInfo,
                            destinationInfo,
                            seat,
                            specialSeat
                        )
                    )
                }
            }
            emit(ticketList)
        }.flowOn(Dispatchers.IO)


        fun click(x: Float, y: Float, service: AccessibilityService) {
            val path = Path()
            path.moveTo(x, y)
            val gestureBuilder = GestureDescription.Builder()
            gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, 500))
            service.dispatchGesture(gestureBuilder.build(), null, null)
        }
    }


}
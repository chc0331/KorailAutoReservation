package com.example.korailreservationapp.service.ui

import android.util.Log
import com.example.korailreservationapp.service.KorailReservationService
import com.example.korailreservationapp.service.data.Ticket
import com.example.korailreservationapp.service.data.Train
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

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
            Log.d("heec.choi","ticket List : "+ticketList)
            emit(ticketList)
        }.flowOn(Dispatchers.IO)
    }


}
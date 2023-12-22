package com.example.uas.model

data class Travel(
    var id: String = "",
    var trainName: String = "",
    var timeOrigin: String = "",
    var timeDestination: String = "",
    var stationOrigin: String = "",
    var stationDestination: String = "",
    var klass: String = "",
    var facilities: ArrayList<String> = arrayListOf(),
    var price: Int = 0
)
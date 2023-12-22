package com.example.uas.model

data class Order(
    var id: String = "",
    var userId: String = "",
    var travelId: String = "",
    var date: String = "",
    var seat: Int = 0
)

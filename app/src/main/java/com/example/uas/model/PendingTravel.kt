package com.example.uas.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "travel_table")
data class PendingTravel(
    @PrimaryKey
    var id: String = "",

    @ColumnInfo(name = "trainName")
    var trainName: String = "",

    @ColumnInfo(name = "timeOrigin")
    var timeOrigin: String = "",

    @ColumnInfo(name = "timeDestination")
    var timeDestination: String = "",

    @ColumnInfo(name = "stationOrigin")
    var stationOrigin: String = "",

    @ColumnInfo(name = "stationDestination")
    var stationDestination: String = "",

    @ColumnInfo(name = "klass")
    var klass: String = "",

    @ColumnInfo(name = "facilities")
    var facilities: String = "",

    @ColumnInfo(name = "price")
    var price: Int = 0
)

package com.example.uas.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_table")
data class Favorite(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id: Int = 0,

    @ColumnInfo(name = "user_id")
    var userId: String = "",

    @ColumnInfo(name = "travel_id")
    var travelId: String = "",

    @ColumnInfo(name = "tag")
    var tag: String = ""
)

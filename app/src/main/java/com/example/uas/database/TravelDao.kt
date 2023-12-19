package com.example.uas.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.uas.model.PendingTravel

@Dao
interface TravelDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(travel: PendingTravel)

    @Update
    fun update(travel: PendingTravel)

    @Delete
    fun delete(travel: PendingTravel)

    @get:Query("SELECT * FROM travel_table ORDER BY id ASC")
    val allTravel: LiveData<List<PendingTravel>>
}
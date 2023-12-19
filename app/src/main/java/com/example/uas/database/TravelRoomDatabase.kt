package com.example.uas.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.example.uas.model.PendingTravel

@Database(entities = [PendingTravel::class], version = 1, exportSchema = false)
abstract class TravelRoomDatabase : RoomDatabase() {
    abstract fun travelDao() : TravelDao?
    companion object {
        @Volatile
        private var INSTANCE : TravelRoomDatabase? = null
        fun getDatabase(context: Context) : TravelRoomDatabase ? {
            if (INSTANCE == null) {
                synchronized(TravelRoomDatabase::class.java) {
                    INSTANCE = databaseBuilder(
                        context.applicationContext,
                        TravelRoomDatabase::class.java,
                        "travel_database"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}
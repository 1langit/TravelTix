package com.example.uas.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.example.uas.model.Favorite

@Database(entities = [Favorite::class], version = 1, exportSchema = false)
abstract class FavoriteRoomDatabase : RoomDatabase() {
    abstract fun favoriteDao() : FavoriteDao?
    companion object {
        @Volatile
        private var INSTANCE : FavoriteRoomDatabase? = null
        fun getDatabase(context: Context) : FavoriteRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(FavoriteRoomDatabase::class.java) {
                    INSTANCE = databaseBuilder(
                        context.applicationContext,
                        FavoriteRoomDatabase::class.java,
                        "travel_database"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}
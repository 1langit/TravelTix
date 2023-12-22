package com.example.uas.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.uas.model.Favorite

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(favorite: Favorite)

    @Delete
    fun delete(favorite: Favorite)

    @Query("DELETE FROM favorite_table WHERE user_id = :userId AND travel_id = :travelId")
    fun deleteFavorite(userId: String, travelId: String)

    @Query("SELECT * FROM favorite_table WHERE user_id = :userId ORDER BY id DESC")
    fun getUserFavorite(userId: String) : LiveData<List<Favorite>>
}
package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

  @Query("SELECT * FROM workout_playlists ORDER BY id ASC")
  fun getAllPlaylists(): Flow<List<WorkoutEntity>>

  @Query("SELECT * FROM workout_playlists WHERE id = :id LIMIT 1")
  suspend fun getPlaylistById(id: Long): WorkoutEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(playlist: WorkoutEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(playlists: List<WorkoutEntity>)

  @Update
  suspend fun update(playlist: WorkoutEntity)

  @Delete
  suspend fun delete(playlist: WorkoutEntity)

  @Query("DELETE FROM workout_playlists WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("SELECT COUNT(*) FROM workout_playlists")
  suspend fun getCount(): Int
}

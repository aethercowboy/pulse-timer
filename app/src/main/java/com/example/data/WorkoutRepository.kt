package com.example.data

import com.example.model.WorkoutPlaylist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class WorkoutRepository(private val workoutDao: WorkoutDao) {

  val allPlaylists: Flow<List<WorkoutPlaylist>> = workoutDao.getAllPlaylists().map { entities ->
    entities.map { it.toDomain() }
  }

  suspend fun ensureSeeded() {
    withContext(Dispatchers.IO) {
      if (workoutDao.getCount() == 0) {
        val presets = DefaultWorkouts.getPresets().map { WorkoutEntity.fromDomain(it) }
        workoutDao.insertAll(presets)
      }
    }
  }

  suspend fun getPlaylistById(id: Long): WorkoutPlaylist? {
    return withContext(Dispatchers.IO) {
      workoutDao.getPlaylistById(id)?.toDomain()
    }
  }

  suspend fun savePlaylist(playlist: WorkoutPlaylist): Long {
    return withContext(Dispatchers.IO) {
      val entity = WorkoutEntity.fromDomain(playlist)
      if (entity.id > 0) {
        workoutDao.update(entity)
        entity.id
      } else {
        workoutDao.insert(entity)
      }
    }
  }

  suspend fun deletePlaylist(playlist: WorkoutPlaylist) {
    withContext(Dispatchers.IO) {
      workoutDao.deleteById(playlist.id)
    }
  }

  suspend fun restorePresets() {
    withContext(Dispatchers.IO) {
      val presets = DefaultWorkouts.getPresets().map { WorkoutEntity.fromDomain(it) }
      workoutDao.insertAll(presets)
    }
  }
}

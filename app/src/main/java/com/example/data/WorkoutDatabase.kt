package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [WorkoutEntity::class], version = 1, exportSchema = false)
abstract class WorkoutDatabase : RoomDatabase() {

  abstract fun workoutDao(): WorkoutDao

  companion object {
    @Volatile
    private var INSTANCE: WorkoutDatabase? = null

    fun getDatabase(context: Context): WorkoutDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          WorkoutDatabase::class.java,
          "pulsetimer_database"
        )
          .addCallback(object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
              super.onCreate(db)
              // Seed default workouts in background
              CoroutineScope(Dispatchers.IO).launch {
                val dao = getDatabase(context).workoutDao()
                val presets = DefaultWorkouts.getPresets().map { WorkoutEntity.fromDomain(it) }
                dao.insertAll(presets)
              }
            }
          })
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}

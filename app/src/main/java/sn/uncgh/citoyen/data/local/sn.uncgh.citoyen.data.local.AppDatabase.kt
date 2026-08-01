package sn.uncgh.citoyen.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import sn.uncgh.citoyen.data.local.dao.IncidentDao
import sn.uncgh.citoyen.data.local.entity.Incident

@Database(entities = [Incident::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun incidentDao(): IncidentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_citoyenne_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
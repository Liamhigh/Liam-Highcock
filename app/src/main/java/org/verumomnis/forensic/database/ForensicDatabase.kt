package org.verumomnis.forensic.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Verum Omnis Forensic Database
 * 
 * Local SQLite database for evidence persistence.
 * All data stored locally, never transmitted.
 * Maintains offline-first, airgap-ready principles.
 */
@Database(
    entities = [CaseEntity::class, EvidenceEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ForensicDatabase : RoomDatabase() {
    
    abstract fun caseDao(): CaseDao
    abstract fun evidenceDao(): EvidenceDao
    
    companion object {
        private const val DATABASE_NAME = "verum_omnis_forensic.db"
        
        @Volatile
        private var INSTANCE: ForensicDatabase? = null
        
        fun getInstance(context: Context): ForensicDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ForensicDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

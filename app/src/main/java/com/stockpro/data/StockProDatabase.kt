package com.stockpro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ProductEntity::class, CategoryEntity::class, StockMovementEntity::class], version = 3, exportSchema = true)
abstract class StockProDatabase : RoomDatabase() {
    abstract fun dao(): StockProDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE products ADD COLUMN purchasePrice REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE products ADD COLUMN salePrice REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE products ADD COLUMN expiryDate TEXT NOT NULL DEFAULT ''")
            }
        }
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE products ADD COLUMN imageKey TEXT NOT NULL DEFAULT 'otros'")
            }
        }
        @Volatile private var INSTANCE: StockProDatabase? = null
        fun get(context: Context): StockProDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context.applicationContext, StockProDatabase::class.java, "stockpro.db")
                // No borrar datos automáticamente. Cada cambio de esquema requiere una migración explícita.
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build().also { INSTANCE = it }
        }
    }
}

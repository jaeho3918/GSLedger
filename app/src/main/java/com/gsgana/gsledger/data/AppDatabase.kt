package com.gsgana.gsledger.data


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.commonsware.cwac.saferoom.SafeHelperFactory


@Database(entities = [Product::class], version = 1, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(
            context: Context,
            key: CharArray?
        ): AppDatabase {
            key
            val key1 = charArrayOf(
                "q"[0],
                "X"[0],
                "0"[0],
                "J"[0],
                "2"[0],
                "Y"[0],
                "3"[0],
                "E"[0],
                "y"[0],
                "3"[0],
                "C"[0],
                "j"[0],
                "Q"[0],
                "o"[0],
                "1"[0],
                "n"[0],
                "j"[0],
                "P"[0],
                "m"[0],
                "s"[0],
                "h"[0],
                "E"[0],
                "D"[0],
                "Y"[0],
                "B"[0],
                "J"[0],
                "E"[0]
            )
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context, key1).also { instance = it }
            }
        }

        private fun buildDatabase(
            context: Context,
            key: CharArray?
        ): AppDatabase {
            val factory = SafeHelperFactory(key)
            return Room.databaseBuilder(context, AppDatabase::class.java, "product")
                .openHelperFactory(factory)
                .build()
        }
    }
}

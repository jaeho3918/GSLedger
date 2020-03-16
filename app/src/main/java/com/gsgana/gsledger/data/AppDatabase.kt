package com.gsgana.gsledger.data


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.commonsware.cwac.saferoom.SafeHelperFactory


@Database( entities = [Product::class, ProductImage::class, Option::class], version = 1, exportSchema = true)

abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun productImageDao(): ProductImageDao
    abstract fun optionDao(): OptionDao

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(
            context: Context,
            key: CharArray?
        ): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context, key).also { instance = it }
            }
        }

        private fun buildDatabase(
            context: Context,
            key: CharArray?
        ): AppDatabase {
            val factory = SafeHelperFactory(key)
            return Room.databaseBuilder(context, AppDatabase::class.java, "product")
//                .addMigrations(MIGRATION_1_2)
                .openHelperFactory(factory)
                .build()
        }
    }
}

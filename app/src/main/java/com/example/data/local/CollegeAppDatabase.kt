package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.PostViewDao
import com.example.data.local.dao.StudentLoginDao
import com.example.data.local.entity.PostViewEntity
import com.example.data.local.entity.StudentLoginEntity

@Database(
    entities = [
        StudentLoginEntity::class,
        PostViewEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CollegeAppDatabase : RoomDatabase() {
    abstract fun studentLoginDao(): StudentLoginDao
    abstract fun postViewDao(): PostViewDao

    companion object {
        @Volatile
        private var INSTANCE: CollegeAppDatabase? = null

        fun getInstance(context: Context): CollegeAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CollegeAppDatabase::class.java,
                    "ggc_college_room.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

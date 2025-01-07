package com.example.gaugedemo.database

import android.content.Context
import androidx.room.Room.databaseBuilder

class DatabaseClient private constructor(mContext: Context) {
    // app database
    // creating the app database with Room database builder
    private val appDatabase: AppDatabase = databaseBuilder(
        mContext,
        AppDatabase::class.java,
        "app-database"
    )
        .fallbackToDestructiveMigration()
        .build()

    companion object {
        @Volatile
        private var mInstance: DatabaseClient? = null

        fun getInstance(context: Context): DatabaseClient =
            mInstance ?: synchronized(this) {
                mInstance ?: DatabaseClient(context).also { mInstance = it }
            }
    }

    fun getAppDatabase(): AppDatabase = appDatabase

}
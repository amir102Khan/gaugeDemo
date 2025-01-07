package com.example.gaugemeterdemo

import android.app.Application
import com.example.gaugedemo.database.DatabaseClient


class Global : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseClient.getInstance(this)
    }
}
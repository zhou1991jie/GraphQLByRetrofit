package com.example.grapqldemo6

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PokemonApplication(): Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("GlobalExceptionHandler", "Uncaught exception in thread ${thread.name}", throwable)
            throwable.printStackTrace()
        }
    }
}
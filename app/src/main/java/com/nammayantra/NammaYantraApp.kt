package com.nammayantra

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.cloudinary.android.MediaManager

class NammaYantraApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Force Light Mode to ensure high contrast for agriculture environments
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        
        // Initialize Cloudinary once for the whole app
        val config = mapOf(
            "cloud_name" to "dzf6d8d8m",
            "api_key" to "875511919113518",
            "api_secret" to "nwoptau0M95tHYlusXlnUs1csEs"
        )
        MediaManager.init(this, config)
    }
}

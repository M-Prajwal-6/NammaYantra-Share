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
            "cloud_name" to " CLOUD NAME ",
            "api_key" to " API KEY ",
            "api_secret" to " SECRET KEY "
        )
        MediaManager.init(this, config)
    }
}

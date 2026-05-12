package com.nammayantra.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nammayantra.firebase.FirebaseHelper
import com.nammayantra.models.User

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val currentUser = FirebaseHelper.auth.currentUser
        if (currentUser == null) {
            // No user signed in, show Welcome screen (SignIn or Register)
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        } else {
            // User is signed in, check role and go to dashboard
            FirebaseHelper.firestore.collection(FirebaseHelper.USERS_COLLECTION).document(currentUser.uid)
                .get()
                .addOnSuccessListener {
                    val user = it.toObject(User::class.java)
                    if (user != null) {
                        if (user.role == "OWNER") {
                            startActivity(Intent(this, OwnerDashboardActivity::class.java))
                        } else {
                            startActivity(Intent(this, FarmerDashboardActivity::class.java))
                        }
                        finish()
                    } else {
                        // User exists in Auth but not in Firestore, take to Welcome
                        startActivity(Intent(this, WelcomeActivity::class.java))
                        finish()
                    }
                }
                .addOnFailureListener {
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                }
        }
    }
}

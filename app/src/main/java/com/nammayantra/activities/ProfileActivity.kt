package com.nammayantra.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nammayantra.databinding.ActivityProfileBinding
import com.nammayantra.firebase.FirebaseHelper
import com.nammayantra.models.User
import com.nammayantra.utils.Constants

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        fetchUserData()

        binding.btnLogout.setOnClickListener {
            FirebaseHelper.auth.signOut()
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun fetchUserData() {
        val uid = FirebaseHelper.currentUserId ?: return
        FirebaseHelper.firestore.collection(Constants.USERS).document(uid).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                user?.let { populateUI(it) }
            }
    }

    private fun populateUI(user: User) {
        with(binding) {
            tvName.text = user.name
            tvEmail.text = user.email
            tvPhone.text = user.phone
            chipRole.text = user.role
        }
    }
}

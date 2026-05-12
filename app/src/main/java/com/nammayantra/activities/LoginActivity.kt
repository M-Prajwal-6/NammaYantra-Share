package com.nammayantra.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nammayantra.databinding.ActivityLoginBinding
import com.nammayantra.firebase.FirebaseHelper
import com.nammayantra.models.User

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        FirebaseHelper.auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                checkUserRole()
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUserRole() {
        val uid = FirebaseHelper.auth.currentUser?.uid ?: return
        FirebaseHelper.firestore.collection(FirebaseHelper.USERS_COLLECTION).document(uid)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    if (user.role == "OWNER") {
                        startActivity(Intent(this, OwnerDashboardActivity::class.java))
                    } else {
                        startActivity(Intent(this, FarmerDashboardActivity::class.java))
                    }
                    finish()
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
            }
    }
}

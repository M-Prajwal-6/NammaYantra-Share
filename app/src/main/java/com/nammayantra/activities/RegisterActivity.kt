package com.nammayantra.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nammayantra.databinding.ActivityRegisterBinding
import com.nammayantra.firebase.FirebaseHelper
import com.nammayantra.models.User

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun registerUser() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val role = if (binding.rbOwner.isChecked) "OWNER" else "FARMER"

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        FirebaseHelper.auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = it.user?.uid ?: ""
                val user = User(uid, name, email, role, phone)
                saveUserToFirestore(user)
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Registration failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserToFirestore(user: User) {
        FirebaseHelper.firestore.collection(FirebaseHelper.USERS_COLLECTION).document(user.id)
            .set(user)
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                if (user.role == "OWNER") {
                    startActivity(Intent(this, OwnerDashboardActivity::class.java))
                } else {
                    startActivity(Intent(this, FarmerDashboardActivity::class.java))
                }
                finishAffinity()
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Failed to save user info", Toast.LENGTH_SHORT).show()
            }
    }
}

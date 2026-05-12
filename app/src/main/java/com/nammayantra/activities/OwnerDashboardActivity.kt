package com.nammayantra.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nammayantra.adapters.MachineAdapter
import com.nammayantra.databinding.ActivityOwnerDashboardBinding
import com.nammayantra.firebase.FirebaseHelper
import com.nammayantra.models.Booking
import com.nammayantra.models.Machine
import com.nammayantra.utils.Constants

class OwnerDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOwnerDashboardBinding
    private lateinit var adapter: MachineAdapter
    private val myMachines = mutableListOf<Machine>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setSupportActionBar(binding.toolbar)
        fetchMyMachines()
        fetchStats()

        binding.fabAddMachine.setOnClickListener {
            startActivity(Intent(this, AddMachineActivity::class.java))
        }

        binding.cardRequests.setOnClickListener {
            startActivity(Intent(this, BookingRequestsActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(com.nammayantra.R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            com.nammayantra.R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            com.nammayantra.R.id.action_logout -> {
                FirebaseHelper.auth.signOut()
                startActivity(Intent(this, WelcomeActivity::class.java))
                finishAffinity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun setupRecyclerView() {
        adapter = MachineAdapter(
            machines = myMachines,
            isOwnerMode = true,
            onMachineClick = { machine ->
                val intent = Intent(this, MachineDetailActivity::class.java)
                intent.putExtra("MACHINE_ID", machine.id)
                startActivity(intent)
            },
            onEditClick = { machine ->
                val intent = Intent(this, AddMachineActivity::class.java)
                intent.putExtra("MACHINE_ID", machine.id)
                startActivity(intent)
            },
            onDeleteClick = { machine ->
                showDeleteConfirmation(machine)
            }
        )
        binding.rvMyMachines.layoutManager = LinearLayoutManager(this)
        binding.rvMyMachines.adapter = adapter
    }

    private fun showDeleteConfirmation(machine: Machine) {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle("Delete Machine")
            .setMessage("Are you sure you want to delete ${machine.name}?")
            .setPositiveButton("Delete") { _, _ ->
                deleteMachine(machine)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteMachine(machine: Machine) {
        FirebaseHelper.firestore.collection(Constants.MACHINES).document(machine.id)
            .delete()
            .addOnSuccessListener {
                android.widget.Toast.makeText(this, "Machine deleted", android.widget.Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                android.widget.Toast.makeText(this, "Error: ${it.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchMyMachines() {
        val uid = FirebaseHelper.currentUserId ?: return
        FirebaseHelper.firestore.collection(Constants.MACHINES)
            .whereEqualTo("ownerId", uid)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                myMachines.clear()
                value?.let {
                    for (doc in it) {
                        val machine = doc.toObject(Machine::class.java)
                        machine.id = doc.id
                        myMachines.add(machine)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }

    private fun fetchStats() {
        val uid = FirebaseHelper.currentUserId ?: return
        
        // Fetch Pending Requests count
        FirebaseHelper.firestore.collection(Constants.BOOKINGS)
            .whereEqualTo("ownerId", uid)
            .whereEqualTo("status", Constants.STATUS_PENDING)
            .addSnapshotListener { value, _ ->
                binding.tvRequestCount.text = (value?.size() ?: 0).toString()
            }

        // Calculate Earnings from Accepted and Completed Bookings
        FirebaseHelper.firestore.collection(Constants.BOOKINGS)
            .whereEqualTo("ownerId", uid)
            .whereIn("status", listOf(Constants.STATUS_ACCEPTED, Constants.STATUS_COMPLETED))
            .addSnapshotListener { value, _ ->
                var total = 0.0
                value?.let {
                    for (doc in it) {
                        try {
                            val booking = doc.toObject(Booking::class.java)
                            total += booking.totalPrice
                        } catch (e: Exception) {}
                    }
                }
                binding.tvTotalEarnings.text = "₹ ${total.toInt()}"
            }

    }
}

package com.nammayantra.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nammayantra.adapters.MachineAdapter
import com.nammayantra.databinding.ActivityFarmerDashboardBinding
import com.nammayantra.firebase.FirebaseHelper
import com.nammayantra.models.Machine
import com.nammayantra.utils.Constants

class FarmerDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFarmerDashboardBinding
    private lateinit var adapter: MachineAdapter
    private var allMachines = mutableListOf<Machine>()
    
    // Mock user location (In real app, get from FusedLocationProvider)
    private val userLat = 12.2958
    private val userLon = 76.6394

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFarmerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setSupportActionBar(binding.toolbar)
        setupSearch()
        fetchMachines()
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
            machines = mutableListOf(),
            userLat = userLat,
            userLon = userLon,
            isOwnerMode = false,
            onMachineClick = { machine ->
                val intent = Intent(this, MachineDetailActivity::class.java)
                intent.putExtra("MACHINE_ID", machine.id)
                startActivity(intent)
            }
        )
        binding.rvMachines.layoutManager = LinearLayoutManager(this)
        binding.rvMachines.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterMachines(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.chipGroupFilters.setOnCheckedStateChangeListener { group, checkedIds ->
            // Filter logic by type
            val selectedChip = group.findViewById<com.google.android.material.chip.Chip>(checkedIds.firstOrNull() ?: -1)
            val type = selectedChip?.text.toString()
            filterByType(type)
        }
    }

    private fun fetchMachines() {
        binding.shimmerViewContainer.startShimmer()
        binding.shimmerViewContainer.visibility = View.VISIBLE
        binding.rvMachines.visibility = View.GONE

        FirebaseHelper.firestore.collection(Constants.MACHINES)
            .addSnapshotListener { value, error ->
                binding.shimmerViewContainer.stopShimmer()
                binding.shimmerViewContainer.visibility = View.GONE
                binding.rvMachines.visibility = View.VISIBLE

                if (error != null) return@addSnapshotListener

                allMachines.clear()
                value?.let {
                    for (doc in it) {
                        val machine = doc.toObject(Machine::class.java)
                        machine.id = doc.id
                        allMachines.add(machine)
                    }
                    adapter.updateData(allMachines)
                    checkEmptyState()
                }
            }
    }

    private fun filterMachines(query: String) {
        val filtered = allMachines.filter {
            it.name.contains(query, ignoreCase = true) || 
            it.description.contains(query, ignoreCase = true)
        }
        adapter.updateData(filtered)
        checkEmptyState(filtered.isEmpty())
    }

    private fun filterByType(type: String) {
        if (type == "All") {
            adapter.updateData(allMachines)
            checkEmptyState()
            return
        }
        val filtered = allMachines.filter { it.type.equals(type, ignoreCase = true) }
        adapter.updateData(filtered)
        checkEmptyState(filtered.isEmpty())
    }

    private fun checkEmptyState(isEmpty: Boolean = allMachines.isEmpty()) {
        binding.layoutEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
}

package com.nammayantra.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nammayantra.R
import com.nammayantra.databinding.ActivityAddMachineBinding
import com.nammayantra.firebase.FirebaseHelper
import com.nammayantra.models.Machine
import com.nammayantra.adapters.SelectedImageAdapter
import com.nammayantra.utils.CloudinaryHelper
import com.nammayantra.utils.Constants
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class AddMachineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMachineBinding
    private val selectedImages = mutableListOf<Uri>()
    private lateinit var imageAdapter: SelectedImageAdapter
    private var machineToEdit: Machine? = null
    
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedImages.clear()
            selectedImages.addAll(uris)
            binding.tvImageCount.text = "${selectedImages.size} images selected"
            imageAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMachineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if we are editing
        val machineId = intent.getStringExtra("MACHINE_ID")
        if (machineId != null) {
            fetchMachineForEdit(machineId)
            binding.toolbar.title = "Edit Machine"
            binding.btnSubmit.text = "Update Machine"
        }

        setupSpinner()
        setupDatePicker()
        setupImageRecyclerView()

        binding.btnSelectImages.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.btnSubmit.setOnClickListener {
            validateAndSubmit()
        }
        
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupDatePicker() {
        binding.etLastService.setOnClickListener {
            val datePicker = com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(supportFragmentManager, "SERVICE_DATE")
            datePicker.addOnPositiveButtonClickListener {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.etLastService.setText(sdf.format(Date(it)))
            }
        }
    }

    private fun fetchMachineForEdit(id: String) {
        FirebaseHelper.firestore.collection(Constants.MACHINES).document(id).get()
            .addOnSuccessListener { doc ->
                machineToEdit = doc.toObject(Machine::class.java)
                machineToEdit?.id = doc.id
                machineToEdit?.let { populateFields(it) }
            }
    }

    private fun populateFields(machine: Machine) {
        with(binding) {
            etName.setText(machine.name)
            spinnerType.setText(machine.type, false)
            etHourlyRate.setText(machine.hourlyRate.toString())
            etDailyRate.setText(machine.dailyRate.toString())
            etDescription.setText(machine.description)
            etLocation.setText(machine.locationName)
            etLastService.setText(machine.lastServiceDate)
            etCondition.setText(machine.conditionRating.toString())
            tvImageCount.text = "${machine.images.size} images (keep current or replace)"
        }
    }

    private fun setupSpinner() {
        val types = arrayOf("Tractor", "Harvester", "Plough", "Sprayer", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, types)
        binding.spinnerType.setAdapter(adapter)
    }

    private fun setupImageRecyclerView() {
        imageAdapter = SelectedImageAdapter(selectedImages) { position ->
            selectedImages.removeAt(position)
            imageAdapter.notifyDataSetChanged()
            binding.tvImageCount.text = if (selectedImages.isEmpty()) 
                "Click to add images" else "${selectedImages.size} images selected"
        }
        binding.rvSelectedImages.adapter = imageAdapter
    }

    private fun validateAndSubmit() {
        val name = binding.etName.text.toString()
        val type = binding.spinnerType.text.toString()
        val hourlyRate = binding.etHourlyRate.text.toString().toDoubleOrNull() ?: 0.0
        val dailyRate = binding.etDailyRate.text.toString().toDoubleOrNull() ?: 0.0
        val description = binding.etDescription.text.toString()
        val location = binding.etLocation.text.toString()
        val lastService = binding.etLastService.text.toString()
        val condition = binding.etCondition.text.toString().toFloatOrNull() ?: 0.0f

        if (name.isBlank()) {
            Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            return
        }

        if (machineToEdit == null && selectedImages.isEmpty()) {
            Toast.makeText(this, "Please select images", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnSubmit.isEnabled = false

        lifecycleScope.launch {
            try {
                // 1. Upload Images to Cloudinary (if new images selected)
                val imageUrls = if (selectedImages.isNotEmpty()) {
                    CloudinaryHelper.uploadImages(selectedImages)
                } else {
                    machineToEdit?.images ?: emptyList()
                }

                // 2. Fetch Owner Details
                val uid = FirebaseHelper.currentUserId ?: ""
                val ownerDoc = FirebaseHelper.firestore.collection(Constants.USERS).document(uid).get().await()
                val ownerName = ownerDoc.getString("name") ?: "Owner User"
                val ownerPhone = ownerDoc.getString("phone") ?: ""

                // 3. Create/Update Machine Object
                val machine = machineToEdit ?: Machine()
                machine.ownerId = uid
                machine.ownerName = ownerName
                machine.ownerPhone = ownerPhone
                machine.name = name
                machine.type = type
                machine.hourlyRate = hourlyRate
                machine.dailyRate = dailyRate
                machine.description = description
                machine.locationName = location
                machine.images = imageUrls
                machine.lastServiceDate = lastService
                machine.conditionRating = condition

                // 4. Save to Firestore
                if (machine.id.isEmpty()) {
                    FirebaseHelper.firestore.collection(Constants.MACHINES).add(machine)
                        .addOnSuccessListener { ref ->
                            ref.update("id", ref.id)
                            Toast.makeText(this@AddMachineActivity, "Machine listed successfully!", Toast.LENGTH_LONG).show()
                            finish()
                        }
                } else {
                    FirebaseHelper.firestore.collection(Constants.MACHINES).document(machine.id).set(machine)
                        .addOnSuccessListener {
                            Toast.makeText(this@AddMachineActivity, "Machine updated successfully!", Toast.LENGTH_LONG).show()
                            finish()
                        }
                }

            } catch (e: Exception) {
                Toast.makeText(this@AddMachineActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                binding.btnSubmit.isEnabled = true
            }
        }
    }
}

package com.nammayantra.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.nammayantra.adapters.ImageSliderAdapter
import com.nammayantra.databinding.ActivityMachineDetailBinding
import com.nammayantra.R
import com.nammayantra.firebase.FirebaseHelper
import com.nammayantra.models.Booking
import com.nammayantra.models.Machine
import com.nammayantra.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class MachineDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMachineDetailBinding
    private var machine: Machine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMachineDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve machine data (usually passed via Intent as JSON or ID)
        // For simplicity in this demo, we'll assume it's passed via Intent
        val machineId = intent.getStringExtra("MACHINE_ID")
        if (machineId != null) {
            fetchMachineDetails(machineId)
        }

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun fetchMachineDetails(id: String) {
        FirebaseHelper.firestore.collection(Constants.MACHINES).document(id).get()
            .addOnSuccessListener { doc ->
                machine = doc.toObject(Machine::class.java)
                machine?.let { setupUI(it) }
            }
    }

    private fun setupUI(machine: Machine) {
        with(binding) {
            tvName.text = machine.name
            tvPrice.text = "₹ ${machine.hourlyRate.toInt()} / hr"
            tvDescription.text = machine.description
            tvOwnerName.text = machine.ownerName
            tvLocation.text = machine.locationName
            tvRating.text = "${machine.avgRating} (${machine.reviewCount} reviews)"
            tvCondition.text = "Condition: ${machine.conditionRating}/5"
            tvLastService.text = "Service: ${machine.lastServiceDate}"

            // Image Slider
            val adapter = ImageSliderAdapter(machine.images)
            viewPagerImages.adapter = adapter
            indicator.setViewPager(viewPagerImages)

            btnCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${machine.ownerPhone}"))
                startActivity(intent)
            }

            btnBookNow.setOnClickListener {
                showBookingDialog(machine)
            }
        }
    }

    private fun showBookingDialog(machine: Machine) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_book_machine, null)
        val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .create()

        val etDate = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etDate)
        val etName = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etFarmerName)
        val etPhone = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etFarmerPhone)
        val etAddress = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etFarmerAddress)
        val etDuration = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etDuration)
        val rgType = dialogView.findViewById<android.widget.RadioGroup>(R.id.rgDurationType)
        val tvTotal = dialogView.findViewById<android.widget.TextView>(R.id.tvTotalEstimate)
        val btnConfirm = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnConfirmBooking)

        // Date Picker
        etDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(supportFragmentManager, "DATE")
            datePicker.addOnPositiveButtonClickListener { 
                etDate.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it)))
            }
        }

        // Live Price Calculation
        val watcher = object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                val qty = s.toString().toDoubleOrNull() ?: 0.0
                val price = if (rgType.checkedRadioButtonId == R.id.rbHours) machine.hourlyRate else machine.dailyRate
                tvTotal.text = "₹ ${String.format("%.0f", qty * price)}"
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        etDuration.addTextChangedListener(watcher)
        rgType.setOnCheckedChangeListener { _, _ -> watcher.afterTextChanged(etDuration.text) }

        btnConfirm.setOnClickListener {
            val name = etName.text.toString()
            val phone = etPhone.text.toString()
            val address = etAddress.text.toString()
            val date = etDate.text.toString()
            val duration = etDuration.text.toString().toDoubleOrNull() ?: 0.0
            val unit = if (rgType.checkedRadioButtonId == R.id.rbHours) "Hours" else "Days"
            val price = if (unit == "Hours") machine.hourlyRate else machine.dailyRate

            if (name.isBlank() || phone.isBlank() || date.isBlank() || duration <= 0) {
                Toast.makeText(this, "Please fill all details correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            createBooking(machine, date, name, phone, address, duration, unit, duration * price)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun createBooking(machine: Machine, date: String, name: String, phone: String, address: String, duration: Double, unit: String, totalPrice: Double) {
        val userId = FirebaseHelper.currentUserId ?: return
        
        val booking = Booking(
            machineId = machine.id,
            machineName = machine.name,
            machineImage = machine.images.firstOrNull() ?: "",
            ownerId = machine.ownerId,
            renterId = userId,
            renterName = name,
            renterPhone = phone,
            renterAddress = address,
            status = Constants.STATUS_PENDING,
            startDate = date,
            startTime = "Flexible",
            duration = duration,
            durationUnit = unit,
            totalPrice = totalPrice
        )

        FirebaseHelper.firestore.collection(Constants.BOOKINGS).add(booking)
            .addOnSuccessListener { ref ->
                ref.update("id", ref.id)
                Toast.makeText(this, "Booking request sent!", Toast.LENGTH_LONG).show()
                finish()
            }
    }

}

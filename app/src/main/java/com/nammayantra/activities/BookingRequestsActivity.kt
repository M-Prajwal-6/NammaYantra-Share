package com.nammayantra.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nammayantra.adapters.BookingAdapter
import com.nammayantra.databinding.ActivityBookingRequestsBinding
import com.nammayantra.firebase.FirebaseHelper
import com.nammayantra.models.Booking
import com.nammayantra.utils.Constants

class BookingRequestsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingRequestsBinding
    private lateinit var adapter: BookingAdapter
    private val bookings = mutableListOf<Booking>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchRequests()

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = BookingAdapter(bookings) { booking, newStatus ->
            updateBookingStatus(booking, newStatus)
        }
        binding.rvRequests.layoutManager = LinearLayoutManager(this)
        binding.rvRequests.adapter = adapter
    }

    private fun fetchRequests() {
        val uid = FirebaseHelper.currentUserId ?: return
        FirebaseHelper.firestore.collection(Constants.BOOKINGS)
            .whereEqualTo("ownerId", uid)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                bookings.clear()
                value?.let { snapshot ->
                    for (doc in snapshot) {
                        try {
                            val booking = doc.toObject(Booking::class.java)
                            if (booking != null) {
                                booking.id = doc.id
                                bookings.add(booking)
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("BookingRequests", "Error parsing booking ${doc.id}: ${e.message}")
                        }
                    }
                    // Sort by newest first
                    bookings.sortByDescending { it.timestamp }
                    adapter.notifyDataSetChanged()
                }

            }
    }

    private fun updateBookingStatus(booking: Booking, status: String) {
        FirebaseHelper.firestore.collection(Constants.BOOKINGS).document(booking.id)
            .update("status", status)
            .addOnSuccessListener {
                Toast.makeText(this, "Request $status", Toast.LENGTH_SHORT).show()
            }
    }
}

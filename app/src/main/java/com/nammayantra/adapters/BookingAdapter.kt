package com.nammayantra.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nammayantra.R
import com.nammayantra.databinding.ItemBookingBinding
import com.nammayantra.models.Booking
import com.nammayantra.utils.Constants

class BookingAdapter(
    private var bookings: List<Booking>,
    private val onStatusChange: (Booking, String) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(val binding: ItemBookingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvMachineName.text = booking.machineName
            tvRenterName.text = "Farmer: ${booking.renterName}"
            tvRenterPhone.text = "Ph: ${booking.renterPhone}"
            tvRenterAddress.text = "Address: ${booking.renterAddress}"
            tvDateTime.text = "${booking.startDate}, ${booking.startTime}"

            tvDuration.text = "${booking.duration.toInt()} ${booking.durationUnit}"
            tvTotalPrice.text = "₹ ${booking.totalPrice.toInt()}"
            tvStatus.text = booking.status

            // Status Styling
            val statusColor = when (booking.status) {
                Constants.STATUS_PENDING -> R.color.accent
                Constants.STATUS_ACCEPTED -> R.color.success
                Constants.STATUS_DECLINED -> R.color.error
                Constants.STATUS_COMPLETED -> R.color.primary
                else -> R.color.text_secondary
            }
            tvStatus.setBackgroundResource(
                when (booking.status) {
                    Constants.STATUS_ACCEPTED -> R.drawable.bg_status_available // Reuse available color
                    Constants.STATUS_DECLINED -> R.drawable.bg_status_busy // Reuse busy color
                    else -> R.drawable.bg_status_pending
                }
            )

            // Image loading
            Glide.with(context)
                .load(booking.machineImage)
                .placeholder(R.color.divider)
                .centerCrop()
                .into(ivMachine)

            // Action Buttons
            if (booking.status == Constants.STATUS_PENDING) {
                layoutActions.visibility = View.VISIBLE
            } else {
                layoutActions.visibility = View.GONE
            }

            btnAccept.setOnClickListener { onStatusChange(booking, Constants.STATUS_ACCEPTED) }
            btnDecline.setOnClickListener { onStatusChange(booking, Constants.STATUS_DECLINED) }
        }
    }

    override fun getItemCount() = bookings.size

    fun updateData(newBookings: List<Booking>) {
        this.bookings = newBookings
        notifyDataSetChanged()
    }
}

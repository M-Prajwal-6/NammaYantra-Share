package com.nammayantra.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nammayantra.R
import com.nammayantra.databinding.ItemMachineBinding
import com.nammayantra.models.Machine
import com.nammayantra.utils.Constants
import com.nammayantra.utils.LocationUtils

class MachineAdapter(
    private var machines: List<Machine>,
    private val userLat: Double = 0.0,
    private val userLon: Double = 0.0,
    private val isOwnerMode: Boolean = false,
    private val onMachineClick: (Machine) -> Unit,
    private val onEditClick: ((Machine) -> Unit)? = null,
    private val onDeleteClick: ((Machine) -> Unit)? = null
) : RecyclerView.Adapter<MachineAdapter.MachineViewHolder>() {

    class MachineViewHolder(val binding: ItemMachineBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MachineViewHolder {
        val binding = ItemMachineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MachineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MachineViewHolder, position: Int) {
        val machine = machines[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvName.text = machine.name
            tvPrice.text = "₹ ${machine.hourlyRate.toInt()} / hr"
            tvLocation.text = machine.locationName
            
            // Display Condition Rating (Owner's Rating) and Review Count
            tvRating.text = "${machine.conditionRating}"
            tvOwner.text = "by ${machine.ownerName}"

            // Availability Status
            chipStatus.text = machine.availability
            val statusColor = when (machine.availability) {
                Constants.AVAILABILITY_AVAILABLE -> R.color.status_available
                Constants.AVAILABILITY_BUSY -> R.color.status_busy
                else -> R.color.status_maintenance
            }
            chipStatus.setChipBackgroundColorResource(statusColor)

            // Distance calculation
            if (userLat != 0.0 && userLon != 0.0) {
                val distance = LocationUtils.calculateDistance(
                    userLat, userLon, machine.latitude, machine.longitude
                )
                tvDistance.text = LocationUtils.formatDistance(distance)
            } else {
                tvDistance.text = "Location unknown"
            }

            // Image loading with Glide
            val firstImage = machine.images.firstOrNull() ?: ""
            Glide.with(context)
                .load(firstImage)
                .placeholder(R.color.divider)
                .error(R.drawable.bg_gradient_overlay) // Fallback
                .centerCrop()
                .into(ivMachine)

            // Owner Actions
            if (isOwnerMode) {
                layoutOwnerActions.visibility = View.VISIBLE
                btnEdit.setOnClickListener { onEditClick?.invoke(machine) }
                btnDelete.setOnClickListener { onDeleteClick?.invoke(machine) }
            } else {
                layoutOwnerActions.visibility = View.GONE
            }

            root.setOnClickListener { onMachineClick(machine) }
        }
    }

    override fun getItemCount() = machines.size

    fun updateData(newMachines: List<Machine>) {
        this.machines = newMachines
        notifyDataSetChanged()
    }
}

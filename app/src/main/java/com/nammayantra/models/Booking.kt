package com.nammayantra.models

data class Booking(
    var id: String = "",
    var machineId: String = "",
    var machineName: String = "",
    var machineImage: String = "",
    var ownerId: String = "",
    var renterId: String = "",
    var renterName: String = "",
    var renterPhone: String = "",
    var renterAddress: String = "",
    var status: String = "Pending", // Pending, Accepted, Declined, Completed, Cancelled

    var startDate: String = "",
    var startTime: String = "",
    var duration: Double = 0.0, // in hours or days depending on type
    var durationUnit: String = "Hours", // Hours, Days
    var totalPrice: Double = 0.0,
    var timestamp: Long = System.currentTimeMillis(),
    var rejectionReason: String = ""
)

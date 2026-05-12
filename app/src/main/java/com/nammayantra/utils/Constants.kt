package com.nammayantra.utils

object Constants {
    // Firestore Collections
    const val USERS = "users"
    const val MACHINES = "machines"
    const val BOOKINGS = "bookings"
    const val REVIEWS = "reviews"
    const val NOTIFICATIONS = "notifications"

    // Booking Statuses
    const val STATUS_PENDING = "Pending"
    const val STATUS_ACCEPTED = "Accepted"
    const val STATUS_DECLINED = "Declined"
    const val STATUS_COMPLETED = "Completed"
    const val STATUS_CANCELLED = "Cancelled"

    // Machine Availability
    const val AVAILABILITY_AVAILABLE = "Available"
    const val AVAILABILITY_BUSY = "Busy"
    const val AVAILABILITY_MAINTENANCE = "Maintenance"

    // User Roles
    const val ROLE_OWNER = "OWNER"
    const val ROLE_FARMER = "FARMER"
}

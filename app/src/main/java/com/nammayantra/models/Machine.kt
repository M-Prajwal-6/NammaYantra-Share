package com.nammayantra.models

data class Machine(
    var id: String = "",
    var ownerId: String = "",
    var ownerName: String = "",
    var ownerPhone: String = "",
    var name: String = "",
    var type: String = "", // Tractor, Harvester, Plough, etc.
    var hourlyRate: Double = 0.0,
    var dailyRate: Double = 0.0,
    var description: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var locationName: String = "",
    var availability: String = "Available", // Available, Busy, Maintenance
    var conditionRating: Float = 0.0f, // 1-5 scale
    var lastServiceDate: String = "Not specified", // Requirement: Machine Health
    var images: List<String> = emptyList(), // Multiple image URLs
    var avgRating: Float = 0.0f,
    var reviewCount: Int = 0,
    var createdAt: Long = System.currentTimeMillis()
)

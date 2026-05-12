package com.nammayantra.models

data class Review(
    var id: String = "",
    var machineId: String = "",
    var userId: String = "",
    var userName: String = "",
    var rating: Float = 0.0f,
    var comment: String = "",
    var timestamp: Long = System.currentTimeMillis()
)

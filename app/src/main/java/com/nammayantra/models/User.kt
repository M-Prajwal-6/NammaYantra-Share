package com.nammayantra.models

data class User(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var role: String = "", // OWNER, FARMER
    var phone: String = "",
    var profileImage: String = "",
    var address: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var fcmToken: String = ""
)

package com.nammayantra.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nammayantra.utils.Constants
import kotlinx.coroutines.tasks.await
import java.util.UUID

object FirebaseHelper {
    const val USERS_COLLECTION = Constants.USERS
    const val MACHINES_COLLECTION = Constants.MACHINES
    const val BOOKINGS_COLLECTION = Constants.BOOKINGS
    const val REVIEWS_COLLECTION = Constants.REVIEWS
    const val NOTIFICATIONS_COLLECTION = Constants.NOTIFICATIONS

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val storage: FirebaseStorage by lazy { 
        FirebaseStorage.getInstance("gs://nammayantra-app.appspot.com") 
    }

    val currentUserId: String?
        get() = auth.currentUser?.uid

    suspend fun uploadImages(uris: List<Uri>, path: String): List<String> {
        val downloadUrls = mutableListOf<String>()
        val storageRef = storage.reference
        for (uri in uris) {
            val fileName = "img_${System.currentTimeMillis()}_${UUID.randomUUID()}.jpg"
            val ref = storageRef.child(path).child(fileName)

            ref.putFile(uri).await()
            val url = ref.downloadUrl.await().toString()
            downloadUrls.add(url)
        }
        return downloadUrls
    }

    /**
     * Helper to get user data once.
     */
    suspend fun getCurrentUserRole(): String? {
        val uid = currentUserId ?: return null
        val doc = firestore.collection(USERS_COLLECTION).document(uid).get().await()
        return doc.getString("role")
    }
}

package com.nammayantra.utils

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object CloudinaryHelper {

    suspend fun uploadImages(uris: List<Uri>): List<String> {
        val uploadedUrls = mutableListOf<String>()
        for (uri in uris) {
            val url = uploadImage(uri)
            uploadedUrls.add(url)
        }
        return uploadedUrls
    }

    private suspend fun uploadImage(uri: Uri): String = suspendCancellableCoroutine { continuation ->
        MediaManager.get().upload(uri)
            .option("folder", "machine_images")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    val url = resultData?.get("secure_url") as? String ?: ""
                    if (continuation.isActive) {
                        continuation.resume(url)
                    }
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    if (continuation.isActive) {
                        continuation.resumeWithException(Exception(error?.description ?: "Unknown Cloudinary error"))
                    }
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            }).dispatch()
    }
}

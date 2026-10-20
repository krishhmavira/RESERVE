package com.example.reserve.data.util

object ErrorMapper {

    fun donationError(action: String, exception: Exception): String {
        val base = FirebaseHelper.mapFirebaseError(exception)
        return when (action) {
            "create" -> "Failed to create donation. $base"
            "update" -> "Failed to update donation. $base"
            "cancel" -> "Failed to cancel donation. $base"
            "load" -> "Failed to load donation. $base"
            else -> base
        }
    }

    fun requestError(action: String, exception: Exception): String {
        val base = FirebaseHelper.mapFirebaseError(exception)
        return when (action) {
            "create" -> "Failed to submit request. $base"
            "cancel" -> "Failed to cancel request. $base"
            "approve" -> "Failed to approve request. $base"
            "reject" -> "Failed to reject request. $base"
            "load" -> "Failed to load request. $base"
            else -> base
        }
    }

    fun imageUploadError(exception: Exception): String {
        val base = FirebaseHelper.mapFirebaseError(exception)
        return "Image upload failed. $base"
    }

    fun profileError(action: String, exception: Exception): String {
        val base = FirebaseHelper.mapFirebaseError(exception)
        return when (action) {
            "load" -> "Failed to load profile. $base"
            "update" -> "Failed to update profile. $base"
            else -> base
        }
    }
}

package com.example.reserve.data.util

import com.google.firebase.auth.FirebaseAuth

object FirebaseHelper {

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

    val currentUserId: String? get() = auth.currentUser?.uid

    val isAuthenticated: Boolean get() = auth.currentUser != null

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User is not authenticated")
    }

    fun mapFirebaseError(exception: Exception): String {
        val message = exception.message ?: return "An unexpected error occurred"
        return when {
            message.contains("PERMISSION_DENIED", ignoreCase = true) ||
                message.contains("permission", ignoreCase = true) ->
                "You don't have permission to perform this action."

            message.contains("UNAVAILABLE", ignoreCase = true) ||
                message.contains("Unable to resolve host", ignoreCase = true) ->
                "Service unavailable. Please check your internet connection."

            message.contains("network error", ignoreCase = true) ||
                message.contains("Failed to connect", ignoreCase = true) ||
                message.contains("Unable to connect", ignoreCase = true) ->
                "Network error. Please check your connection and try again."

            message.contains("NOT_FOUND", ignoreCase = true) ||
                message.contains("No document to update", ignoreCase = true) ->
                "The requested item was not found."

            message.contains("ALREADY_EXISTS", ignoreCase = true) ->
                "This item already exists."

            message.contains("DEADLINE_EXCEEDED", ignoreCase = true) ||
                message.contains("timeout", ignoreCase = true) ->
                "The request timed out. Please try again."

            message.contains("no user record", ignoreCase = true) ->
                "No account found with this email."

            message.contains("password is invalid", ignoreCase = true) ->
                "Incorrect password."

            message.contains("email address is badly formatted", ignoreCase = true) ->
                "Invalid email format."

            message.contains("email address is already in use", ignoreCase = true) ->
                "An account with this email already exists."

            message.contains("too many unsuccessful", ignoreCase = true) ||
                message.contains("RESOURCE_EXHAUSTED", ignoreCase = true) ->
                "Too many attempts. Please try again later."

            message.contains("storage", ignoreCase = true) &&
                message.contains("unauthorized", ignoreCase = true) ->
                "Image upload not authorized. Please try again."

            else -> "Error: ${exception::class.simpleName}: $message"
        }
    }
}

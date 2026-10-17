package com.example.reserve.data.repository

import com.example.reserve.data.model.User
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    fun createUserProfile(
        user: User,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        usersCollection.document(user.userId)
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getUserProfile(
        userId: String,
        onSuccess: (User?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        usersCollection.document(userId)
            .get()
            .addOnSuccessListener { document ->
                onSuccess(document.toObject(User::class.java))
            }
            .addOnFailureListener { onFailure(it) }
    }
}

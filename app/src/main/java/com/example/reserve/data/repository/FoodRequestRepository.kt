package com.example.reserve.data.repository

import com.example.reserve.data.model.FoodRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class FoodRequestRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val requestsCollection = firestore.collection("foodRequests")

    fun createRequest(
        request: FoodRequest,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = if (request.requestId.isNotBlank()) {
            requestsCollection.document(request.requestId)
        } else {
            requestsCollection.document()
        }
        val toSave = if (request.requestId.isBlank()) {
            request.copy(requestId = docRef.id)
        } else {
            request
        }
        docRef.set(toSave)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getRequest(
        requestId: String,
        onSuccess: (FoodRequest?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        requestsCollection.document(requestId)
            .get()
            .addOnSuccessListener { document ->
                onSuccess(document.toObject(FoodRequest::class.java))
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun listenToNgoRequests(
        ngoId: String,
        onUpdate: (List<FoodRequest>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return requestsCollection
            .whereEqualTo("ngoId", ngoId)
            .orderBy("requestTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }
                val requests = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(FoodRequest::class.java)
                } ?: emptyList()
                onUpdate(requests)
            }
    }

    fun checkExistingRequest(
        donationId: String,
        ngoId: String,
        onResult: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        requestsCollection
            .whereEqualTo("donationId", donationId)
            .whereEqualTo("ngoId", ngoId)
            .get()
            .addOnSuccessListener { snapshot ->
                onResult(!snapshot.isEmpty)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateRequestStatus(
        requestId: String,
        status: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        requestsCollection.document(requestId)
            .update("status", status)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}

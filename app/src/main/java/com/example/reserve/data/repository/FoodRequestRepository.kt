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
                val hasActiveRequest = snapshot.documents.any { doc ->
                    val status = doc.getString("status")
                    status != "REJECTED"
                }
                onResult(hasActiveRequest)
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

    fun listenToRequest(
        requestId: String,
        onUpdate: (FoodRequest?) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return requestsCollection.document(requestId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }
                onUpdate(snapshot?.toObject(FoodRequest::class.java))
            }
    }

    fun listenToRequestsForDonation(
        donationId: String,
        onUpdate: (List<FoodRequest>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return requestsCollection
            .whereEqualTo("donationId", donationId)
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

    fun createRequestAndUpdateDonation(
        request: FoodRequest,
        donationStatus: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = requestsCollection.document()
        val toSave = request.copy(requestId = docRef.id)
        val batch = firestore.batch()
        batch.set(docRef, toSave)
        batch.update(
            firestore.collection("donations").document(request.donationId),
            "status", donationStatus
        )
        batch.commit()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun approveRequest(
        requestId: String,
        donationId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        requestsCollection
            .whereEqualTo("donationId", donationId)
            .get()
            .addOnSuccessListener { snapshot ->
                val batch = firestore.batch()
                batch.update(requestsCollection.document(requestId), "status", "APPROVED")
                batch.update(
                    firestore.collection("donations").document(donationId),
                    "status", "APPROVED"
                )
                for (doc in snapshot.documents) {
                    if (doc.id != requestId && doc.getString("status") == "PENDING") {
                        batch.update(requestsCollection.document(doc.id), "status", "REJECTED")
                    }
                }
                batch.commit()
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun rejectRequest(
        requestId: String,
        donationId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val donationDoc = firestore.collection("donations").document(donationId)
        val requestDoc = requestsCollection.document(requestId)

        firestore.runTransaction { transaction ->
            val donationSnapshot = transaction.get(donationDoc)
            val currentStatus = donationSnapshot.getString("status")

            transaction.update(requestDoc, "status", "REJECTED")

            if (currentStatus == "REQUESTED") {
                transaction.update(donationDoc, "status", "AVAILABLE")
            }
        }
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}

package com.example.reserve.data.repository

import com.example.reserve.data.model.Donation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class DonationRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val donationsCollection = firestore.collection("donations")

    fun createDonation(
        donation: Donation,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = if (donation.donationId.isNotBlank()) {
            donationsCollection.document(donation.donationId)
        } else {
            donationsCollection.document()
        }
        val toSave = if (donation.donationId.isBlank()) {
            donation.copy(donationId = docRef.id)
        } else {
            donation
        }
        docRef.set(toSave)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getDonation(
        donationId: String,
        onSuccess: (Donation?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        donationsCollection.document(donationId)
            .get()
            .addOnSuccessListener { document ->
                onSuccess(document.toObject(Donation::class.java))
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun listenToDonorDonations(
        donorId: String,
        onUpdate: (List<Donation>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return donationsCollection
            .whereEqualTo("donorId", donorId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }
                val donations = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Donation::class.java)
                } ?: emptyList()
                onUpdate(donations)
            }
    }

    fun updateDonationStatus(
        donationId: String,
        status: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        donationsCollection.document(donationId)
            .update("status", status)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateDonationField(
        donationId: String,
        fields: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        donationsCollection.document(donationId)
            .update(fields)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}

package com.example.reserve.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class StorageRepository {

    private val storage = FirebaseStorage.getInstance()
    private val donationImagesRef = storage.reference.child("donation_images")

    fun uploadDonationImage(
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val fileName = "${UUID.randomUUID()}.jpg"
        val imageRef = donationImagesRef.child(fileName)

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl
                    .addOnSuccessListener { downloadUri ->
                        onSuccess(downloadUri.toString())
                    }
                    .addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }
}

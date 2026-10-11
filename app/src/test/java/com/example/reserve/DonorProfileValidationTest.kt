package com.example.reserve

import com.example.reserve.data.model.DonorProfile
import com.example.reserve.data.model.DonorType
import com.example.reserve.data.repository.InMemoryDonorProfileRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DonorProfileValidationTest {

    private lateinit var repository: InMemoryDonorProfileRepository

    @Before
    fun setUp() {
        repository = InMemoryDonorProfileRepository()
    }

    @Test
    fun initials_singleName_returnsFirstChar() {
        val profile = DonorProfile(organizationName = "Taj")
        assertEquals("T", profile.initials)
    }

    @Test
    fun initials_multipleWords_returnsFirstTwoChars() {
        val profile = DonorProfile(organizationName = "The Grand Hotel")
        assertEquals("TG", profile.initials)
    }

    @Test
    fun initials_blankOrg_fallbackToContactName() {
        val profile = DonorProfile(organizationName = "", contactName = "Aarav Sharma")
        assertEquals("AS", profile.initials)
    }

    @Test
    fun repository_validUpdate_succeeds() = runBlocking {
        val current = repository.getProfile()
        val updated = current.copy(
            contactName = "Vikram Malhotra",
            organizationName = "Royal Banquet Hall",
            phone = "+91 9988776655",
            address = "10 Residency Road, Bengaluru, Karnataka"
        )

        val result = repository.updateProfile(updated)
        assertTrue("Expected update to succeed", result.isSuccess)
        assertEquals("Vikram Malhotra", repository.getProfile().contactName)
        assertEquals("Royal Banquet Hall", repository.getProfile().organizationName)
    }

    @Test
    fun repository_shortContactName_failsValidation() = runBlocking {
        val current = repository.getProfile()
        val invalid = current.copy(contactName = "A")

        val result = repository.updateProfile(invalid)
        assertFalse("Expected update to fail for short contact name", result.isSuccess)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun repository_invalidPhone_failsValidation() = runBlocking {
        val current = repository.getProfile()
        val invalid = current.copy(phone = "12345") // less than 10 digits

        val result = repository.updateProfile(invalid)
        assertFalse("Expected update to fail for short phone number", result.isSuccess)
    }

    @Test
    fun repository_shortAddress_failsValidation() = runBlocking {
        val current = repository.getProfile()
        val invalid = current.copy(address = "Rd")

        val result = repository.updateProfile(invalid)
        assertFalse("Expected update to fail for short address", result.isSuccess)
    }
}

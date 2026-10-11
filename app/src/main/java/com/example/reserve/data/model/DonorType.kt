package com.example.reserve.data.model

enum class DonorType(val displayName: String) {
    RESTAURANT("Restaurant"),
    HOTEL("Hotel"),
    CATERING("Catering Service"),
    EVENT_ORGANIZER("Event Organizer"),
    SUPERMARKET("Supermarket / Grocery"),
    BAKERY("Bakery / Cafe"),
    INDIVIDUAL("Individual Donor"),
    OTHER("Other");

    companion object {
        fun fromString(value: String): DonorType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) }
                ?: RESTAURANT
        }
    }
}

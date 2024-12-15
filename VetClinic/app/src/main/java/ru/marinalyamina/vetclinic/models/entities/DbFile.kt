package ru.marinalyamina.vetclinic.models.entities

data class DbFile(
    val id: Long? = null,
    val name: String,
    val appointments: List<Appointment> = emptyList()
)

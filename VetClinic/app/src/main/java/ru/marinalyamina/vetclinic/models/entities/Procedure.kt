package ru.marinalyamina.vetclinic.models.entities

data class Procedure(
    var id: Long? = null,
    var name: String,
    var price: Int,
    var appointments: List<Appointment>? = null
)

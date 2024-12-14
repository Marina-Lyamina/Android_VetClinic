package ru.marinalyamina.vetclinic.models.entities

data class Employee(
    var id: Long? = null,
    var user: User? = null,
    var position: Position? = null,
    var appointments: List<Appointment>? = null
)

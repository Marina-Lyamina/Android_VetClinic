package ru.marinalyamina.vetclinic.models.entities

data class Employee(
    val id: Long? = null,
    val description: String? = null,
    val user: User? = null,
    val mainImage: DbFile? = null,
    val position: Position? = null,
    val appointments: List<Appointment> = emptyList(),
    val schedules: List<Schedule> = emptyList()
)

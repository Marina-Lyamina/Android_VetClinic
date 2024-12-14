package ru.marinalyamina.vetclinic.models.entities

import java.time.LocalDateTime

data class Appointment(
    var id: Long? = null,
    var date: LocalDateTime,
    var reason: String? = null,
    var diagnosis: String? = null,
    var medicalPrescription: String? = null,
    var animal: Animal? = null,
    var employees: List<Employee>? = null,
    var procedures: List<Procedure>? = null
)

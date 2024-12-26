package ru.marinalyamina.vetclinic.models.entities

data class Appointment(
    val id: Long? = null,
    val date: String,
    val reason: String? = null,
    val diagnosis: String? = null,
    val medicalPrescription: String? = null,
    val animal: Animal? = null,
    val employees: List<Employee> = emptyList(),
    val procedures: List<Procedure> = emptyList(),
    val files: List<DbFile> = emptyList()
)

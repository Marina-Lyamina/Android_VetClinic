package ru.marinalyamina.vetclinic.models.dtos

data class ScheduleDTO(
    val id: Long,
    val date: String,
    val employeeId: Long
)

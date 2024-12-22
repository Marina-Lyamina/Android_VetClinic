package ru.marinalyamina.vetclinic.models.dtos

import java.time.LocalDateTime

data class ScheduleDTO(
    val id: Long,
//    val date: LocalDateTime,
    val date: String,
    val employeeId: Long
)

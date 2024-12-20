package ru.marinalyamina.vetclinic.models.entities

import java.time.LocalDateTime

data class Schedule(
    val id: Long? = null,
    val date: LocalDateTime,
    val employee: Employee? = null,
    val animal: Animal? = null
)

package ru.marinalyamina.vetclinic.models.entities

data class Schedule(
    val id: Long? = null,
    val date: String,
    val employee: Employee? = null,
    val animal: Animal? = null

)

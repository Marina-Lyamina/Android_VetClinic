package ru.marinalyamina.vetclinic.models.entities

data class DbFile(
    val id: Long? = null,
    val name: String,
    val date: String,
    val content: String
)

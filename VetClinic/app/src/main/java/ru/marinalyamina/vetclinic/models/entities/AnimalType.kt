package ru.marinalyamina.vetclinic.models.entities

data class AnimalType(
    var id: Long? = null,
    var name: String,
    var animals: List<Animal>? = null
)

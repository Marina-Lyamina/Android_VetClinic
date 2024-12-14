package ru.marinalyamina.vetclinic.models.entities

data class Client(
    var id: Long? = null,
    var user: User? = null,
    var animals: List<Animal>? = null
)

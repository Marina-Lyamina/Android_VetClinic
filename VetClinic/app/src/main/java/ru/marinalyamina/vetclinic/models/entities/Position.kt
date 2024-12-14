package ru.marinalyamina.vetclinic.models.entities

data class Position(
    var id: Long? = null,
    var name: String,
    var employees: List<Employee>? = null
)

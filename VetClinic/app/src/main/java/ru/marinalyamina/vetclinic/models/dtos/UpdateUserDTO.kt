package ru.marinalyamina.vetclinic.models.dtos

data class UpdateUserDTO (
    val surname : String,
    val name : String,
    val patronymic : String,
    val birthday : String,
    val email : String,
    val phone : String,
    val username : String
)
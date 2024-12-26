package ru.marinalyamina.vetclinic.models.dtos

data class CreateFileDTO (
    val fileContent : String,
    val fileExtensions : String
)
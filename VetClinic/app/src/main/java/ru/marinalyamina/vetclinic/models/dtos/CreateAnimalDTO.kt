package ru.marinalyamina.vetclinic.models.dtos

import ru.marinalyamina.vetclinic.models.enums.AnimalGender

data class CreateAnimalDTO (
    val name: String,
    //val birthday: LocalDate,
    val birthday: String,
    val gender: AnimalGender,
    var breed: String? = null,
    val animalTypeId: Long?
)
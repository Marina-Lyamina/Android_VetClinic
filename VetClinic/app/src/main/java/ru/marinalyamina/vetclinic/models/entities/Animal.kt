package ru.marinalyamina.vetclinic.models.entities

import ru.marinalyamina.vetclinic.models.enums.AnimalGender
import java.time.LocalDate

data class Animal(
    var id: Long? = null,
    var name: String,
    var birthday: LocalDate? = null,
    var gender: AnimalGender? = null,
    var breed: String? = null,
    var animalType: AnimalType? = null,
    var client: Client? = null,
    var appointments: List<Appointment>? = null
)

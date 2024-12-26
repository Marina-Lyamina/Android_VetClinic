package ru.marinalyamina.vetclinic.models.entities

import ru.marinalyamina.vetclinic.models.enums.AnimalGender
import java.io.Serializable

data class Animal(
    val id: Long? = null,
    val name: String,
    val birthday: String? = null,
    val gender: AnimalGender? = null,
    val breed: String? = null,
    val mainImage: DbFile? = null,
    val animalType: AnimalType? = null,
    val client: Client? = null,
    val appointments: List<Appointment> = emptyList(),
    val schedules: List<Schedule> = emptyList()
): Serializable

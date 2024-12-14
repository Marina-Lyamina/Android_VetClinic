package ru.marinalyamina.vetclinic.models.entities

import java.time.LocalDate

data class User(
    var id: Long? = null,
    var surname: String,
    var name: String,
    var patronymic: String? = null,
    var birthday: LocalDate? = null,
    var email: String? = null,
    var phone: String? = null,

)

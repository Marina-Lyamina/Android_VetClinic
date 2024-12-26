package ru.marinalyamina.vetclinic.models.entities

data class User(
    var id: Long?,
    var surname: String,
    var name: String,
    var patronymic: String? = null,
    val birthday: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var username: String,
    var password: String
) {

    val shortFullName: String
        get() {
            val patronymicInitial = patronymic?.takeIf { it.isNotBlank() }?.get(0)?.uppercase() ?: ""
            return "${surname} ${name[0].uppercase()}.$patronymicInitial."
        }

    val fullName: String
        get() {
            val patronymicPart = patronymic?.takeIf { it.isNotBlank() } ?: ""
            return "${surname} ${name} $patronymicPart"
        }
}

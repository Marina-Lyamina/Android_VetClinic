package ru.marinalyamina.vetclinic.models.entities

data class User(
    var id: Long? = null,
    var surname: String,
    var name: String,
    var patronymic: String? = null,
    //TODO
//    val birthday: LocalDate? = null,
    val birthday: String? = null,
    var email: String? = null,
    var phone: String? = null
){
    val shortFullName: String
        get() = "${surname} ${name[0].uppercase()}.${patronymic?.get(0)?.uppercase()}."

    val fullName: String
        get() = "${surname} ${name} ${patronymic}"
}

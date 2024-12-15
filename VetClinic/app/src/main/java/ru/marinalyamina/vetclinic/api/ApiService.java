package ru.marinalyamina.vetclinic.api;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import ru.marinalyamina.vetclinic.models.entities.Animal;
import ru.marinalyamina.vetclinic.models.entities.Procedure;

public interface ApiService {
    //список услуг
    @GET("/api/procedures")
    Call<List<Procedure>> getAllProcedures();

    // список животных для текущего пользователя с будущими приемами
    @GET("/api/animals/currentUser")
    Call<List<Animal>> getAnimalsWithFutureAppointments();

//    // список животных для конкретного пользователя по userId
//    @GET("/api/animals/user/{userId}")
//    Call<List<Animal>> getAnimalsByUserId(@Path("userId") Long userId);

    // Получить конкретное животное по id
    @GET("/api/animals/{id}")
    Call<Animal> getAnimalById(@Path("id") Long id);
}
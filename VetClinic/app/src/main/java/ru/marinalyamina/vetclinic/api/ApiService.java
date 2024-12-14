package ru.marinalyamina.vetclinic.api;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import ru.marinalyamina.vetclinic.models.entities.Procedure;

public interface ApiService {
    @GET("/api/procedures")
    Call<List<Procedure>> getAllProcedures();
}
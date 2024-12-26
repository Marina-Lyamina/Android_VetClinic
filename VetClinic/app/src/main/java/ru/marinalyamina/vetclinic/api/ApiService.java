package ru.marinalyamina.vetclinic.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import ru.marinalyamina.vetclinic.models.dtos.CreateAnimalScheduleDTO;
import ru.marinalyamina.vetclinic.models.dtos.CreateFileDTO;
import ru.marinalyamina.vetclinic.models.dtos.LoginDTO;
import ru.marinalyamina.vetclinic.models.dtos.ScheduleDTO;
import ru.marinalyamina.vetclinic.models.dtos.UpdateUserDTO;
import ru.marinalyamina.vetclinic.models.entities.Animal;
import ru.marinalyamina.vetclinic.models.entities.AnimalType;
import ru.marinalyamina.vetclinic.models.dtos.CreateAnimalDTO;
import ru.marinalyamina.vetclinic.models.entities.Appointment;
import ru.marinalyamina.vetclinic.models.entities.Client;
import ru.marinalyamina.vetclinic.models.entities.Employee;
import ru.marinalyamina.vetclinic.models.entities.Procedure;
import ru.marinalyamina.vetclinic.models.entities.Schedule;
import ru.marinalyamina.vetclinic.models.entities.User;

public interface ApiService {
    //список услуг
    @GET("/api/procedures")
    Call<List<Procedure>> getAllProcedures();

    //список видов животных
    @GET("/api/animaltypes")
    Call<List<AnimalType>> getAllAnimalTypes();

    // список животных для текущего пользователя с предстоящими приемами
    @GET("/api/animals/currentUser")
    Call<List<Animal>> getAnimalsWithFutureAppointments();

    // животное по id
    @GET("/api/animals/{id}")
    Call<Animal> getAnimalById(@Path("id") Long id);

    //создание животного
    @PUT("/api/animals/{id}")
    Call<Animal> updateAnimal(@Path("id") Long id, @Body CreateAnimalDTO createAnimalDTO);

    // список врачей
    @GET("/api/employees")
    Call<List<Employee>> getAllEmployees();

    //врач по id
    @GET("/api/employees/{id}")
    Call<Employee> getEmployeeById(@Path("id") Long id);

    @GET("/api/employees/{id}/free-schedules")
    Call<List<ScheduleDTO>> getEmployeeFreeSchedule(@Path("id") Long id);

    @POST("/api/clients/login")
    Call<Long> login(@Body LoginDTO loginDTO);

    @POST("/api/clients/register")
    Call<Long> registration(@Body User user);

    //запись по id
    @GET("/api/schedules/{id}")
    Call<Schedule> getScheduleById(@Path("id") Long id);

    @POST("/api/schedules/animals")
    Call<Long> createAnimalSchedule(@Body CreateAnimalScheduleDTO animalScheduleDTO);

    //создание питомца
    @POST("/api/animals")
    Call<Animal> createAnimal(@Body CreateAnimalDTO animalDTO);

    //получение клиента
    @GET("/api/clients/currentUser")
    Call<Client> getCurrentUser();

    @DELETE("/api/animals/{id}")
    Call<Void> deleteAnimal(@Path("id") Long id);

    @PUT("/api/animals/{id}/image")
    Call<Void> updateAnimalMainImage(@Path("id") Long id, @Body CreateFileDTO fileDTO);

    @PUT("/api/clients/currentUser")
    Call<Client> updateCurrentUser(@Body UpdateUserDTO userDTO);

    @GET("/api/appointments/{id}")
    Call<Appointment> getAppointmentById(@Path("id") Long id);

}
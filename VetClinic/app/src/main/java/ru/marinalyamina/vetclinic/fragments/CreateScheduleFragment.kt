package ru.marinalyamina.vetclinic.fragments
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.adapters.ScheduleAnimalsAdapter
import ru.marinalyamina.vetclinic.api.ApiService
import ru.marinalyamina.vetclinic.api.RetrofitClient
import ru.marinalyamina.vetclinic.models.dtos.CreateAnimalScheduleDTO
import ru.marinalyamina.vetclinic.models.entities.Animal
import ru.marinalyamina.vetclinic.models.entities.Schedule
import java.time.LocalDateTime
import androidx.appcompat.app.AlertDialog
import java.time.format.DateTimeFormatter

class CreateScheduleFragment : Fragment(R.layout.fragment_create_schedule) {

    private var scheduleId: Long? = null
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduleId = arguments?.getLong("scheduleId")
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textViewEmployeeName: TextView = view.findViewById(R.id.textViewEmployeeName)
        val textViewDateSchedule: TextView = view.findViewById(R.id.textViewDateSchedule)
        val recyclerScheduleAnimal: RecyclerView = view.findViewById(R.id.recyclerScheduleAnimal)
        val noAnimalsText: TextView = view.findViewById(R.id.noAnimalsText)  // TextView для сообщения, если нет животных

        recyclerScheduleAnimal.layoutManager = LinearLayoutManager(requireContext())

        scheduleId?.let { id ->
            apiService.getScheduleById(id).enqueue(object : Callback<Schedule> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call<Schedule>, response: Response<Schedule>) {
                    if (response.isSuccessful) {
                        val schedule = response.body()
                        schedule?.let {
                            val localDateTime = LocalDateTime.parse(it.date)
                            val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                            textViewEmployeeName.text = "Врач: ${it.employee?.user?.fullName}"
                            textViewDateSchedule.text = "Дата: ${dateFormatter.format(localDateTime)}"

                            fetchAnimals(recyclerScheduleAnimal, noAnimalsText)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Ошибка загрузки расписания", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Schedule>, t: Throwable) {
                    Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun fetchAnimals(recyclerView: RecyclerView, noAnimalsText: TextView) {
        apiService.animalsWithFutureAppointments.enqueue(object : Callback<List<Animal>> {
            override fun onResponse(call: Call<List<Animal>>, response: Response<List<Animal>>) {
                if (response.isSuccessful && response.body() != null) {
                    val animals = response.body()!!
                    if (animals.isNotEmpty()) {
                        noAnimalsText.visibility = View.GONE
                        val scheduleAnimalsAdapter = ScheduleAnimalsAdapter(animals) { selectedAnimal ->
                            createScheduleForAnimal(selectedAnimal)
                        }
                        recyclerView.adapter = scheduleAnimalsAdapter
                    } else {
                        noAnimalsText.visibility = View.VISIBLE
                        recyclerView.adapter = null
                    }
                } else {
                    Toast.makeText(requireContext(), "Ошибка загрузки животных", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Animal>>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createScheduleForAnimal(selectedAnimal: Animal) {
        showConfirmationDialog(selectedAnimal)
    }

    private fun showConfirmationDialog(selectedAnimal: Animal) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Вы уверены, что хотите записать питомца ко кличке ${selectedAnimal.name} на приём?")
            .setCancelable(false)
            .setPositiveButton("Да") { dialog, id ->
                scheduleId?.let { id ->
                    val createAnimalScheduleDTO = selectedAnimal.id?.let { CreateAnimalScheduleDTO(id, it) }

                    apiService.createAnimalSchedule(createAnimalScheduleDTO).enqueue(object : Callback<Long> {
                        override fun onResponse(call: Call<Long>, response: Response<Long>) {
                            if (response.isSuccessful) {
                                Toast.makeText(requireContext(), "Запись успешно создана", Toast.LENGTH_SHORT).show()
                                navigateToAnimalDetails(selectedAnimal)
                            } else {
                                Toast.makeText(requireContext(), "Ошибка при создании записи", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Long>, t: Throwable) {
                            Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
            .setNegativeButton("Нет") { dialog, id ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }

    private fun navigateToAnimalDetails(animal: Animal) {
        val fragment = AnimalDetailsFragment()

        val bundle = Bundle().apply {
            animal.id?.let { putLong("animalId", it) } // Передаем только ID
        }
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .addToBackStack(null)
            .commit()
    }

}



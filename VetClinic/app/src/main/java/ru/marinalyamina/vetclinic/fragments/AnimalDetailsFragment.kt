package ru.marinalyamina.vetclinic.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.api.ApiService
import ru.marinalyamina.vetclinic.api.RetrofitClient
import ru.marinalyamina.vetclinic.databinding.FragmentAccountBinding
import ru.marinalyamina.vetclinic.models.entities.Animal
import ru.marinalyamina.vetclinic.models.enums.AnimalGender
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AnimalDetailsFragment : Fragment(R.layout.fragment_animal_details) {

    private var animalId: Long? = null
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        animalId = arguments?.getLong("animalId")
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateToolbarTitle(getString(R.string.title_animal_details))

        //элементы xml
        val imageViewAnimal : ImageView = view.findViewById(R.id.imageViewAnimal)
        val textViewAnimalName: TextView = view.findViewById(R.id.textViewAnimalName)
        val textViewAnimalType: TextView = view.findViewById(R.id.textViewAnimalType)
        val textViewAnimalBreed: TextView = view.findViewById(R.id.textViewAnimalBreed)
        val textViewAnimalGender: TextView = view.findViewById(R.id.textViewAnimalGender)
        val textViewAnimalBirthday: TextView = view.findViewById(R.id.textViewAnimalBirthday)

        val textViewUpcomingSchedulesTitle : TextView = view.findViewById(R.id.textViewUpcomingSchedulesTitle)
        val upcomingSchedulesContainer : LinearLayout = view.findViewById(R.id.upcomingSchedulesContainer)

        val allAppointmentsContainer: LinearLayout = view.findViewById(R.id.allAppointmentsContainer)

        val buttonUpdate: Button = view.findViewById(R.id.buttonUpdate)
        val buttonDelete: Button = view.findViewById(R.id.buttonDelete)

        animalId?.let { id ->
            loadAnimalDetails(
                id,
                imageViewAnimal,
                textViewAnimalName,
                textViewAnimalType,
                textViewAnimalBreed,
                textViewAnimalGender,
                textViewAnimalBirthday,
                textViewUpcomingSchedulesTitle,
                upcomingSchedulesContainer,
                allAppointmentsContainer
            )
        } ?: Log.e("AnimalDetailsFragment", "Animal ID is null")

        buttonUpdate.setOnClickListener {
            navigateToFragment(AnimalUpdateFragment().apply {
                arguments = Bundle().apply {
                    putLong("animalId", animalId ?: return@setOnClickListener)
                }
            })
        }

        buttonDelete.setOnClickListener {
            navigateToFragment(AnimalDeleteFragment().apply {
                arguments = Bundle().apply {
                    putLong("animalId", animalId ?: return@setOnClickListener)
                }
            })
        }

    }

    private fun loadAnimalDetails(
        id: Long,
        imageViewAnimal: ImageView,
        textViewAnimalName: TextView,
        textViewAnimalType: TextView,
        textViewAnimalBreed: TextView,
        textViewAnimalGender: TextView,
        textViewAnimalBirthday: TextView,
        textViewUpcomingSchedulesTitle: TextView,
        upcomingSchedulesContainer: LinearLayout,
        allAppointmentsContainer: LinearLayout
    ) {
        val call = apiService.getAnimalById(id)

        call.enqueue(object : Callback<Animal> {
            @RequiresApi(Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<Animal>, response: Response<Animal>) {
                if (response.isSuccessful) {
                    val animal = response.body()
                    if (animal != null) {

                        // Заполнить данные
                        imageViewAnimal.setImageResource(R.drawable.animal_default) // изображение по умолчанию

                        textViewAnimalName.text = animal.name
                        textViewAnimalType.text = "Вид животного: ${animal.animalType?.name?.lowercase() ?: "-"}"
                        textViewAnimalBreed.text = "Порода: ${animal.breed ?: "-"}"
                        textViewAnimalGender.text = "Пол: ${getGenderInRussian(animal.gender)}"

                        val localDate = LocalDate.parse(animal.birthday)
                        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                        textViewAnimalBirthday.text = "Дата рождения: ${formatter.format(localDate)}"

                        // Обработка предстоящих приемов
                        textViewUpcomingSchedulesTitle.visibility = View.VISIBLE
                        upcomingSchedulesContainer.removeAllViews()

                        if (animal.schedules.isNotEmpty()) {
                            // Если есть предстоящие приемы
                            for (schedule in animal.schedules) {
                                val scheduleView = TextView(context).apply {
                                    val localDateTime = LocalDateTime.parse(schedule.date)
                                    val appointmentFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                                    text = "${appointmentFormatter.format(localDateTime)} — ${schedule.employee?.user?.shortFullName}, ${schedule.employee?.position?.name?.lowercase()}"
                                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                                    textSize = 17f
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    ).apply {
                                        setMargins(0, 0, 0, 4)
                                    }
                                }
                                upcomingSchedulesContainer.addView(scheduleView)
                            }
                        } else {
                            // Если нет предстоящих приемов
                            val noAppointmentsMessage = TextView(context).apply {
                                text = "Нет предстоящих приемов"
                                setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                                textSize = 16f
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    setMargins(0, 0, 0, 4)
                                }
                            }
                            upcomingSchedulesContainer.addView(noAppointmentsMessage)
                        }
                        allAppointmentsContainer.removeAllViews()

                        if (animal.appointments.isNotEmpty()) {
                            for (appointment in animal.appointments) {
                                val appointmentView = LinearLayout(context).apply {
                                    orientation = LinearLayout.HORIZONTAL
                                    gravity = android.view.Gravity.CENTER_VERTICAL
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    ).apply {
                                        setMargins(0, 0, 0, 8)
                                    }
                                }

                                val appointmentTextView = TextView(context).apply {
                                    val localDateTime = LocalDateTime.parse(appointment.date)
                                    val appointmentFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                                    //employees[0]
                                    text = "${appointmentFormatter.format(localDateTime)} — ${appointment.employees[0].user?.shortFullName}"
                                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                                    textSize = 17f
                                    layoutParams = LinearLayout.LayoutParams(
                                        0,
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        1f
                                    )
                                }

                                val viewDetailsButton = TextView(context).apply {
                                    text = "Подробнее"
                                    setTextColor(ContextCompat.getColor(requireContext(), R.color.purple1))
                                    textSize = 17f
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                                    setOnClickListener {
                                        // Обработка перехода на подробности
                                        Toast.makeText(context, "Переход к деталям", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                val separator = View(context).apply {
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        1
                                    ).apply {
                                        setMargins(0, 12, 0, 12)
                                    }
                                    setBackgroundResource(R.drawable.line_purple2)
                                }

                                appointmentView.addView(appointmentTextView)
                                appointmentView.addView(viewDetailsButton)
                                allAppointmentsContainer.addView(appointmentView)
                                allAppointmentsContainer.addView(separator)
                            }
                        } else {
                            val noAppointmentsMessage = TextView(context).apply {
                                text = "Нет приемов"
                                setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                                textSize = 16f
                            }
                            allAppointmentsContainer.addView(noAppointmentsMessage)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Животное не найдено", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("AnimalDetailsFragment", "Ошибка: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Animal>, t: Throwable) {
                Log.e("AnimalDetailsFragment", "Ошибка подключения: ${t.message}")
                Toast.makeText(requireContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /*private fun deleteAnimal(animalId: Long?) {
        animalId?.let { id ->
            val call = apiService.deleteAnimalById(id)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Животное удалено", Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressed()
                    } else {
                        Log.e("AnimalDetailsFragment", "Ошибка удаления: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("AnimalDetailsFragment", "Ошибка подключения при удалении: ${t.message}")
                    Toast.makeText(requireContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show()
                }
            })
        } ?: Toast.makeText(requireContext(), "ID животного отсутствует", Toast.LENGTH_SHORT).show()
    }*/

    fun getGenderInRussian(gender: AnimalGender?): String {
        return when (gender) {
            AnimalGender.Male -> "мужской"
            AnimalGender.Female -> "женский"
            AnimalGender.Other -> "другой"
            else -> "-"
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun updateToolbarTitle(title: String) {
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = title
    }
}

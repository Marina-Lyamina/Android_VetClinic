package ru.marinalyamina.vetclinic.fragments

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.api.ApiService
import ru.marinalyamina.vetclinic.api.RetrofitClient
import ru.marinalyamina.vetclinic.models.dtos.CreateAnimalDTO
import ru.marinalyamina.vetclinic.models.entities.Animal
import ru.marinalyamina.vetclinic.models.entities.AnimalType
import ru.marinalyamina.vetclinic.models.enums.AnimalGender
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class AnimalCreateFragment : Fragment(R.layout.fragment_animal_create) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateToolbarTitle(getString(R.string.title_animal_create))

        val editTextName: EditText = view.findViewById(R.id.editTextName)
        val editTextBirthDate: EditText = view.findViewById(R.id.editTextBirthDate)
        val spinnerAnimalType: Spinner = view.findViewById(R.id.spinnerAnimalType)
        val radioGroupGender: RadioGroup = view.findViewById(R.id.radioGroupGender)
        val editTextBreed: EditText = view.findViewById(R.id.editTextBreed)
        val buttonSave: Button = view.findViewById(R.id.buttonSaveAnimal)
        val buttonCancel: Button = view.findViewById(R.id.buttonCancel)

        radioGroupGender.check(R.id.radioOther)

        var animalTypeList: List<AnimalType> = listOf()

        fetchAnimalTypes { animalTypes ->
            animalTypeList = animalTypes
            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, animalTypes.map { it.name })
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerAnimalType.adapter = spinnerAdapter
        }

        editTextBirthDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireActivity(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val localDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val formattedDate = localDate.format(formatter)
                    editTextBirthDate.setText(formattedDate)
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        buttonSave.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val birthDate = editTextBirthDate.text.toString().trim()
            val breed = editTextBreed.text.toString().trim()
            val selectedAnimalTypeName = spinnerAnimalType.selectedItem.toString().trim()
            val genderId = radioGroupGender.checkedRadioButtonId
            val gender = when (genderId) {
                R.id.radioMale -> AnimalGender.Male
                R.id.radioFemale -> AnimalGender.Female
                else -> AnimalGender.Other
            }


            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните обязательные поля", Toast.LENGTH_SHORT).show()
            } else {
                val animalTypeId = animalTypeList.firstOrNull { it.name == selectedAnimalTypeName }?.id ?: 0L

                if (animalTypeId == 0L) {
                    Toast.makeText(requireContext(), "Ошибка определения типа животного", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val animalDTO = CreateAnimalDTO(name, birthDate, gender, breed, animalTypeId)

                if(animalDTO.breed.isNullOrBlank()) animalDTO.breed = null

                saveAnimal(animalDTO)
            }
        }

        buttonCancel.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun updateToolbarTitle(title: String) {
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = title
    }

    private fun fetchAnimalTypes(callback: (List<AnimalType>) -> Unit) {
        val apiService = RetrofitClient.getRetrofitInstance().create(ApiService::class.java)
        val call = apiService.getAllAnimalTypes()

        call.enqueue(object : Callback<List<AnimalType>> {
            override fun onResponse(call: Call<List<AnimalType>>, response: Response<List<AnimalType>>) {
                if (response.isSuccessful && response.body() != null) {
                    callback(response.body()!!)
                } else {
                    Toast.makeText(requireContext(), "Ошибка загрузки типов животных", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<AnimalType>>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveAnimal(animalDTO: CreateAnimalDTO) {
        val apiService = RetrofitClient.getRetrofitInstance().create(ApiService::class.java)
        val call = apiService.createAnimal(animalDTO)

        call.enqueue(object : Callback<Animal> {
            override fun onResponse(call: Call<Animal>, response: Response<Animal>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Животное успешно добавлено", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                } else {
                    Toast.makeText(requireContext(), "Ошибка при добавлении животного", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Animal>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
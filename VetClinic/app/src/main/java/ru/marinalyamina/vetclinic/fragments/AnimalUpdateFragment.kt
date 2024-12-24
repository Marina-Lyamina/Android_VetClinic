package ru.marinalyamina.vetclinic.fragments

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.api.ApiService
import ru.marinalyamina.vetclinic.api.RetrofitClient
import ru.marinalyamina.vetclinic.models.entities.Animal
import ru.marinalyamina.vetclinic.models.entities.AnimalType
import ru.marinalyamina.vetclinic.models.enums.AnimalGender

class AnimalUpdateFragment : Fragment(R.layout.fragment_animal_update) {

    private var animalId: Long? = null
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        animalId = arguments?.getLong("animalId")
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateToolbarTitle(getString(R.string.title_animal_update))

        val editTextName: EditText = view.findViewById(R.id.editTextName)
        val editTextBirthDate: EditText = view.findViewById(R.id.editTextBirthDate)
        val spinnerAnimalType: Spinner = view.findViewById(R.id.spinnerAnimalType)
        val radioGroupGender: RadioGroup = view.findViewById(R.id.radioGroupGender)
        val editTextBreed: EditText = view.findViewById(R.id.editTextBreed)
        val buttonSave: Button = view.findViewById(R.id.buttonSave)
        val buttonCancel: Button = view.findViewById(R.id.buttonCancel)

        animalId?.let { id ->
            loadAnimalDetails(id, editTextName, editTextBirthDate, spinnerAnimalType, radioGroupGender, editTextBreed)
        }

        buttonSave.setOnClickListener {
            val updatedAnimal = collectAnimalData(editTextName, editTextBirthDate, spinnerAnimalType, radioGroupGender, editTextBreed)
            animalId?.let { id ->
                saveAnimalData(id, updatedAnimal)
            }
        }

        buttonCancel.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun loadAnimalDetails(
        id: Long,
        editTextName: EditText,
        editTextBirthDate: EditText,
        spinnerAnimalType: Spinner,
        radioGroupGender: RadioGroup,
        editTextBreed: EditText
    ) {
        apiService.getAnimalById(id).enqueue(object : retrofit2.Callback<Animal> {
            override fun onResponse(call: retrofit2.Call<Animal>, response: retrofit2.Response<Animal>) {
                if (response.isSuccessful) {
                    response.body()?.let { animal ->
                        editTextName.setText(animal.name)
                        editTextBirthDate.setText(animal.birthday)

                        val genderId = when (animal.gender) {
                            AnimalGender.Male -> R.id.radioMale
                            AnimalGender.Female -> R.id.radioFemale
                            else -> R.id.radioAnother
                        }
                        radioGroupGender.check(genderId)

                        editTextBreed.setText(animal.breed)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<Animal>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun collectAnimalData(
        editTextName: EditText,
        editTextBirthDate: EditText,
        spinnerAnimalType: Spinner,
        radioGroupGender: RadioGroup,
        editTextBreed: EditText
    ): Animal {
        val name = editTextName.text.toString()
        val birthDate = editTextBirthDate.text.toString()
        val animalType = spinnerAnimalType.selectedItem as AnimalType
        val gender = when (radioGroupGender.checkedRadioButtonId) {
            R.id.radioMale -> AnimalGender.Male
            R.id.radioFemale -> AnimalGender.Female
            else -> AnimalGender.Other
        }
        val breed = editTextBreed.text.toString()

        return Animal(
            id = animalId,
            name = name,
            birthday = birthDate,
            animalType = animalType,
            gender = gender,
            breed = breed
        )
    }


    private fun saveAnimalData(id: Long, updatedAnimal: Animal) {
        /*apiService.updateAnimal(id, updatedAnimal).enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Животное обновлено", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                } else {
                    Toast.makeText(requireContext(), "Ошибка обновления", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show()
            }
        })*/
    }

    private fun updateToolbarTitle(title: String) {
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = title
    }

    private fun loadAnimalTypes(spinner: Spinner) {
        apiService.getAllAnimalTypes().enqueue(object : retrofit2.Callback<List<AnimalType>> {
            override fun onResponse(call: retrofit2.Call<List<AnimalType>>, response: retrofit2.Response<List<AnimalType>>) {
                if (response.isSuccessful) {
                    response.body()?.let { animalTypes ->
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, animalTypes)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<List<AnimalType>>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка загрузки типов животных", Toast.LENGTH_SHORT).show()
            }
        })
    }

}

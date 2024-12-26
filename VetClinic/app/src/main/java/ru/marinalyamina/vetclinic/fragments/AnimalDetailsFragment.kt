package ru.marinalyamina.vetclinic.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import ru.marinalyamina.vetclinic.models.dtos.CreateFileDTO
import ru.marinalyamina.vetclinic.models.entities.Animal
import ru.marinalyamina.vetclinic.models.entities.Appointment
import ru.marinalyamina.vetclinic.models.enums.AnimalGender
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AnimalDetailsFragment : Fragment(R.layout.fragment_animal_details) {

    private var animalId: Long? = null
    private lateinit var serverAnimal : Animal
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
        val buttonUpdatePhoto: Button = view.findViewById(R.id.buttonUpdatePhoto)

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
            animalId?.let {
                if(serverAnimal.schedules.isNotEmpty() || serverAnimal.appointments.isNotEmpty()){
                    val dialog = AlertDialog.Builder(requireContext())
                        .setTitle("Предупреждение")
                        .setMessage("Нельзя удалить питомца, т.к. у него есть приемы и/или записи на приемы")
                        .setPositiveButton("Ок") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()

                    dialog.show()
                }
                else{
                    val dialog = AlertDialog.Builder(requireContext())
                        .setTitle("Подтверждение удаления")
                        .setMessage("Вы уверены, что хотите удалить питомца?")
                        .setPositiveButton("Да") { _, _ ->
                            deleteAnimal(it)
                        }
                        .setNegativeButton("Нет") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()

                    dialog.show()
                }

            } ?: run {
                Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }


        buttonUpdatePhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

    }

    private fun deleteAnimal(animalId: Long) {
        val apiService = RetrofitClient.getRetrofitInstance().create(ApiService::class.java)
        val call = apiService.deleteAnimal(animalId)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Питомец удален", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, HomeFragment())
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(context, "Ошибка при удалении питомца: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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

                        serverAnimal = animal

                        if (animal.mainImage?.content.isNullOrBlank()) {
                            // изображение по умолчанию
                            imageViewAnimal.setImageResource(R.drawable.animal_default)
                        } else {
                            try {
                                val byteArray = animal.mainImage?.content?.let { Base64.decode(it, Base64.DEFAULT) }

                                if (byteArray != null) {
                                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

                                    if (bitmap != null) {
                                        imageViewAnimal.setImageBitmap(bitmap)
                                    } else {
                                        imageViewAnimal.setImageResource(R.drawable.animal_default)
                                    }
                                } else {
                                    imageViewAnimal.setImageResource(R.drawable.animal_default)
                                }
                            } catch (e: Exception) {
                                imageViewAnimal.setImageResource(R.drawable.animal_default)
                                Log.e("AnimalDetailsFragment", "Ошибка при загрузке изображения: ${e.message}")
                            }
                        }


                        textViewAnimalName.text = animal.name
                        textViewAnimalType.text = "Вид животного: ${animal.animalType?.name?.lowercase() ?: "-"}"
                        textViewAnimalBreed.text = "Порода: ${animal.breed ?: "-"}"
                        textViewAnimalGender.text = "Пол: ${getGenderInRussian(animal.gender)}"

                        val birthdayText = animal.birthday
                        if (!birthdayText.isNullOrEmpty()) {
                            try {
                                val localDate = LocalDate.parse(birthdayText, DateTimeFormatter.ISO_DATE)
                                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                                textViewAnimalBirthday.text = "Дата рождения: ${formatter.format(localDate)}"
                            } catch (e: Exception) {
                                textViewAnimalBirthday.text = "Дата рождения: неверный формат"
                            }
                        } else {
                            textViewAnimalBirthday.text = "Дата рождения: -"
                        }


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
                                        navigateToAppointmentDetails(appointment)
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

    private fun navigateToAppointmentDetails(appointment: Appointment) {
        val fragment = AppointmentDetailsFragment()

        val bundle = Bundle().apply {
            appointment.id?.let { putLong("appointmentId", it) }
        }
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun getGenderInRussian(gender: AnimalGender?): String {
        return when (gender) {
            AnimalGender.Male -> "мужской"
            AnimalGender.Female -> "женский"
            AnimalGender.Other -> "-"
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

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val byteArray = inputStream?.readBytes()
                inputStream?.close()

                val fileContent = Base64.encodeToString(byteArray, Base64.DEFAULT)

                var fileExtension = ".png"
                val mimeType = requireContext().contentResolver.getType(uri)
                fileExtension = mimeType?.let {
                    MimeTypeMap.getSingleton().getExtensionFromMimeType(it)
                } ?: ".png"

                if (fileExtension.isEmpty()) {
                    val path = uri.path
                    path?.let {
                        val dotIndex = it.lastIndexOf('.')
                        if (dotIndex != -1) {
                            fileExtension = it.substring(dotIndex)
                        }
                    }
                }

                uploadImage(fileContent, fileExtension)

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка при чтении изображения", Toast.LENGTH_SHORT).show()
                Log.e("AnimalDetailsFragment", "Ошибка: ${e.message}")
            }
        }
    }

    private fun uploadImage(fileContent: String, fileExtension: String) {
        animalId?.let { id ->
            val fileDTO = CreateFileDTO(fileContent, fileExtension)

            val call = apiService.updateAnimalMainImage(id, fileDTO)

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Изображение обновлено успешно", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Ошибка при обновлении изображения", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("AnimalDetailsFragment", "Ошибка подключения: ${t.message}")
                    Toast.makeText(requireContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show()
                }
            })
        } ?: Toast.makeText(requireContext(), "ID животного отсутствует", Toast.LENGTH_SHORT).show()
    }
}

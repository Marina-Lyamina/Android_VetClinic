package ru.marinalyamina.vetclinic.fragments

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.api.ApiService
import ru.marinalyamina.vetclinic.api.RetrofitClient
import ru.marinalyamina.vetclinic.models.entities.Appointment
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AppointmentDetailsFragment : Fragment(R.layout.fragment_appointment_details) {

    private var appointmentId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appointmentId = arguments?.getLong("appointmentId")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateToolbarTitle(getString(R.string.title_appointment_details))

        // Проверяем, передан ли ID приема
        appointmentId?.let { id ->
            fetchAppointmentDetails(id, view)
        } ?: run {
            Toast.makeText(requireContext(), "ID приема не найден", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchAppointmentDetails(id: Long, view: View) {
        val apiService = RetrofitClient.getRetrofitInstance().create(ApiService::class.java)
        val call = apiService.getAppointmentById(id)

        call.enqueue(object : Callback<Appointment> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<Appointment>, response: Response<Appointment>) {
                if (response.isSuccessful) {
                    response.body()?.let { appointment ->
                        populateAppointmentDetails(appointment, view)
                    }
                } else {
                    Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Appointment>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка подключения: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun populateAppointmentDetails(appointment: Appointment, view: View) {
        val localDate = LocalDateTime.parse(appointment.date, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        val formattedDate = localDate.format(formatter)
        view.findViewById<TextView>(R.id.appointmentDate).text = "Дата приема: $formattedDate"


        view.findViewById<TextView>(R.id.appointmentReason).text = "Причина: ${appointment.reason}"
        view.findViewById<TextView>(R.id.appointmentDiagnosis).text = "Диагноз: ${appointment.diagnosis}"
        view.findViewById<TextView>(R.id.appointmentMedicalPrescription).text =
            "Рекомендации: ${appointment.medicalPrescription}"

        // Врачи
        val employeesContainer = view.findViewById<LinearLayout>(R.id.employeesContainer)
        employeesContainer.removeAllViews()

        appointment.employees.forEach { employee ->
            val employeeTextView = TextView(requireContext()).apply {
                text = "${employee.user?.fullName}, ${employee.position?.name?.lowercase()}"
                textSize = 18f
                setTextColor(resources.getColor(R.color.black))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 8
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
            employeesContainer.addView(employeeTextView)
            employeesContainer.addView(separator)
        }

        val procedureContainer = view.findViewById<LinearLayout>(R.id.procedureContainer)
        procedureContainer.removeAllViews()

        appointment.procedures.forEach { procedure ->
            val procedureTextView = TextView(requireContext()).apply {
                text = procedure.name
                textSize = 18f
                setTextColor(resources.getColor(R.color.black))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 8
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
            procedureContainer.addView(procedureTextView)
            procedureContainer.addView(separator)
        }

        if (appointment.files.isEmpty()) {
            view.findViewById<TextView>(R.id.title_photo_appointment).visibility = View.GONE
            view.findViewById<LinearLayout>(R.id.photosContainer).visibility = View.GONE
        } else {
            view.findViewById<TextView>(R.id.title_photo_appointment).visibility = View.VISIBLE
            val photosContainer = view.findViewById<LinearLayout>(R.id.photosContainer)
            photosContainer.visibility = View.VISIBLE
            photosContainer.removeAllViews()

            appointment.files.forEach { file ->
                try {
                    val byteArray = file.content.let { Base64.decode(it, Base64.DEFAULT) }

                    if (byteArray != null) {
                        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

                        val imageView = ImageView(requireContext()).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                300,
                               300
                            ).apply {
                                setMargins(0, 8, 0, 8)
                            }
                            scaleType = ImageView.ScaleType.CENTER_CROP
                            setImageBitmap(bitmap)
                            setOnClickListener {
                                ImageDialogFragment(bitmap).show(parentFragmentManager, "imageDialog")
                            }
                        }
                        photosContainer.addView(imageView)
                    } else {
                        Log.e("AppointmentDetails", "Bitmap оказался null для файла: ${file.name}")
                    }
                } catch (e: Exception) {
                    Log.e("AppointmentDetails", "Ошибка обработки файла: ${e.message}")
                }
            }
        }
    }

    private fun updateToolbarTitle(title: String) {
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = title
    }
}

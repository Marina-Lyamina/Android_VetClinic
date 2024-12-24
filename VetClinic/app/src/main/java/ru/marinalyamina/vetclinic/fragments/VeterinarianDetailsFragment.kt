package ru.marinalyamina.vetclinic.fragments

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.adapters.ScheduleAdapter
import ru.marinalyamina.vetclinic.api.ApiService
import ru.marinalyamina.vetclinic.api.RetrofitClient
import ru.marinalyamina.vetclinic.models.dtos.ScheduleDTO
import ru.marinalyamina.vetclinic.models.entities.Employee

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class VeterinarianDetailsFragment : Fragment(R.layout.fragment_veterinarian_details) {

    private var employeeId: Long? = null
    private lateinit var apiService: ApiService
    private lateinit var textViewEmployeeName: TextView
    private lateinit var imageViewEmployee: ImageView
    private lateinit var textViewEmployeePosition: TextView
    private lateinit var textViewEmployeeDescription: TextView
    private lateinit var textNotFreeSchedules: TextView
    private lateinit var calendarView: CalendarView
    private lateinit var schedules: List<ScheduleDTO>
    private lateinit var recyclerButtonsSchedules: RecyclerView
    private lateinit var scheduleAdapter: ScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        employeeId = arguments?.getLong("employeeId")
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateToolbarTitle(getString(R.string.title_veterinarian_details))

        // Инициализация элементов UI
        textViewEmployeeName = view.findViewById(R.id.textViewEmployeeName)
        imageViewEmployee = view.findViewById(R.id.imageViewEmployee)
        textViewEmployeePosition = view.findViewById(R.id.textViewEmployeePosition)
        textNotFreeSchedules = view.findViewById(R.id.textNotFreeSchedules)
        textViewEmployeeDescription = view.findViewById(R.id.textViewEmployeeDescription)
        calendarView = view.findViewById(R.id.calendarView)
        recyclerButtonsSchedules = view.findViewById(R.id.recyclerButtonsSchedules)

        recyclerButtonsSchedules.layoutManager = LinearLayoutManager(context)

        scheduleAdapter = ScheduleAdapter(emptyList()){ selectedSchedule ->
            navigateToCreateSchedule(selectedSchedule)
        }
        recyclerButtonsSchedules.adapter = scheduleAdapter

        loadEmployeeDetails()
        loadSchedules()

        // Обработчик изменения выбранной даты
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            onChangeDateSchedule(year, month, dayOfMonth)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onChangeDateSchedule (year: Int, month: Int, dayOfMonth: Int) {
        val dateSchedules = schedules.filter { schedule ->
            val scheduleDate = LocalDateTime.parse(schedule.date)
            scheduleDate.year == year &&
                    scheduleDate.month.value == (month + 1) &&
                    scheduleDate.dayOfMonth == dayOfMonth
        }
        scheduleAdapter.updateSchedules(dateSchedules)

        if(dateSchedules.isEmpty()){
            textNotFreeSchedules.visibility = android.view.View.VISIBLE
        }
        else{
            textNotFreeSchedules.visibility = android.view.View.INVISIBLE
        }

    }

    private fun loadEmployeeDetails() {
        employeeId?.let { id ->
            apiService.getEmployeeById(id).enqueue(object : Callback<Employee> {
                override fun onResponse(call: Call<Employee>, response: Response<Employee>) {
                    if (response.isSuccessful) {
                        response.body()?.let { employee ->
                            updateUI(employee)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Employee>, t: Throwable) {
                    Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun loadSchedules() {
        employeeId?.let { id ->
            apiService.getEmployeeFreeSchedule(id).enqueue(object : Callback<List<ScheduleDTO>> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call<List<ScheduleDTO>>, response: Response<List<ScheduleDTO>>) {
                    if (response.isSuccessful) {
                        schedules = response.body() ?: emptyList()
//                        highlightDates()
                        scheduleAdapter = ScheduleAdapter(schedules){ selectedSchedule ->
                            navigateToCreateSchedule(selectedSchedule)}
                        recyclerButtonsSchedules.adapter = scheduleAdapter

                        onChangeDateSchedule(LocalDateTime.now().year, LocalDateTime.now().month.value, LocalDateTime.now().dayOfMonth)
                    } else {
                        Toast.makeText(requireContext(), "Ошибка загрузки расписания", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<ScheduleDTO>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun updateUI(employee: Employee) {
        textViewEmployeeName.text = employee.user?.fullName
        textViewEmployeePosition.text = employee.position?.name
        textViewEmployeeDescription.text = employee.description
    }

    private fun navigateToCreateSchedule(scheduleDTO: ScheduleDTO) {
        val fragment = CreateScheduleFragment()

        val bundle = Bundle().apply {
            putLong("scheduleId", scheduleDTO.id)
        }
        fragment.arguments = bundle

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



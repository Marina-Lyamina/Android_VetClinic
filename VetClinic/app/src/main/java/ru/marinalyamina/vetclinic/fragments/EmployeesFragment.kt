package ru.marinalyamina.vetclinic.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.adapters.EmployeeAdapter
import ru.marinalyamina.vetclinic.api.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.marinalyamina.vetclinic.api.RetrofitClient
import ru.marinalyamina.vetclinic.models.entities.Employee

class EmployeesFragment : Fragment(R.layout.fragment_employees) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EmployeeAdapter
    private val employeeList = mutableListOf<Employee>()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewVeterinarians)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = EmployeeAdapter(requireContext(), employeeList){ selectedVeterinarian ->
            navigateToVeterinarianDetails(selectedVeterinarian)
        }
        recyclerView.adapter = adapter


        fetchEmployees()
    }

    private fun navigateToVeterinarianDetails(employee: Employee) {
        val fragment = EmployeeDetailsFragment()

        val bundle = Bundle().apply {
            employee.id?.let { putLong("employeeId", it) } // Передаем только ID
        }
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun fetchEmployees() {
        try {
            val apiService = RetrofitClient.getRetrofitInstance().create(ApiService::class.java)
            val call = apiService.getAllEmployees()

            call.enqueue(object : Callback<List<Employee>> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<List<Employee>>, response: Response<List<Employee>>) {
                    if (response.isSuccessful && response.body() != null) {
                        employeeList.clear()
                        employeeList.addAll(response.body()!!)
                        adapter.notifyDataSetChanged()
                    } else {
                        Log.e("VeterinariansFragment", "Ошибка: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<Employee>>, t: Throwable) {
                    Log.e("VeterinariansFragment", "Ошибка: ${t.message}")
                    Toast.makeText(requireContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Log.e("VeterinariansFragment", "Exception: ${e.message}")
        }
    }
}

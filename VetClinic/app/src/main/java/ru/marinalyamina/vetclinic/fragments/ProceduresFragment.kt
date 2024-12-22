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
import ru.marinalyamina.vetclinic.adapters.ProceduresAdapter
import ru.marinalyamina.vetclinic.api.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.marinalyamina.vetclinic.api.RetrofitClient
import ru.marinalyamina.vetclinic.models.entities.Procedure

class ProceduresFragment : Fragment(R.layout.fragment_procedures) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProceduresAdapter
    private val proceduresList = mutableListOf<Procedure>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewProcedures)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProceduresAdapter(proceduresList)
        recyclerView.adapter = adapter

        // Загрузка данных
        fetchProcedures()
    }

    private fun fetchProcedures() {
        try {
            val apiService = RetrofitClient.getRetrofitInstance().create(ApiService::class.java)
            val call = apiService.allProcedures

            // Выполнение сетевого запроса
            call.enqueue(object : Callback<List<Procedure>> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<List<Procedure>>, response: Response<List<Procedure>>) {
                    if (!isAdded) return

                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (!responseBody.isNullOrEmpty()) {
                            proceduresList.clear()
                            proceduresList.addAll(responseBody)
                            adapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(requireContext(), "Список процедур пуст", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Обработка ошибок API
                        handleApiError(response.code())
                    }
                }

                override fun onFailure(call: Call<List<Procedure>>, t: Throwable) {
                    if (!isAdded) return
                    Log.e("ProceduresFragment", "Ошибка подключения: ${t.message}")
                    Toast.makeText(requireContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Log.e("ProceduresFragment", "Exception: ${e.message}")
        }
    }

    private fun handleApiError(code: Int) {
        when (code) {
            404 -> Toast.makeText(requireContext(), "Данные не найдены (404)", Toast.LENGTH_SHORT).show()
            500 -> Toast.makeText(requireContext(), "Ошибка сервера (500)", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(requireContext(), "Неизвестная ошибка: $code", Toast.LENGTH_SHORT).show()
        }
        Log.e("ProceduresFragment", "API Error Code: $code")
    }
}

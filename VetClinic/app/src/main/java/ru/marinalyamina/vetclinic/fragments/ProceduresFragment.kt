
package ru.marinalyamina.vetclinic.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.adapters.ProcedureAdapter
import ru.marinalyamina.vetclinic.api.ApiService
import ru.marinalyamina.vetclinic.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.marinalyamina.vetclinic.models.entities.Procedure

class ProceduresFragment : Fragment(R.layout.fragment_procedures) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProcedureAdapter
    private val proceduresList = mutableListOf<Procedure>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewProcedures)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ProcedureAdapter(requireContext(), proceduresList)
        recyclerView.adapter = adapter

        fetchProcedures()
    }

    private fun fetchProcedures() {
        try {
            val apiService = RetrofitClient.getRetrofitInstance().create(ApiService::class.java)
            val call = apiService.allProcedures
            call.enqueue(object : Callback<List<Procedure>> {
                override fun onResponse(call: Call<List<Procedure>>, response: Response<List<Procedure>>) {
                    if (response.isSuccessful && response.body() != null) {
                        proceduresList.clear()
                        proceduresList.addAll(response.body()!!)
                        adapter.notifyDataSetChanged()
                    } else {
                        Log.e("ProceduresFragment", "Ошибка: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<Procedure>>, t: Throwable) {
                    // Проверка isAdded перед доступом к контексту
                    if (isAdded) {
                        Log.e("ProceduresFragment", "Ошибка: ${t.message}")
                        Toast.makeText(requireContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("ProceduresFragment", "Exception: ${e.message}")
        }
    }
}


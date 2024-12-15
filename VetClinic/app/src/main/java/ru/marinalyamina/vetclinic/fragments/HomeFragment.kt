package ru.marinalyamina.vetclinic.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.adapters.AnimalAdapter
import ru.marinalyamina.vetclinic.api.ApiService
import ru.marinalyamina.vetclinic.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.marinalyamina.vetclinic.models.entities.Animal

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AnimalAdapter
    private val animalList = mutableListOf<Animal>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewAnimals)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = AnimalAdapter(requireContext(), animalList)
        recyclerView.adapter = adapter

        fetchAnimals()
    }

    private fun fetchAnimals() {
        try {
            val apiService = RetrofitClient.getRetrofitInstance().create(ApiService::class.java)
            val call = apiService.getAnimalsWithFutureAppointments()
            call.enqueue(object : Callback<List<Animal>> {
                override fun onResponse(call: Call<List<Animal>>, response: Response<List<Animal>>) {
                    if (response.isSuccessful && response.body() != null) {
                        animalList.clear()
                        animalList.addAll(response.body()!!)
                        adapter.notifyDataSetChanged()
                    } else {
                        Log.e("HomeFragment", "Ошибка: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<Animal>>, t: Throwable) {
                    // Проверка isAdded перед доступом к контексту
                    if (isAdded) {
                        Log.e("HomeFragment", "Ошибка: ${t.message}")
                        Toast.makeText(requireContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("HomeFragment", "Exception: ${e.message}")
        }
    }
}

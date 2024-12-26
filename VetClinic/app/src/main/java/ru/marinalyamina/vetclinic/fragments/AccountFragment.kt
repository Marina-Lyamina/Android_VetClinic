package ru.marinalyamina.vetclinic.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.api.ApiService
import ru.marinalyamina.vetclinic.api.RetrofitClient
import ru.marinalyamina.vetclinic.databinding.FragmentAccountBinding
import ru.marinalyamina.vetclinic.models.entities.Client
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.marinalyamina.vetclinic.activities.LoginActivity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AccountFragment : Fragment(R.layout.fragment_account) {

    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateToolbarTitle(getString(R.string.title_account))

        loadCurrentUser()

        binding.buttonCreateAnimal.setOnClickListener {
            navigateToFragment(AnimalCreateFragment())
        }

        binding.buttonEditData.setOnClickListener {
            navigateToFragment(AccountUpdateFragment())
        }

        binding.buttonLogout.setOnClickListener {
            logoutAndNavigateToLogin()
        }

    }

    private fun logoutAndNavigateToLogin() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.logout_confirmation_title))
            .setMessage(getString(R.string.logout_confirmation_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.clear()
                editor.apply()

                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()
    }



    private fun loadCurrentUser() {
        val apiService = RetrofitClient.getRetrofitInstance().create(ApiService::class.java)
        val call = apiService.getCurrentUser()

        call.enqueue(object : Callback<Client> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<Client>, response: Response<Client>) {
                if (response.isSuccessful) {
                    val client = response.body()
                    if (client != null) {
                        binding.textViewFullName.text = client.user?.fullName ?: "-"
                        if(!client.user?.birthday.isNullOrBlank()){
                            val localDate = LocalDate.parse(client.user?.birthday, DateTimeFormatter.ISO_LOCAL_DATE)
                            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                            val formattedDate = localDate.format(formatter)
                            binding.textViewBirthday.text = formattedDate ?: "-"
                        }
                        else{
                            binding.textViewBirthday.text = "-"
                        }
                        binding.textViewPhoneNumber.text = client.user?.phone ?: "-"
                        binding.textViewEmail.text = client.user?.email ?: "-"
                    } else {
                        Toast.makeText(context, "Данные пользователя отсутствуют", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Ошибка загрузки данных: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Client>, t: Throwable) {
                Toast.makeText(context, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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


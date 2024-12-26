package ru.marinalyamina.vetclinic.fragments
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.api.ApiService
import ru.marinalyamina.vetclinic.api.RetrofitClient
import ru.marinalyamina.vetclinic.models.dtos.UpdateUserDTO
import ru.marinalyamina.vetclinic.models.entities.Client
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AccountUpdateFragment : Fragment(R.layout.fragment_account_update) {

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextName: EditText = view.findViewById(R.id.firstNameInput)
        val editTextSurname: EditText = view.findViewById(R.id.lastNameInput)
        val editTextPatronymic: EditText = view.findViewById(R.id.patronymicInput)
        val editTextBirthday: EditText = view.findViewById(R.id.birthdayInput)
        val editTextEmail: EditText = view.findViewById(R.id.emailInput)
        val editTextPhone: EditText = view.findViewById(R.id.phoneInput)
        val editTextUsername: EditText = view.findViewById(R.id.loginInput)

        val buttonSave: Button = view.findViewById(R.id.buttonSave)
        val buttonCancel: Button = view.findViewById(R.id.buttonCancel)

        loadAccount(editTextName, editTextSurname, editTextPatronymic, editTextBirthday, editTextEmail, editTextPhone, editTextUsername)

        buttonSave.setOnClickListener {
            val updatedUser = collectUserData(editTextName, editTextSurname, editTextPatronymic, editTextBirthday, editTextEmail, editTextPhone, editTextUsername)
            saveUserProfile(updatedUser)
        }

        buttonCancel.setOnClickListener {
            requireActivity().onBackPressed()
        }

        setupDatePicker(editTextBirthday)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupDatePicker(editTextBirthDate: EditText) {
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
                    editTextBirthDate.setText(localDate.format(formatter))
                },
                year, month, day
            )
            datePickerDialog.show()
        }
    }

    private fun loadAccount(
        editTextName: EditText,
        editTextSurname: EditText,
        editTextPatronymic: EditText,
        editTextBirthday: EditText,
        editTextEmail: EditText,
        editTextPhone: EditText,
        editTextUsername : EditText
    ) {
        apiService.getCurrentUser().enqueue(object : retrofit2.Callback<Client> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: retrofit2.Call<Client>, response: retrofit2.Response<Client>) {
                if (response.isSuccessful) {
                    response.body()?.let { client ->
                        editTextName.setText(client.user?.name )
                        editTextSurname.setText(client.user?.surname)
                        editTextPatronymic.setText(client.user?.patronymic)
                        if(!client.user?.birthday.isNullOrBlank()){
                            val localDate = LocalDate.parse(client.user?.birthday, DateTimeFormatter.ISO_LOCAL_DATE)
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            val formattedDate = localDate.format(formatter)
                            editTextBirthday.setText(formattedDate)
                        }
                        editTextEmail.setText(client.user?.email)
                        editTextPhone.setText(client.user?.phone)
                        editTextUsername.setText(client.user?.username)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<Client>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun collectUserData(
        editTextName: EditText,
        editTextSurname: EditText,
        editTextPatronymic: EditText,
        editTextBirthday: EditText,
        editTextEmail: EditText,
        editTextPhone: EditText,
        editTextUsername : EditText
    ): UpdateUserDTO {
        return UpdateUserDTO(
            name = editTextName.text.toString(),
            surname = editTextSurname.text.toString(),
            patronymic = editTextPatronymic.text.toString(),
            birthday = editTextBirthday.text.toString(),
            email = editTextEmail.text.toString(),
            phone = editTextPhone.text.toString(),
            username = editTextUsername.text.toString()
        )
    }

    private fun saveUserProfile(userDTO: UpdateUserDTO) {
        apiService.updateCurrentUser(userDTO).enqueue(object : retrofit2.Callback<Client> {
            override fun onResponse(call: retrofit2.Call<Client>, response: retrofit2.Response<Client>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Профиль успешно обновлен", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                } else {
                    Toast.makeText(requireContext(), "Ошибка обновления профиля", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<Client>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

package ru.marinalyamina.vetclinic.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.api.ApiService
import ru.marinalyamina.vetclinic.api.RetrofitClient
import ru.marinalyamina.vetclinic.models.entities.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class RegistrationActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)

        val firstNameInput: EditText = findViewById(R.id.firstNameInput)
        val lastNameInput: EditText = findViewById(R.id.lastNameInput)
        val patronymicInput: EditText = findViewById(R.id.patronymicInput)
        val emailInput: EditText = findViewById(R.id.emailInput)
        val phoneInput: EditText = findViewById(R.id.phoneInput)
        val usernameInput: EditText = findViewById(R.id.loginInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        val confirmPasswordInput: EditText = findViewById(R.id.confirmPasswordInput)

        val registerButton: Button = findViewById(R.id.registerButton)

        val birthdayInput: EditText = findViewById(R.id.birthdayInput)

        birthdayInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val localDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)

                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val formattedDate = localDate.format(formatter)

                    birthdayInput.setText(formattedDate)
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        val loginRedirect: TextView = findViewById(R.id.loginRedirect)
        loginRedirect.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        registerButton.setOnClickListener {
            val name = firstNameInput.text.toString().trim()
            val surname = lastNameInput.text.toString().trim()
            val patronymic = patronymicInput.text.toString().trim()
            val birthday = birthdayInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (name.isNotEmpty() && surname.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    val user = User(null, surname, name, patronymic, birthday, email, phone, username, password)

                    if(user.patronymic.isNullOrBlank()) user.patronymic = null
                    if(user.email.isNullOrBlank()) user.email = null
                    if(user.phone.isNullOrBlank()) user.phone = null

                    registerUser(user)
                } else {
                    Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Заполните все обязательные поля", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registration)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun registerUser(user: User) {
        val apiService = RetrofitClient.getRetrofitInstance().create(ApiService::class.java)
        val call = apiService.registration(user)
        val intent = Intent(this, LoginActivity::class.java)

        call.enqueue(object : Callback<Long> {
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                if (response.isSuccessful) {
                    startActivity(intent)
                    Toast.makeText(this@RegistrationActivity, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@RegistrationActivity, "Ошибка регистрации", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                Toast.makeText(this@RegistrationActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

package ru.marinalyamina.vetclinic.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.api.ApiService
import ru.marinalyamina.vetclinic.api.RetrofitClient
import ru.marinalyamina.vetclinic.models.dtos.LoginDTO

class LoginActivity : AppCompatActivity() {

    private lateinit var registerRedirect: TextView
    private lateinit var loginButton: Button
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        registerRedirect = findViewById(R.id.registerRedirect)
        loginButton = findViewById(R.id.loginButton)
        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)

        // Переход к экрану регистрации
        registerRedirect.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        // Обработка нажатия на кнопку "Логин"
        loginButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (validateInput(username, password)) {
                loginUser(username, password)
            }
        }
    }

    // Валидация ввода
    private fun validateInput(username: String, password: String): Boolean {
        return when {
            username.isEmpty() -> {
                showToast(getString(R.string.error_username_empty))
                false
            }
            password.isEmpty() -> {
                showToast(getString(R.string.error_password_empty))
                false
            }
            else -> true
        }
    }

    // Вызов API для логина
    private fun loginUser(username: String, password: String) {
        val apiService = RetrofitClient.getRetrofitInstance().create(ApiService::class.java)
        val loginDTO = LoginDTO(username, password)

        apiService.login(loginDTO).enqueue(object : Callback<Long> {
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                if (response.isSuccessful && response.body() != null) {
                    val userId = response.body()!!
                    showToast(getString(R.string.login_success))

                    // Переход к следующему экрану
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                } else {
                    showToast(getString(R.string.login_failed))
                }
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                showToast(getString(R.string.error_network))
            }
        })
    }

    // Функция для показа Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

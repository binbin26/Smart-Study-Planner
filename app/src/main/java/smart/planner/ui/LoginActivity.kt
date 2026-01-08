package smart.planner.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import smart.planner.MainActivity
import smart.planner.R
import smart.planner.ui.viewmodel.UserViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if already logged in
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

        lifecycleScope.launch {
            if (userViewModel.isLoggedIn()) {
                navigateToMain()
                return@launch
            }
        }

        setContentView(R.layout.activity_login)

        // Views
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // Create mock user on first launch
        lifecycleScope.launch {
            createMockUserIfNotExists()
        }

        // Login button click
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validation
            if (email.isEmpty()) {
                etEmail.error = "Email không được để trống"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Password không được để trống"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            // Perform login
            performLogin(email, password)
        }
    }

    private fun performLogin(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val user = userViewModel.login(email, password)

                if (user != null) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Chào mừng ${user.fullName}! 👋",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to MainActivity
                    navigateToMain()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Email hoặc password không đúng! ❌",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Lỗi đăng nhập: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private suspend fun createMockUserIfNotExists() {
        val existingUser = userViewModel.getUserByEmail("test@test.com")

        if (existingUser == null) {
            val mockUser = userViewModel.register(
                email = "test@test.com",
                password = "123456",
                fullName = "Test User"
            )

            if (mockUser != null) {
                Toast.makeText(
                    this,
                    "Đã tạo tài khoản test 🎉",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, smart.planner.view.HomeActivity::class.java))
        finish()
    }
}
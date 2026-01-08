package smart.planner.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import smart.planner.R
import smart.planner.ui.viewmodel.UserViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Setup ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Cài đặt"

        // Initialize ViewModel
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

        loadUserInfo()
        setupButtons()
    }

    private fun loadUserInfo() {
        lifecycleScope.launch {
            try {
                val userId = userViewModel.getCurrentUserId()

                // TODO: Load user từ database
                // Tạm thời dùng data cứng
                findViewById<TextView>(R.id.tvUserName).text = "Test User"
                findViewById<TextView>(R.id.tvUserEmail).text = "test@test.com"
            } catch (e: Exception) {
                Toast.makeText(this@SettingsActivity, "Lỗi load thông tin: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupButtons() {
        // Nút "Xem thống kê"
        findViewById<Button>(R.id.btnStats).setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
        }

        // Nút "Chỉnh sửa thông tin"
        findViewById<Button>(R.id.btnEditProfile).setOnClickListener {
            Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show()
        }

        // Nút "Đổi mật khẩu"
        findViewById<Button>(R.id.btnChangePassword).setOnClickListener {
            Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show()
        }

        // Nút "Giới thiệu"
        findViewById<Button>(R.id.btnAbout).setOnClickListener {
            showAboutDialog()
        }

        // Nút "Đăng xuất"
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle("📱 Smart Study Planner")
            .setMessage("""
                Version: 1.0.0
                
                Ứng dụng quản lý học tập thông minh
                
                Tính năng:
                ✅ Quản lý tasks
                ✅ Lịch học và deadline
                ✅ Thống kê tiến độ
                ✅ Nhắc nhở deadline
                
                Phát triển bởi: Team Smart Planner
                © 2026
            """.trimIndent())
            .setPositiveButton("Đóng", null)
            .show()
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("⚠️ Đăng xuất")
            .setMessage("Bạn có chắc muốn đăng xuất không?")
            .setPositiveButton("Đăng xuất") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun performLogout() {
        lifecycleScope.launch {
            try {
                // Xóa TẤT CẢ SharedPreferences
                val appPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val userPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

                appPrefs.edit().clear().apply()
                userPrefs.edit().clear().apply()

                // Gọi logout từ ViewModel
                userViewModel.logout()

                Toast.makeText(this@SettingsActivity, "👋 Đã đăng xuất!", Toast.LENGTH_SHORT).show()

                // Quay về Login
                val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@SettingsActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handle back button
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
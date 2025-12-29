package com.example.smartstudyplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

import com.example.smartstudyplanner.ui.theme.SmartStudyPlannerTheme
import com.example.smartstudyplanner.data.database.AppDatabase
import com.example.smartstudyplanner.data.entity.Subject

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ✅ 1. LẤY DATABASE (Singleton)
        val db = AppDatabase.getDatabase(this)

        // ✅ 2. INSERT TEST (CHỈ ĐỂ KIỂM TRA DATABASE)
        lifecycleScope.launch {
            db.subjectDao().insert(
                Subject(
                    name = "Mobile Programming",
                    code = "MOB101",
                    teacher = "Mr A"
                )
            )
        }

        // ✅ 3. UI (KHÔNG LIÊN QUAN DATABASE)
        setContent {
            SmartStudyPlannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SmartStudyPlannerTheme {
        Greeting("Android")
    }
}

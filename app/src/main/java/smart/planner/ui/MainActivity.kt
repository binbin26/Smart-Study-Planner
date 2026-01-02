package smart.planner.smart.planner.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import smart.planner.data.test.CoroutinesIOTest

// This is a placeholder theme. You can customize it in another file (e.g., ui/theme/Theme.kt)
@Composable
fun SmartStudyPlannerTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}

// 1. Change AppCompatActivity to ComponentActivity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Test API calls (có thể comment lại sau khi test xong)
        //smart.planner.data.api.ApiTestExample.testAllApis()
        CoroutinesIOTest.testDispatchersIO()
        // 2. Use setContent for Jetpack Compose
        setContent {
            // 3. Call your Compose theme and content
            SmartStudyPlannerTheme {
                // You can place your main screen composable here
                // For now, we will just show a greeting
                Greeting(name = "User")
            }
        }
    }


}

// A simple example of a Composable function
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

// A preview for the Greeting Composable
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SmartStudyPlannerTheme {
        Greeting("Android")
    }
}

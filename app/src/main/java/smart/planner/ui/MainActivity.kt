package smart.planner

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import smart.planner.ui.AddTaskActivity // Đảm bảo import đúng đường dẫn này

@Composable
fun SmartStudyPlannerTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        smart.planner.data.test.CoroutinesIOTest.testDispatchersIO()

        setContent {
            SmartStudyPlannerTheme {
                // Gọi màn hình chính ở đây
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current // Lấy context để khởi tạo Intent chuyển màn hình

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hello User!",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Nút bấm để mở màn hình Thêm Task
        Button(onClick = {
            val intent = Intent(context, AddTaskActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(text = "Mở Thêm Task")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SmartStudyPlannerTheme {
        MainScreen()
    }
}
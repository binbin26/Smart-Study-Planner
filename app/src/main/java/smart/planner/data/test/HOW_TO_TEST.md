# Hướng Dẫn Test Coroutines IO

## Cách 1: Test trong MainActivity (Đơn giản nhất)

### Bước 1: Mở MainActivity.kt

### Bước 2: Uncomment dòng test trong `onCreate()`:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Test Dispatchers.IO
    smart.planner.data.test.CoroutinesIOTest.testAll()
    
    setContent {
        // ...
    }
}
```

### Bước 3: Chạy app và xem Logcat

1. Mở **Logcat** trong Android Studio
2. Filter theo tag: `CoroutinesIOTest`
3. Chạy app
4. Xem kết quả trong Logcat

## Cách 2: Test trong ViewModel

### Tạo test trong ViewModel:

```kotlin
class TestViewModel : ViewModel() {
    init {
        viewModelScope.launch {
            smart.planner.data.test.CoroutinesIOTest.testAll()
        }
    }
}
```

## Cách 3: Test từ TestScreen

### Thêm button test vào TestScreen:

```kotlin
Button(
    onClick = {
        CoroutineScope(Dispatchers.IO).launch {
            smart.planner.data.test.CoroutinesIOTest.testAll()
        }
    }
) {
    Text("Test Dispatchers.IO")
}
```

## Các Test Có Sẵn

### 1. CoroutinesIOTest.testAll()
- Test Dispatchers.IO cơ bản
- Test thread switching
- Test error handling
- Test multiple operations

### 2. CoroutinesIOTest.testDispatchersIO()
- Test verify đang chạy trên IO thread
- Test error handling
- Test withContext switching

### 3. CoroutinesIOTest.testErrorHandling()
- Test error handling với Dispatchers.IO

### 4. CoroutinesIOTest.testMultipleOperations()
- Test nhiều operations chạy song song

## Kiểm Tra Kết Quả

### ✅ Kết quả đúng sẽ thấy:

```
CoroutinesIOTest: === Testing Dispatchers.IO ===
CoroutinesIOTest: ✅ Current thread: DefaultDispatcher-worker-1
CoroutinesIOTest: ✅ Is IO thread: true
CoroutinesIOTest: ✅ Result: Operation completed
```

### ❌ Nếu thấy Main thread:

```
CoroutinesIOTest: Current thread: main
CoroutinesIOTest: ❌ Is IO thread: false
```

→ Có nghĩa là operations đang chạy trên Main thread (SAI!)

## Filter Logcat

### Filter theo tag:
- `CoroutinesIOTest` - Test Dispatchers.IO

### Filter theo level:
- Chọn **Debug** hoặc **Info** để xem tất cả logs
- Chọn **Error** để chỉ xem lỗi

## Ví Dụ Log Output

### Khi test thành công:

```
CoroutinesIOTest: === Testing Dispatchers.IO ===
CoroutinesIOTest: ✅ Current thread: DefaultDispatcher-worker-1
CoroutinesIOTest: ✅ Is IO thread: true

CoroutinesIOTest: --- Test withContext(Dispatchers.IO) ---
CoroutinesIOTest:   Thread trong withContext(Dispatchers.IO): DefaultDispatcher-worker-2
CoroutinesIOTest:   ✅ Result: Operation completed successfully
CoroutinesIOTest:   ✅ Thread sau withContext: DefaultDispatcher-worker-1
```

## Troubleshooting

### Nếu không thấy logs:

1. **Kiểm tra Logcat filter:**
   - Đảm bảo filter không quá strict
   - Thử clear filter

2. **Kiểm tra log level:**
   - Đảm bảo chọn Debug/Info level

3. **Kiểm tra code:**
   - Đảm bảo đã uncomment dòng test
   - Đảm bảo app đã chạy

### Nếu thấy Main thread:

1. **Kiểm tra Repository:**
   - Đảm bảo có `withContext(Dispatchers.IO)`
   - Đảm bảo không gọi trực tiếp từ Main thread

2. **Kiểm tra ViewModel:**
   - Đảm bảo dùng `viewModelScope.launch`
   - Không dùng `Dispatchers.Main` trực tiếp

## Tips

1. **Test từng phần:**
   - Test Dispatchers.IO trước
   - Sau đó test Repository
   - Cuối cùng test kết hợp

2. **Xem thread names:**
   - Thread names chứa "DefaultDispatcher-worker" = IO thread ✅
   - Thread name = "main" = Main thread ❌

3. **Log Pattern:**
   - ✅ Prefix = IO thread hoạt động đúng
   - ❌ Error = Có vấn đề với Dispatchers


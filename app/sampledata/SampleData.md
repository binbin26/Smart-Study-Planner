# Bộ Dữ Liệu Mẫu - Smart Study Planner

## Cách 1: Sử dụng Android Studio Database Inspector

1. Mở Android Studio
2. Chạy app trên emulator hoặc thiết bị
3. Mở **View > Tool Windows > App Inspection**
4. Chọn tab **Database Inspector**
5. Chọn database `app_database`
6. Chạy các câu lệnh SQL sau trong Query tab:

### Bước 1: Thêm Subjects (5 môn học với màu sắc khác nhau)

```sql
-- Môn 1: Lập Trình Mobile (Xanh dương)
INSERT INTO subjects (id, name, code, teacher, color, userId, createdAt, updatedAt)
VALUES (1, 'Lập Trình Mobile', 'CS301', 'TS. Nguyễn Văn A', '#2196F3', 1,
        strftime('%s','now') * 1000, strftime('%s','now') * 1000);

-- Môn 2: Cơ Sở Dữ Liệu (Xanh lá)
INSERT INTO subjects (id, name, code, teacher, color, userId, createdAt, updatedAt)
VALUES (2, 'Cơ Sở Dữ Liệu', 'CS302', 'PGS. Trần Thị B', '#4CAF50', 1,
        strftime('%s','now') * 1000, strftime('%s','now') * 1000);

-- Môn 3: Mạng Máy Tính (Cam)
INSERT INTO subjects (id, name, code, teacher, color, userId, createdAt, updatedAt)
VALUES (3, 'Mạng Máy Tính', 'CS303', 'ThS. Lê Văn C', '#FF9800', 1,
        strftime('%s','now') * 1000, strftime('%s','now') * 1000);

-- Môn 4: Trí Tuệ Nhân Tạo (Tím)
INSERT INTO subjects (id, name, code, teacher, color, userId, createdAt, updatedAt)
VALUES (4, 'Trí Tuệ Nhân Tạo', 'CS304', 'GS. Phạm Thị D', '#9C27B0', 1,
        strftime('%s','now') * 1000, strftime('%s','now') * 1000);

-- Môn 5: An Toàn Thông Tin (Đỏ)
INSERT INTO subjects (id, name, code, teacher, color, userId, createdAt, updatedAt)
VALUES (5, 'An Toàn Thông Tin', 'CS305', 'TS. Hoàng Văn E', '#F44336', 1,
        strftime('%s','now') * 1000, strftime('%s','now') * 1000);
```

### Bước 2: Thêm Tasks (25 tasks phân bố trong 7 ngày qua)

**Ngày hôm nay (5 tasks):**
```sql
-- Task 1: DONE
INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Hoàn thiện UI Dashboard', 'Thiết kế và implement giao diện dashboard với charts',
        strftime('%s','now') * 1000,
        (strftime('%s','now') + 86400) * 1000,
        'DONE', '1',
        strftime('%s','now') * 1000, '');

-- Task 2: IN_PROGRESS
INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Thiết kế ERD Database', 'Vẽ sơ đồ Entity-Relationship cho hệ thống',
        strftime('%s','now') * 1000,
        (strftime('%s','now') + 172800) * 1000,
        'IN_PROGRESS', '2',
        strftime('%s','now') * 1000, '');

-- Task 3: TODO
INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Cấu hình Firewall', 'Thiết lập quy tắc firewall cho mạng doanh nghiệp',
        strftime('%s','now') * 1000,
        (strftime('%s','now') + 259200) * 1000,
        'TODO', '3',
        strftime('%s','now') * 1000, '');

-- Task 4: DONE
INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Nghiên cứu Neural Networks', 'Đọc tài liệu về mạng neural cơ bản',
        strftime('%s','now') * 1000,
        (strftime('%s','now') + 86400) * 1000,
        'DONE', '4',
        strftime('%s','now') * 1000, '');

-- Task 5: TODO
INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Phân tích lỗ hổng bảo mật', 'Kiểm tra và báo cáo lỗ hổng trong hệ thống',
        strftime('%s','now') * 1000,
        (strftime('%s','now') + 432000) * 1000,
        'TODO', '5',
        strftime('%s','now') * 1000, '');
```

**1 ngày trước (4 tasks):**
```sql
-- 1 ngày trước
INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Code Firebase Integration', 'Tích hợp Firebase Realtime Database vào app',
        (strftime('%s','now') - 86400) * 1000,
        (strftime('%s','now') + 172800) * 1000,
        'DONE', '1',
        (strftime('%s','now') - 86400) * 1000, '');

INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Viết SQL Queries', 'Thực hành các câu lệnh SELECT, JOIN nâng cao',
        (strftime('%s','now') - 86400) * 1000,
        (strftime('%s','now') + 259200) * 1000,
        'DONE', '2',
        (strftime('%s','now') - 86400) * 1000, '');

INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Lab Wireshark', 'Phân tích gói tin mạng bằng Wireshark',
        (strftime('%s','now') - 86400) * 1000,
        (strftime('%s','now') + 345600) * 1000,
        'IN_PROGRESS', '3',
        (strftime('%s','now') - 86400) * 1000, '');

INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Implement Decision Tree', 'Lập trình thuật toán cây quyết định',
        (strftime('%s','now') - 86400) * 1000,
        (strftime('%s','now') + 432000) * 1000,
        'TODO', '4',
        (strftime('%s','now') - 86400) * 1000, '');
```

**2 ngày trước (3 tasks):**
```sql
INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Test Navigation Components', 'Kiểm tra navigation giữa các màn hình',
        (strftime('%s','now') - 172800) * 1000,
        (strftime('%s','now') + 259200) * 1000,
        'DONE', '1',
        (strftime('%s','now') - 172800) * 1000, '');

INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Database Normalization', 'Chuẩn hóa database lên dạng 3NF',
        (strftime('%s','now') - 172800) * 1000,
        (strftime('%s','now') + 432000) * 1000,
        'DONE', '2',
        (strftime('%s','now') - 172800) * 1000, '');

INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Mã hóa RSA', 'Implement thuật toán mã hóa RSA',
        (strftime('%s','now') - 172800) * 1000,
        (strftime('%s','now') + 518400) * 1000,
        'TODO', '5',
        (strftime('%s','now') - 172800) * 1000, '');
```

**3 ngày trước (4 tasks):**
```sql
INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Design Pattern Study', 'Nghiên cứu MVVM, Repository Pattern',
        (strftime('%s','now') - 259200) * 1000,
        (strftime('%s','now') + 172800) * 1000,
        'DONE', '1',
        (strftime('%s','now') - 259200) * 1000, '');

INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Setup Network Topology', 'Thiết kế mô hình mạng LAN/WAN',
        (strftime('%s','now') - 259200) * 1000,
        (strftime('%s','now') + 432000) * 1000,
        'IN_PROGRESS', '3',
        (strftime('%s','now') - 259200) * 1000, '');

INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Machine Learning Review', 'Ôn tập các thuật toán ML cơ bản',
        (strftime('%s','now') - 259200) * 1000,
        (strftime('%s','now') + 518400) * 1000,
        'DONE', '4',
        (strftime('%s','now') - 259200) * 1000, '');

INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Security Audit Report', 'Viết báo cáo kiểm tra bảo mật hệ thống',
        (strftime('%s','now') - 259200) * 1000,
        (strftime('%s','now') + 604800) * 1000,
        'TODO', '5',
        (strftime('%s','now') - 259200) * 1000, '');
```

**4 ngày trước (3 tasks):**
```sql
INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Optimize Database Queries', 'Tối ưu hóa performance cho queries',
        (strftime('%s','now') - 345600) * 1000,
        (strftime('%s','now') + 432000) * 1000,
        'DONE', '2',
        (strftime('%s','now') - 345600) * 1000, '');

INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Config Router Cisco', 'Cấu hình router Cisco cơ bản',
        (strftime('%s','now') - 345600) * 1000,
        (strftime('%s','now') + 518400) * 1000,
        'DONE', '3',
        (strftime('%s','now') - 345600) * 1000, '');

INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Deep Learning Paper', 'Đọc paper về CNN và RNN',
        (strftime('%s','now') - 345600) * 1000,
        (strftime('%s','now') + 604800) * 1000,
        'TODO', '4',
        (strftime('%s','now') - 345600) * 1000, '');
```

**5 ngày trước (3 tasks):**
```sql
INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Build RecyclerView Adapter', 'Tạo adapter cho danh sách tasks',
        (strftime('%s','now') - 432000) * 1000,
        (strftime('%s','now') + 259200) * 1000,
        'DONE', '1',
        (strftime('%s','now') - 432000) * 1000, '');

INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Transaction Management', 'Học về ACID và transaction trong DB',
        (strftime('%s','now') - 432000) * 1000,
        (strftime('%s','now') + 518400) * 1000,
        'DONE', '2',
        (strftime('%s','now') - 432000) * 1000, '');

INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Penetration Testing', 'Thực hành kiểm tra xâm nhập cơ bản',
        (strftime('%s','now') - 432000) * 1000,
        (strftime('%s','now') + 691200) * 1000,
        'IN_PROGRESS', '5',
        (strftime('%s','now') - 432000) * 1000, '');
```

**6 ngày trước (3 tasks):**
```sql
INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Study Kotlin Coroutines', 'Tìm hiểu async programming với Coroutines',
        (strftime('%s','now') - 518400) * 1000,
        (strftime('%s','now') + 345600) * 1000,
        'DONE', '1',
        (strftime('%s','now') - 518400) * 1000, '');

INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('OSI Model Review', 'Ôn tập 7 tầng mô hình OSI',
        (strftime('%s','now') - 518400) * 1000,
        (strftime('%s','now') + 604800) * 1000,
        'DONE', '3',
        (strftime('%s','now') - 518400) * 1000, '');

INSERT INTO tasks (title, description, createdAt, deadline, status, subjectId, updatedAt, firebaseId)
VALUES ('Reinforcement Learning', 'Nghiên cứu Q-Learning algorithm',
        (strftime('%s','now') - 518400) * 1000,
        (strftime('%s','now') + 777600) * 1000,
        'TODO', '4',
        (strftime('%s','now') - 518400) * 1000, '');
```

## Cách 2: Chạy từ code (tạo helper function)

Tạo file `SampleDataHelper.kt` trong package `smart.planner.util`:

```kotlin
package smart.planner.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import smart.planner.data.local.AppDatabase
import smart.planner.data.model.Subject
import smart.planner.data.model.Task
import java.util.concurrent.TimeUnit

class SampleDataHelper {

    suspend fun insertSampleData(database: AppDatabase) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val oneDayMs = TimeUnit.DAYS.toMillis(1)

        // Insert Subjects
        val subjects = listOf(
            Subject(1, "Lập Trình Mobile", "CS301", "TS. Nguyễn Văn A", "#2196F3", 1, now, now),
            Subject(2, "Cơ Sở Dữ Liệu", "CS302", "PGS. Trần Thị B", "#4CAF50", 1, now, now),
            Subject(3, "Mạng Máy Tính", "CS303", "ThS. Lê Văn C", "#FF9800", 1, now, now),
            Subject(4, "Trí Tuệ Nhân Tạo", "CS304", "GS. Phạm Thị D", "#9C27B0", 1, now, now),
            Subject(5, "An Toàn Thông Tin", "CS305", "TS. Hoàng Văn E", "#F44336", 1, now, now)
        )

        subjects.forEach { database.subjectDao().insertSubject(it) }

        // Insert Tasks - distributed over 7 days
        val tasks = listOf(
            // Today (5 tasks)
            Task(0, "", "Hoàn thiện UI Dashboard", "Thiết kế và implement giao diện dashboard", now, now + oneDayMs, "DONE", "1", now),
            Task(0, "", "Thiết kế ERD Database", "Vẽ sơ đồ Entity-Relationship", now, now + 2*oneDayMs, "IN_PROGRESS", "2", now),
            Task(0, "", "Cấu hình Firewall", "Thiết lập quy tắc firewall", now, now + 3*oneDayMs, "TODO", "3", now),
            Task(0, "", "Nghiên cứu Neural Networks", "Đọc tài liệu về mạng neural", now, now + oneDayMs, "DONE", "4", now),
            Task(0, "", "Phân tích lỗ hổng bảo mật", "Kiểm tra và báo cáo lỗ hổng", now, now + 5*oneDayMs, "TODO", "5", now),

            // 1 day ago (4 tasks)
            Task(0, "", "Code Firebase Integration", "Tích hợp Firebase vào app", now - oneDayMs, now + 2*oneDayMs, "DONE", "1", now - oneDayMs),
            Task(0, "", "Viết SQL Queries", "Thực hành SELECT, JOIN", now - oneDayMs, now + 3*oneDayMs, "DONE", "2", now - oneDayMs),
            Task(0, "", "Lab Wireshark", "Phân tích gói tin mạng", now - oneDayMs, now + 4*oneDayMs, "IN_PROGRESS", "3", now - oneDayMs),
            Task(0, "", "Implement Decision Tree", "Lập trình cây quyết định", now - oneDayMs, now + 5*oneDayMs, "TODO", "4", now - oneDayMs),

            // 2 days ago (3 tasks)
            Task(0, "", "Test Navigation Components", "Kiểm tra navigation", now - 2*oneDayMs, now + 3*oneDayMs, "DONE", "1", now - 2*oneDayMs),
            Task(0, "", "Database Normalization", "Chuẩn hóa lên 3NF", now - 2*oneDayMs, now + 5*oneDayMs, "DONE", "2", now - 2*oneDayMs),
            Task(0, "", "Mã hóa RSA", "Implement thuật toán RSA", now - 2*oneDayMs, now + 6*oneDayMs, "TODO", "5", now - 2*oneDayMs),

            // 3 days ago (4 tasks)
            Task(0, "", "Design Pattern Study", "MVVM, Repository Pattern", now - 3*oneDayMs, now + 2*oneDayMs, "DONE", "1", now - 3*oneDayMs),
            Task(0, "", "Setup Network Topology", "Thiết kế LAN/WAN", now - 3*oneDayMs, now + 5*oneDayMs, "IN_PROGRESS", "3", now - 3*oneDayMs),
            Task(0, "", "Machine Learning Review", "Ôn tập thuật toán ML", now - 3*oneDayMs, now + 6*oneDayMs, "DONE", "4", now - 3*oneDayMs),
            Task(0, "", "Security Audit Report", "Báo cáo kiểm tra bảo mật", now - 3*oneDayMs, now + 7*oneDayMs, "TODO", "5", now - 3*oneDayMs),

            // 4 days ago (3 tasks)
            Task(0, "", "Optimize Database Queries", "Tối ưu performance", now - 4*oneDayMs, now + 5*oneDayMs, "DONE", "2", now - 4*oneDayMs),
            Task(0, "", "Config Router Cisco", "Cấu hình router cơ bản", now - 4*oneDayMs, now + 6*oneDayMs, "DONE", "3", now - 4*oneDayMs),
            Task(0, "", "Deep Learning Paper", "Đọc paper CNN và RNN", now - 4*oneDayMs, now + 7*oneDayMs, "TODO", "4", now - 4*oneDayMs),

            // 5 days ago (3 tasks)
            Task(0, "", "Build RecyclerView Adapter", "Tạo adapter cho tasks", now - 5*oneDayMs, now + 3*oneDayMs, "DONE", "1", now - 5*oneDayMs),
            Task(0, "", "Transaction Management", "ACID và transaction", now - 5*oneDayMs, now + 6*oneDayMs, "DONE", "2", now - 5*oneDayMs),
            Task(0, "", "Penetration Testing", "Kiểm tra xâm nhập", now - 5*oneDayMs, now + 8*oneDayMs, "IN_PROGRESS", "5", now - 5*oneDayMs),

            // 6 days ago (3 tasks)
            Task(0, "", "Study Kotlin Coroutines", "Async programming", now - 6*oneDayMs, now + 4*oneDayMs, "DONE", "1", now - 6*oneDayMs),
            Task(0, "", "OSI Model Review", "Ôn tập 7 tầng OSI", now - 6*oneDayMs, now + 7*oneDayMs, "DONE", "3", now - 6*oneDayMs),
            Task(0, "", "Reinforcement Learning", "Q-Learning algorithm", now - 6*oneDayMs, now + 9*oneDayMs, "TODO", "4", now - 6*oneDayMs)
        )

        tasks.forEach { database.taskDao().insertTask(it) }
    }
}
```

Sau đó gọi từ MainActivity hoặc một nơi phù hợp:

```kotlin
// Trong onCreate hoặc một button click
lifecycleScope.launch {
    SampleDataHelper().insertSampleData(database)
    Toast.makeText(this@MainActivity, "✅ Đã thêm dữ liệu mẫu", Toast.LENGTH_SHORT).show()
}
```

## Thống Kê Dữ Liệu Mẫu

### Subjects: 5 môn học
- Lập Trình Mobile (Xanh dương #2196F3)
- Cơ Sở Dữ Liệu (Xanh lá #4CAF50)
- Mạng Máy Tính (Cam #FF9800)
- Trí Tuệ Nhân Tạo (Tím #9C27B0)
- An Toàn Thông Tin (Đỏ #F44336)

### Tasks: 25 tasks
**Phân bố theo trạng thái:**
- ✅ DONE: 15 tasks (60%)
- ⏳ IN_PROGRESS: 5 tasks (20%)
- 📝 TODO: 5 tasks (20%)

**Phân bố theo môn:**
- Lập Trình Mobile: 6 tasks
- Cơ Sở Dữ Liệu: 5 tasks
- Mạng Máy Tính: 5 tasks
- Trí Tuệ Nhân Tạo: 5 tasks
- An Toàn Thông Tin: 4 tasks

**Phân bố theo ngày (7 ngày qua):**
- Hôm nay: 5 tasks
- 1 ngày trước: 4 tasks
- 2 ngày trước: 3 tasks
- 3 ngày trước: 4 tasks
- 4 ngày trước: 3 tasks
- 5 ngày trước: 3 tasks
- 6 ngày trước: 3 tasks

## Kết Quả Mong Đợi Trên Stats Dashboard

Sau khi thêm dữ liệu mẫu, màn hình Stats sẽ hiển thị:

1. **Donut Chart - Hoàn thành:**
   - Tổng: 25 tasks
   - Đã xong: 15 tasks
   - Chờ: 10 tasks
   - Phần trăm: 60%

2. **Bar Chart - Xu hướng 7 ngày:**
   - Hiển thị các cột với chiều cao tương ứng số tasks mỗi ngày
   - Ngày hôm nay được highlight màu xanh

3. **Tasks theo môn:**
   - 5 môn học với màu sắc riêng biệt
   - Progress bar cho từng môn
   - Tỷ lệ hoàn thành khác nhau

4. **Màn hình Home:**
   - Hiển thị "📚 5\nMôn học" (không còn hardcode)

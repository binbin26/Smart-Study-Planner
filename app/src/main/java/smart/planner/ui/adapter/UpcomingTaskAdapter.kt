package smart.planner.ui.adapter


import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import smart.planner.data.model.Task
import smart.planner.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpcomingTaskAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onCheckboxClick: (Task, Boolean) -> Unit
) : ListAdapter<Task, UpcomingTaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding, onTaskClick, onCheckboxClick)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TaskViewHolder(
        private val binding: ItemTaskBinding,
        private val onTaskClick: (Task) -> Unit,
        private val onCheckboxClick: (Task, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.apply {
                // 1. Tắt listener trước khi gán giá trị để tránh bị gọi đè logic khi scroll list
                cbDone.setOnCheckedChangeListener(null)

                tvTaskTitle.text = task.title
                tvDeadline.text = "Deadline: ${formatDeadline(task.deadline)}"
                cbDone.isChecked = task.isCompleted

                // 2. Gọi hàm cập nhật gạch ngang (tách riêng để dùng lại)
                updateStrikeThrough(task.isCompleted)

                root.setOnClickListener { onTaskClick(task) }

                // 3. Xử lý click checkbox
                cbDone.setOnCheckedChangeListener { _, isChecked ->
                    updateStrikeThrough(isChecked) // Gạch ngang ngay lập tức trên giao diện
                    onCheckboxClick(task, isChecked) // Báo về Activity/ViewModel
                }
            }
        }
        private fun updateStrikeThrough(isCompleted: Boolean) {
            binding.apply {
                if (isCompleted) {
                    tvTaskTitle.paintFlags = tvTaskTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    tvTaskTitle.alpha = 0.5f // Làm mờ chữ cho đẹp
                } else {
                    tvTaskTitle.paintFlags = tvTaskTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    tvTaskTitle.alpha = 1.0f // Hiện rõ lại
                }
            }
        }

        private fun formatDeadline(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}
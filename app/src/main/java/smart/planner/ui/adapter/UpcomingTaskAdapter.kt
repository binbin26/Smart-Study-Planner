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
    private val onCheckboxClick: (Task, Boolean) -> Unit,
    private val onDeleteClick: ((Task) -> Unit)? = null  // ✅ THÊM CALLBACK DELETE (optional)
) : ListAdapter<Task, UpcomingTaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding, onTaskClick, onCheckboxClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TaskViewHolder(
        private val binding: ItemTaskBinding,
        private val onTaskClick: (Task) -> Unit,
        private val onCheckboxClick: (Task, Boolean) -> Unit,
        private val onDeleteClick: ((Task) -> Unit)?  // ✅ THÊM PARAMETER
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.apply {
                // 1. Tắt listener trước khi gán giá trị
                checkboxCompleted.setOnCheckedChangeListener(null)

                tvTaskTitle.text = task.title
                tvDeadline.text = "Deadline: ${formatDeadline(task.deadline)}"

                // Hiển thị status
                val isCompleted = task.status == "DONE"
                checkboxCompleted.isChecked = isCompleted

                // 2. Gọi hàm cập nhật gạch ngang
                updateStrikeThrough(isCompleted)

                // 3. Click vào task → Vào detail
                root.setOnClickListener { onTaskClick(task) }

                // 4. Xử lý click checkbox
                checkboxCompleted.setOnCheckedChangeListener { _, isChecked ->
                    updateStrikeThrough(isChecked)
                    onCheckboxClick(task, isChecked)
                }

                // ✅ 5. XỬ LÝ NÚT XÓA
                btnDeleteTask?.setOnClickListener {
                    onDeleteClick?.invoke(task)
                }
            }
        }

        private fun updateStrikeThrough(isCompleted: Boolean) {
            binding.apply {
                if (isCompleted) {
                    tvTaskTitle.paintFlags = tvTaskTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    tvTaskTitle.alpha = 0.5f
                } else {
                    tvTaskTitle.paintFlags = tvTaskTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    tvTaskTitle.alpha = 1.0f
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
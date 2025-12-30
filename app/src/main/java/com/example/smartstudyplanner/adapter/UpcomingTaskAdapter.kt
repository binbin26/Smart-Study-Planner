package smart.planner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import smart.planner.data.Task

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
                tvTaskTitle.text = task.title
                tvDeadline.text = "Deadline: ${formatDeadline(task.deadline)}"
                cbDone.isChecked = task.isCompleted

                // Click vào card → navigate
                root.setOnClickListener {
                    onTaskClick(task)
                }

                // Click checkbox → update status
                cbDone.setOnCheckedChangeListener { _, isChecked ->
                    onCheckboxClick(task, isChecked)
                }

                // Optional: Strike-through text nếu completed
                if (task.isCompleted) {
                    tvTaskTitle.paintFlags = tvTaskTitle.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    tvTaskTitle.paintFlags = tvTaskTitle.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }

                // Optional: Đổi màu deadline nếu gần hết hạn
                val timeRemaining = task.deadline - System.currentTimeMillis()
                if (timeRemaining < 86400000) { // < 1 day
                    tvDeadline.setTextColor(binding.root.context.getColor(android.R.color.holo_red_dark))
                } else {
                    tvDeadline.setTextColor(binding.root.context.getColor(android.R.color.darker_gray))
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
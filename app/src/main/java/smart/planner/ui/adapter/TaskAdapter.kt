package smart.planner.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import smart.planner.R
import smart.planner.data.entity.Task

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var tasks: List<Task> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.title.text = task.title
        holder.subject.text = task.subjectId.toString()
        holder.deadline.text = "Deadline: ${task.deadline}"
        holder.status.text = task.status
        holder.checkBox.isChecked = task.status == "Completed"

        // Cập nhật trạng thái công việc khi checkbox được đánh dấu
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            val updatedTask = task.copy(status = if (isChecked) "Completed" else "TODO")
            // Cập nhật trong cơ sở dữ liệu nếu cần
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun submitList(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    fun getTaskAt(position: Int): Task {
        return tasks[position]
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTaskTitle)
        val subject: TextView = itemView.findViewById(R.id.tvTaskSubject)
        val deadline: TextView = itemView.findViewById(R.id.tvDeadline)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
    }
}

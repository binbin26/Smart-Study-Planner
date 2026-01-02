package smart.planner.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import smart.planner.R
import smart.planner.data.entity.Task
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val onCheckedChange: ((Task, Boolean) -> Unit)? = null
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var tasks: List<Task> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.title.text = task.title
        holder.subject.text = task.subjectName

        holder.deadline.text = "Deadline: ${formatDate(task.deadline)}"

        holder.status.text = if (task.isDone) "Completed" else "Pending"

        // tránh trigger lại listener khi recycle
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = task.isDone

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChange?.invoke(task, isChecked)
        }
    }

    override fun getItemCount(): Int = tasks.size

    fun submitList(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    fun getTaskAt(position: Int): Task = tasks[position]

    private fun formatDate(time: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(time))
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTaskTitle)
        val subject: TextView = itemView.findViewById(R.id.tvTaskSubject)
        val deadline: TextView = itemView.findViewById(R.id.tvDeadline)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
    }
}

package smart.planner.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import smart.planner.R
import smart.planner.data.model.Task
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val onDeleteClick: (Task) -> Unit // Thêm callback xóa
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var tasks: List<Task> = listOf()

    fun submitList(newList: List<Task>) {
        tasks = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position], onDeleteClick)
    }

    override fun getItemCount(): Int = tasks.size

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
        private val tvSubject: TextView = itemView.findViewById(R.id.tvTaskSubject)
        private val tvDeadline: TextView = itemView.findViewById(R.id.tvDeadline)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteTask) // Ánh xạ nút xóa

        fun bind(task: Task, onDeleteClick: (Task) -> Unit) {
            tvTitle.text = task.title
            tvSubject.text = task.subjectName

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            tvDeadline.text = "Deadline: ${sdf.format(Date(task.deadline))}"

            tvStatus.text = if (task.isDone) "Hoàn thành" else "Chưa xong"
            checkBox.isChecked = task.isDone

            // Xử lý sự kiện bấm nút xóa
            btnDelete.setOnClickListener {
                onDeleteClick(task)
            }
        }
    }
}
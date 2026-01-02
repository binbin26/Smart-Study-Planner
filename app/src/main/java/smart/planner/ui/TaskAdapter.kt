package smart.planner.ui

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import androidx.recyclerview.widget.RecyclerView
import smart.planner.R
import smart.planner.data.model.Task
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private var tasks: List<Task>,
    private val onDeleteClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvTaskName)
        val tvSubject: TextView = view.findViewById(R.id.tvSubject)
        val tvDeadline: TextView = view.findViewById(R.id.tvDeadline)
        val tvDescription: TextView = view.findViewById(R.id.tvFullDescription)
        val layoutDetail: LinearLayout = view.findViewById(R.id.layoutDetail)
        val btnShow: Button = view.findViewById(R.id.btnShowDetail)
        val btnHide: Button = view.findViewById(R.id.btnHideDetail)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
        val timePicker: TimePicker = view.findViewById(R.id.timePickerNotify)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task_check, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        holder.tvName.text = "Tên bài tập: ${task.name}"
        holder.tvSubject.text = "Môn học: ${task.subject}"
        holder.tvDeadline.text = "Deadline: ${sdf.format(Date(task.deadline))}"
        holder.tvDescription.text = "Nội dung: ${task.description}"

        // Mặc định ẩn chi tiết
        holder.layoutDetail.visibility = View.GONE
        holder.btnShow.visibility = View.VISIBLE

        // Xử lý Phóng to
        holder.btnShow.setOnClickListener {
            holder.layoutDetail.visibility = View.VISIBLE
            holder.btnShow.visibility = View.GONE
        }

        // Xử lý Thu nhỏ
        holder.btnHide.setOnClickListener {
            holder.layoutDetail.visibility = View.GONE
            holder.btnShow.visibility = View.VISIBLE
        }

        // Xử lý Xóa với AlertDialog xác nhận
        holder.btnDelete.setOnClickListener {
            val context = holder.itemView.context
            AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Xóa Task này?")
                .setPositiveButton("Xác nhận") { _, _ ->
                    onDeleteClick(task)
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
    }

    override fun getItemCount() = tasks.size

    fun updateData(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}
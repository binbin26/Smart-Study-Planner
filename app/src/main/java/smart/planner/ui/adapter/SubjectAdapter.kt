package smart.planner.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import smart.planner.R
import smart.planner.data.model.Subject

class SubjectAdapter(
    private var subjects: List<Subject>,
    private val onEditClick: (Subject) -> Unit,
    private val onDeleteClick: (Subject) -> Unit
) : RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    inner class SubjectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val colorIndicator: View = view.findViewById(R.id.colorIndicator)
        val tvSubjectName: TextView = view.findViewById(R.id.tvSubjectName)
        val tvSubjectCode: TextView = view.findViewById(R.id.tvSubjectCode)
        val tvSubjectDescription: TextView = view.findViewById(R.id.tvSubjectDescription)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEditSubject)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteSubject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subject, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position]

        // Set subject info
        holder.tvSubjectName.text = subject.name
        holder.tvSubjectCode.text = subject.code ?: "N/A"

        // Set teacher or hide if empty
        if (subject.teacher.isNullOrBlank()) {
            holder.tvSubjectDescription.visibility = View.GONE
        } else {
            holder.tvSubjectDescription.visibility = View.VISIBLE
            holder.tvSubjectDescription.text = subject.teacher
        }

        // Set color indicator
        try {
            val color = Color.parseColor(subject.color ?: "#3F51B5")
            holder.colorIndicator.setBackgroundColor(color)
        } catch (e: Exception) {
            holder.colorIndicator.setBackgroundColor(Color.parseColor("#3F51B5"))
        }

        // Click listeners
        holder.btnEdit.setOnClickListener { onEditClick(subject) }
        holder.btnDelete.setOnClickListener { onDeleteClick(subject) }
    }

    override fun getItemCount(): Int = subjects.size

    fun updateSubjects(newSubjects: List<Subject>) {
        subjects = newSubjects
        notifyDataSetChanged()
    }
}

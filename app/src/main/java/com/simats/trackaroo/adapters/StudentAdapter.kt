package com.simats.trackaroo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simats.trackaroo.R
import com.simats.trackaroo.models.StudentData

class StudentAdapter(
    private var studentList: List<StudentData>
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStudentID: TextView = itemView.findViewById(R.id.tvStudentID)
        val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        val tvRouteNo: TextView = itemView.findViewById(R.id.tvRouteNo)
        val tvGrade: TextView = itemView.findViewById(R.id.tvGrade)
        val btnDeleteStudent: ImageView = itemView.findViewById(R.id.btnDeleteStudent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = studentList[position]
        holder.tvStudentID.text = student.student_id
        holder.tvStudentName.text = student.student_name
        holder.tvRouteNo.text = student.route_number ?: "Not Assigned" // ✅ handle null
        holder.tvGrade.text = student.grade

        // ❌ Delete button placeholder
        holder.btnDeleteStudent.setOnClickListener {
            // TODO: Delete API integration
        }
    }

    override fun getItemCount(): Int = studentList.size

    fun updateList(newList: List<StudentData>) {
        studentList = newList
        notifyDataSetChanged()
    }
}

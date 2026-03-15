package com.simats.trackaroo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.trackaroo.adapters.StudentAdapter
import com.simats.trackaroo.models.StudentData
import com.simats.trackaroo.models.StudentDatabaseResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentDatabaseActivity : AppCompatActivity() {

    private lateinit var studentAdapter: StudentAdapter
    private var studentList: MutableList<StudentData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_database)

        val backButton = findViewById<ImageView>(R.id.backButton)
        val searchBar = findViewById<EditText>(R.id.searchStudent)
        val recyclerView = findViewById<RecyclerView>(R.id.driversRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        studentAdapter = StudentAdapter(studentList)
        recyclerView.adapter = studentAdapter

        backButton.setOnClickListener { finish() }

        // 🔍 Search
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterStudents(s.toString())
            }
        })

        fetchStudents()
    }

    private fun fetchStudents() {
        val call = RetrofitClient.instance.getStudents()
        call.enqueue(object : Callback<StudentDatabaseResponse> {
            override fun onResponse(
                call: Call<StudentDatabaseResponse>,
                response: Response<StudentDatabaseResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    studentList.clear()
                    response.body()?.data?.let { studentList.addAll(it) }
                    studentAdapter.updateList(studentList)
                } else {
                    Toast.makeText(this@StudentDatabaseActivity, "No students found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StudentDatabaseResponse>, t: Throwable) {
                Toast.makeText(this@StudentDatabaseActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterStudents(query: String) {
        val filtered = studentList.filter { student ->
            val routeUnassigned = student.route_number.isNullOrEmpty() && query.equals("not assigned", ignoreCase = true)
            student.student_name.contains(query, ignoreCase = true) ||
                    student.student_id.contains(query, ignoreCase = true) ||
                    routeUnassigned
        }
        studentAdapter.updateList(filtered)
    }
}

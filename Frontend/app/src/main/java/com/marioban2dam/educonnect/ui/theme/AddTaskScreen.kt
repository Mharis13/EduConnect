package com.marioban2dam.educonnect.ui.theme

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marioban2dam.educonnect.R
import com.marioban2dam.educonnect.retrofit.CourseApi
import com.marioban2dam.educonnect.retrofit.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Data class representing a task to be added to a course.
 *
 * @param title The title of the task.
 * @param description The description of the task (optional).
 * @param dueDate The due date of the task in ISO 8601 format.
 * @param courseId The ID of the course to which the task belongs.
 */
data class TaskDto(
    val title: String,
    val description: String?,
    val dueDate: String, // ISO 8601 format
    val courseId: Int
)

/**
 * Activity for adding a task to a course.
 * Handles the creation of the task and interaction with the API.
 */
class AddTaskScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve course ID and token from the intent
        val courseId = intent.getIntExtra("courseId", -1)
        val token = intent.getStringExtra("token") ?: ""

        // Finish the activity if course ID or token is invalid
        if (courseId == -1 || token.isEmpty()) {
            finish()
            return
        }

        // Set the content of the activity to the AddTaskForm composable
        setContent {
            AddTaskForm(
                onSave = { title, description, dueDate ->
                    addTaskToCourse(courseId, token, title, description, dueDate)
                },
                onCancel = { finish() }
            )
        }
    }

    /**
     * Adds a task to the specified course using the API.
     *
     * @param courseId The ID of the course.
     * @param token The authentication token for API requests.
     * @param title The title of the task.
     * @param description The description of the task (optional).
     * @param dueDate The due date of the task in ISO 8601 format.
     */
    private fun addTaskToCourse(
        courseId: Int,
        token: String,
        title: String,
        description: String?,
        dueDate: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Create the API client and task object
                val courseApi = RetrofitClient.getInstance(token).create(CourseApi::class.java)
                val taskDto = Task(
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    courseId = courseId
                )

                // Send the request to add the task
                val response = courseApi.addTaskToCourse(courseId, taskDto).execute()
                if (response.isSuccessful) {
                    // Show success message and finish the activity
                    runOnUiThread {
                        Toast.makeText(
                            this@AddTaskScreen,
                            "Task added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                } else {
                    // Show error message
                    runOnUiThread {
                        Toast.makeText(
                            this@AddTaskScreen,
                            "Error adding task",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                // Handle exceptions and show error message
                Log.d("AddTaskScreen", "Error adding task", e)
                runOnUiThread {
                    Toast.makeText(
                        this@AddTaskScreen,
                        "Task added successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }
}

/**
 * Composable function for displaying the form to add a task.
 *
 * @param onSave Callback function to handle saving the task.
 * @param onCancel Callback function to handle canceling the task creation.
 */
@Composable
fun AddTaskForm(
    onSave: (String, String?, String) -> Unit,
    onCancel: () -> Unit
) {
    // State variables for form inputs
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var dueTime by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Layout for the form
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF001C55))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title text
        Text(
            text = "Add Task",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.fira_code_medium)),
            color = Color(0xFFF1E5FC)
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Input field for task title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF3391FF),
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Input field for task description
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF3391FF),
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Button to select due date and time
        Button(
            onClick = {
                val calendar = Calendar.getInstance()
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        dueDate = "$year-${month + 1}-$dayOfMonth"
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                dueTime = String.format("%02d:%02d", hourOfDay, minute)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3391FF)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (dueDate.isEmpty() || dueTime.isEmpty()) "Select Date and Time" else "Date: $dueDate Time: $dueTime",
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Buttons for saving or canceling the task
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { onCancel() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    val combinedDateTime = "$dueDate $dueTime"
                    val isoDateTime =
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }.format(
                            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(
                                combinedDateTime
                            )!!
                        )
                    onSave(title, description, isoDateTime)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3391FF)),
                shape = RoundedCornerShape(12.dp),
                enabled = title.isNotEmpty() && dueDate.isNotEmpty() && dueTime.isNotEmpty()
            ) {
                Text("Save")
            }
        }
    }
}
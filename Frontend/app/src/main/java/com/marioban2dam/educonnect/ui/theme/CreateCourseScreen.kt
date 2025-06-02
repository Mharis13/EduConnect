package com.marioban2dam.educonnect.ui.theme

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marioban2dam.educonnect.R
import com.marioban2dam.educonnect.retrofit.CourseInterface
import com.marioban2dam.educonnect.retrofit.CourseApi
import com.marioban2dam.educonnect.retrofit.RetrofitClient
import com.marioban2dam.educonnect.ui.TeacherHomeActivity
import kotlinx.coroutines.launch

/**
 * ViewModel to manage course creation logic.
 * Handles API calls and updates the UI state.
 */
class CreateCourseViewModel : ViewModel() {
    // State variable to store the list of courses
    var cours by mutableStateOf<List<CourseInterface>>(emptyList())
        private set

    /**
     * Creates a new course using the provided API and updates the state.
     *
     * @param context The application context for accessing shared preferences.
     * @param courseInterface The course data to be created.
     * @param onSuccess Callback function triggered on successful course creation.
     * @param onError Callback function triggered on error during course creation.
     */
    fun createCourse(
        context: Context,
        courseInterface: CourseInterface,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val sharedPreferences =
                    context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("auth_token", null)

                if (token != null) {
                    val newCourse = RetrofitClient.getInstance(token).create(CourseApi::class.java)
                        .createCourse(courseInterface)
                    cours = cours + newCourse
                    onSuccess()
                } else {
                    onError("Token not found. Please log in again.")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }
}

/**
 * Composable function to display the course creation screen.
 * Allows users to input course details and create a new course.
 *
 * @param viewModel The ViewModel instance to manage course creation logic.
 */
@Composable
fun CreateCourseScreen(viewModel: CreateCourseViewModel = viewModel()) {
    // State variables for course name, description, and error message
    var courseName by remember { mutableStateOf("") }
    var courseDescription by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NavyBlue, DarkBlue),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Title text
            Text(
                text = "Create a New Course",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF1E5FC),
                fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Input field for course name
            TextField(
                value = courseName,
                onValueChange = { courseName = it },
                label = { Text("Course Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Input field for course description
            TextField(
                value = courseDescription,
                onValueChange = { courseDescription = it },
                label = { Text("Course Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            )

            // Error message display
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Button to create the course
            Button(
                onClick = {
                    if (courseName.isNotBlank() && courseDescription.isNotBlank()) {
                        val defaultIcon = R.drawable.android // Default icon
                        viewModel.createCourse(
                            context = context,
                            courseInterface = CourseInterface(
                                name = courseName,
                                description = courseDescription,
                                iconResId = defaultIcon
                            ),
                            onSuccess = {
                                courseName = ""
                                courseDescription = ""
                                errorMessage = null
                                println("Course created successfully")
                                val intent = Intent(context, TeacherHomeActivity::class.java)
                                context.startActivity(intent)
                            },
                            onError = { error ->
                                errorMessage = "Error: $error"
                            }
                        )
                    } else {
                        errorMessage = "Please fill in all fields"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E6BA8)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Create Course",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Preview function for the CreateCourseScreen composable.
 * Displays a placeholder preview without ViewModel functionality.
 */
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun CreateCourseScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Text("Preview not available with ViewModel")
    }
}
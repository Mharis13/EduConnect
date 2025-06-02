package com.marioban2dam.educonnect.ui.theme

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marioban2dam.educonnect.retrofit.CourseApi
import com.marioban2dam.educonnect.retrofit.CourseInterface
import com.marioban2dam.educonnect.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Composable function to display the grades of courses for a specific user.
 *
 * @param token The authentication token used for API requests.
 * @param userId The ID of the user whose courses and grades are being fetched.
 * @param onBackClick Callback function triggered when the back button is clicked.
 */
@Composable
fun CourseGradesScreen(token: String, userId: String, onBackClick: () -> Unit) {
    // Create an instance of the Course API using the provided token
    val courseApi = RetrofitClient.getInstance(token).create(CourseApi::class.java)

    // State variables to store the list of courses and their average grades
    var courses by remember { mutableStateOf<List<CourseInterface>>(emptyList()) }
    var courseGrades by remember { mutableStateOf<Map<String, Float>>(emptyMap()) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch courses and grades when the composable is launched
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                // Fetch courses associated with the user
                val fetchedCourses = withContext(Dispatchers.IO) {
                    courseApi.getCoursesByUserId(userId)
                }
                courses = fetchedCourses

                // Fetch grades for all tasks
                val grades = withContext(Dispatchers.IO) {
                    courseApi.getGrades()
                }

                // Calculate average grades for each course
                val averages = fetchedCourses.associate { course ->
                    val courseGrades =
                        grades.filter { it.taskId in course.id?.let { id -> id..id } ?: emptyList() }
                    val average = if (courseGrades.isNotEmpty()) {
                        courseGrades.map { it.score }.average().toFloat()
                    } else {
                        0f
                    }
                    course.name to average
                }
                courseGrades = averages
            } catch (e: Exception) {
                Log.e("CourseGradesScreen", "Error: ${e.message}")
            }
        }
    }

    // Scaffold to display the UI
    Scaffold(
        containerColor = Color(0xFF0A1A35) // Background color of the screen
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding) // Padding inside the Scaffold
                .padding(horizontal = 16.dp, vertical = 8.dp) // Additional padding
                .fillMaxSize() // Fill the available space
        ) {
            // Back button
            Button(
                onClick = onBackClick, // Action when the button is clicked
                modifier = Modifier.padding(bottom = 16.dp) // Bottom spacing
            ) {
                Text(text = "Go Back") // Button text
            }

            // Screen title
            Text(
                text = "Average Grades in Courses", // Title text
                color = Color.White, // Text color
                fontSize = 24.sp, // Font size
                fontWeight = FontWeight.Bold, // Font weight
                modifier = Modifier.padding(bottom = 16.dp) // Bottom spacing
            )

            // List of courses and their average grades
            LazyColumn {
                items(courses) { course ->
                    // Card for each course
                    Card(
                        modifier = Modifier
                            .fillMaxWidth() // Fill the available width
                            .padding(vertical = 8.dp), // Vertical spacing
                        shape = RoundedCornerShape(16.dp), // Rounded shape for the card
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2B4A)), // Background color of the card
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp) // Card elevation
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Course name
                            Text(
                                text = course.name, // Course name
                                color = Color.White, // Text color
                                fontSize = 20.sp, // Font size
                                fontWeight = FontWeight.Bold // Font weight
                            )
                            Spacer(modifier = Modifier.height(4.dp)) // Spacing between elements
                            // Average grade for the course
                            Text(
                                text = "Average Note: ${courseGrades[course.name] ?: "No grades"}", // Text with the average grade
                                color = Color(0xFFB0BEC5), // Text color
                                fontSize = 16.sp // Font size
                            )
                        }
                    }
                }
            }
        }
    }
}
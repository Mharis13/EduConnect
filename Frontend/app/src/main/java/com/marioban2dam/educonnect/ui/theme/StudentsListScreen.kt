package com.marioban2dam.educonnect.ui.theme

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marioban2dam.educonnect.R
import com.marioban2dam.educonnect.retrofit.RetrofitClient
import com.marioban2dam.educonnect.retrofit.CourseApi
import com.marioban2dam.educonnect.retrofit.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel to manage the list of students in a course.
 */
class StudentsListViewModel(private val token: String) : ViewModel() {
    private val courseService = RetrofitClient.getInstance(token).create(CourseApi::class.java)

    private val _students = MutableStateFlow<List<User>>(emptyList())
    val students: StateFlow<List<User>> = _students

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /**
     * Loads the list of students in a course.
     *
     * @param courseId ID of the course.
     */
    fun loadStudents(courseId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = courseService.getEnrolledStudents(courseId)
                _students.value = response
            } catch (e: Exception) {
                _errorMessage.value = "Error loading students: ${e.message}"
                Log.e("StudentsListViewModel", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

/**
 * Screen to display the list of students in a course.
 *
 * @param courseId ID of the course.
 * @param token Authentication token.
 */
@Composable
fun StudentsListScreen(courseId: Int, token: String) {
    val viewModel = remember { StudentsListViewModel(token) }
    val students by viewModel.students.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadStudents(courseId)
    }

    Scaffold(
        containerColor = Color(0xFF0A1A35)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Student List",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.fira_code_medium))
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(students) { student ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2A49))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = student.name,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = student.email,
                                        color = Color(0xFFB0BEC5),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
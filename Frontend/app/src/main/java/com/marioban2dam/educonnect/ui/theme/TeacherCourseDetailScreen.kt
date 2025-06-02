package com.marioban2dam.educonnect.ui.theme

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marioban2dam.educonnect.R
import com.marioban2dam.educonnect.decodeRoleFromToken
import com.marioban2dam.educonnect.retrofit.CourseApi
import com.marioban2dam.educonnect.retrofit.CourseInterface
import com.marioban2dam.educonnect.retrofit.RetrofitClient
import com.marioban2dam.educonnect.retrofit.User
import com.marioban2dam.educonnect.ui.AddStudentInCourseActivity
import com.marioban2dam.educonnect.ui.CourseGradesActivity
import com.marioban2dam.educonnect.ui.ProfileActivity
import com.marioban2dam.educonnect.ui.SelectPersonActivity
import com.marioban2dam.educonnect.ui.StudentHomeActivity
import com.marioban2dam.educonnect.ui.StudentsListActivity
import com.marioban2dam.educonnect.ui.TaskDetailActivity
import com.marioban2dam.educonnect.ui.TeacherHomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.toString

/**
 * ViewModel for managing course details.
 * Handles loading students, tasks, and removing students from a course.
 *
 * @param token The authentication token for API requests.
 */
class CourseDetailViewModel(private val token: String) : ViewModel() {
    private val courseService = RetrofitClient.getInstance(token).create(CourseApi::class.java)

    // StateFlow for storing the list of students enrolled in the course.
    private val _students = MutableStateFlow<List<User>>(emptyList())
    val students: StateFlow<List<User>> = _students

    // StateFlow for storing the list of tasks associated with the course.
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    // StateFlow for tracking the loading state.
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // StateFlow for storing error messages.
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /**
     * Loads the list of students enrolled in the course.
     *
     * @param courseId The ID of the course.
     */
    fun loadStudents(courseId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = courseService.getEnrolledStudents(courseId.toInt())
                _students.value = response
            } catch (e: Exception) {
                if (e.message?.contains("500") == true) {
                    _errorMessage.value = "Network error: ${e.message}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Loads the list of tasks associated with the course.
     *
     * @param courseId The ID of the course.
     */
    fun loadTasks(courseId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response =
                    courseService.getAllTasks().filter { task -> task.courseId == courseId }
                _tasks.value = response
            } catch (e: Exception) {
                _errorMessage.value = "Error loading tasks: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Removes a student from the course.
     *
     * @param courseId The ID of the course.
     * @param studentId The ID of the student to be removed.
     */
    fun removeStudent(courseId: Int, studentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                courseService.removeStudentFromCourse(courseId, listOf(studentId))
                _students.value = _students.value.filter { it.id != studentId }
            } catch (e: Exception) {
                _students.value = _students.value.filter { it.id != studentId }
                Log.e("CourseDetailViewModel", "Error removing student", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

/**
 * Composable function to display the course detail screen.
 * Shows the list of students and tasks associated with the course.
 *
 * @param course The course details.
 * @param isTeacher Boolean indicating if the user is a teacher.
 * @param modifier Modifier for styling the composable.
 * @param context The context for navigation and API requests.
 * @param token The authentication token for API requests.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CourseDetailScreen(
    course: CourseInterface,
    isTeacher: Boolean,
    modifier: Modifier = Modifier,
    context: Context,
    token: String
) {
    val viewModel = remember { CourseDetailViewModel(token) }
    val students by viewModel.students.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val userId = remember {
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            .getString("userId", null)
    }

    // Load students and tasks when the screen is launched.
    LaunchedEffect(Unit) {
        Log.d("CourseId", "Course ID: ${course.id}")
        viewModel.loadStudents(course.id.toString())
        viewModel.loadTasks(course.id!!)
    }

    Scaffold(
        bottomBar = {
            Bottom2NavigationBar(
                selectedIndex = 0,
                isTeacher = isTeacher,
                onItemClick = { index ->
                    when (index) {
                        0 -> {
                            val isTeacher = decodeRoleFromToken(token) == "Teacher"
                            val intent = if (isTeacher) {
                                Intent(context, TeacherHomeActivity::class.java)
                            } else {
                                Intent(context, StudentHomeActivity::class.java)
                            }
                            intent.putExtra("token", token)
                            context.startActivity(intent)
                        }
                        1 -> {

                            val intent = Intent(context, SelectPersonActivity::class.java)
                            intent.putExtra("token", token)
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val retrofit = token
                                        ?.let { RetrofitClient.getInstance(it) }
                                        ?: RetrofitClient.instance
                                    val api = retrofit.create(CourseApi::class.java)
                                    val users = api.getUsers().map { user -> user.id to user.name }
                                    withContext(Dispatchers.Main) {
                                        val intent = Intent(context, SelectPersonActivity::class.java)
                                        intent.putExtra("token", token)

                                        intent.putExtra("isTeacher", decodeRoleFromToken(token.toString()) == "Teacher")


                                        val people = users.map { PersonParcelable(it.first, it.second) } // Mapear userId y name
                                        intent.putParcelableArrayListExtra("people", ArrayList(people)) // Pasar la lista de objetos PersonParcelable
                                        context.startActivity(intent)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }

                        2 -> {


                            val intent = Intent(context, CourseGradesActivity::class.java)
                            intent.putExtra("token", token)
                            context.startActivity(intent)

                        }

                        3 -> {
                            Log.d("Navigation", "Settings clicked")
                            val intent = Intent(context, ProfileActivity::class.java)
                            intent.putExtra("token", token)
                            intent.putExtra("userId", userId)
                            context.startActivity(intent)
                        }
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
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
                .padding(innerPadding)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Display course name.
                Text(
                    text = course.name,
                    fontSize = 26.sp,
                    color = Color(0xFFF1E5FC),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.fira_code_medium))
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Card for displaying students.
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2A49))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Students",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.fira_code_medium))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White)
                        } else {
                            students.forEach { student ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = student.name,
                                        color = Color.White,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "Remove",
                                        color = Color.Red,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable {
                                            viewModel.removeStudent(course.id!!, student.id)
                                        }
                                    )
                                }
                            }
                        }
                        errorMessage?.let {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = it,
                                color = Color.Red,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (isTeacher) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    val intent =
                                        Intent(context, AddStudentInCourseActivity::class.java)
                                    intent.putExtra("courseId", course.id)
                                    intent.putExtra("token", token)
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF3391FF)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Add Student")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Card for displaying tasks.
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2A49))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Tasks",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.fira_code_medium))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        tasks.forEach { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val intent =
                                            Intent(context, TaskDetailActivity::class.java).apply {
                                                putExtra("taskList", ArrayList(tasks))
                                                putExtra("courseId", course.id)
                                                putExtra("token", token)
                                            }
                                        context.startActivity(intent)
                                    }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = task.title,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        if (isTeacher) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    val intent = Intent(context, AddTaskScreen::class.java)
                                    intent.putExtra("courseId", course.id)
                                    intent.putExtra("token", token)
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF3391FF)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Add Task")
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Composable function to display the bottom navigation bar.
 *
 * @param selectedIndex The index of the currently selected navigation item.
 * @param onItemClick Callback triggered when a navigation item is clicked.
 */
@Composable
fun Bottom2NavigationBar(selectedIndex: Int, onItemClick: (Int) -> Unit, isTeacher: Boolean) {
    val items = listOf(
        Option("Dashboard", R.drawable.vector__2_),
        Option("Messages", R.drawable.vector__1_),
        if (isTeacher) {
            Option("", R.drawable.file_bar_graph_fill)

        } else {
            Option("Grades", R.drawable.file_bar_graph_fill)
        },
        Option("Settings", R.drawable.vector)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFF001C55), shape = RoundedCornerShape(20.dp))
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, (label, icon) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onItemClick(index) }
                ) {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = label,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF1E5FC),
                        fontFamily = FontFamily(Font(R.font.fira_code_medium))
                    )
                }
            }
        }
    }
}


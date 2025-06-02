package com.marioban2dam.educonnect.ui.theme

import android.content.Intent
import android.os.Build
import android.os.Parcelable
import android.util.Log
import androidx.annotation.RequiresApi
import com.marioban2dam.educonnect.decodeRoleFromToken
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.os.Parcel
import com.marioban2dam.educonnect.R
import com.marioban2dam.educonnect.retrofit.*
import com.marioban2dam.educonnect.ui.CourseGradesActivity
import com.marioban2dam.educonnect.ui.ProfileActivity
import com.marioban2dam.educonnect.ui.SelectPersonActivity
import com.marioban2dam.educonnect.ui.StudentHomeActivity
import com.marioban2dam.educonnect.ui.TeacherHomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Composable function to display the "Add Students" screen.
 *
 * @param courseId The ID of the course to which students will be added.
 * @param token The authentication token for API requests.
 * @param onBack Callback function to handle navigation back to the previous screen.
 */

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudentsScreen(
    courseId: Int,
    token: String,
    onBack: () -> Unit
) {
    // Local context for accessing Android-specific functionality
    var context = LocalContext.current

    // API service for course-related operations
    val courseService = RetrofitClient.getInstance(token).create(CourseApi::class.java)

    // State variables for managing UI and data
    var availableStudents by remember { mutableStateOf<List<User>>(emptyList()) }
    var selectedStudents by remember { mutableStateOf(mutableSetOf<User>()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch available students when the composable is launched
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val response = courseService.getStudentsByCourse(courseId)
            availableStudents = response
        } catch (e: Exception) {
            if (availableStudents.isEmpty()) {
                onBack()
            }
            errorMessage = "Network error: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    // Scaffold layout for the screen
    Scaffold(
        topBar = {
            // Top app bar with title and navigation icon
            TopAppBar(
                title = { Text("Add Students", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF001C55))
            )
        },
        bottomBar = {
            // Bottom navigation bar
            BottomNavigationBar(selectedIndex = 2) { index ->
                when (index) {
                    0 -> {
                        // Navigate to home screen based on user role
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
                        // Navegar a la pantalla de mensajes
                        val intent = Intent(context, SelectPersonActivity::class.java)
                        intent.putExtra("token", token)
                       CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val users = courseService.getUsers().map { user -> user.id to user.name } // Mapear userId y name
                                withContext(Dispatchers.Main) {
                                    val intent = Intent(context, SelectPersonActivity::class.java)
                                    intent.putExtra("token", token)

                                    intent.putExtra("isTeacher", decodeRoleFromToken(token) == "Teacher")


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
                        // Navigate to course grades screen
                        val intent = Intent(context, CourseGradesActivity::class.java)
                        intent.putExtra("token", token)
                        context.startActivity(intent)
                    }

                    3 -> {
                        // Navigate to profile screen
                        val intent = Intent(context, ProfileActivity::class.java)
                        intent.putExtra("token", token)
                        context.startActivity(intent)
                    }
                }
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        // Main content of the screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF001C55),
                            Color(0xFF003366)
                        )
                    )
                )
                .padding(padding)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Title text
                Text(
                    text = "Select Students",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.fira_code_medium))
                )
                Spacer(Modifier.height(12.dp))
                if (isLoading) {
                    // Show loading indicator
                    CircularProgressIndicator(color = Color.White)
                } else {
                    // Display list of available students
                    availableStudents.forEach { student ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    // Toggle student selection
                                    selectedStudents = selectedStudents.toMutableSet().apply {
                                        if (contains(student)) remove(student) else add(student)
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedStudents.contains(student)) Color(
                                    0xFF3391FF
                                ) else Color(0xFF1B2A49)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                // Display student name
                                Text(
                                    text = student.name,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 16.sp
                                )
                                if (selectedStudents.contains(student)) {
                                    // Show icon for selected students
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_add_24),
                                        contentDescription = "Selected",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                // Button to add selected students to the course
                Button(
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        val studentIds = selectedStudents.map { it.id }
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                Log.d("AddStudentsScreen", "Adding students: $studentIds")
                                courseService.addStudentToCourse(
                                    courseId,
                                    studentIds.map { it -> it.toString() }
                                )
                                withContext(Dispatchers.Main) {
                                    onBack()
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    onBack()
                                }
                            } finally {
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    enabled = selectedStudents.isNotEmpty() && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedStudents.isNotEmpty()) Color(0xFF3391FF) else Color(
                            0xFF555555
                        ),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isLoading) "Loading..." else "Add Students",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                errorMessage?.let {
                    Spacer(Modifier.height(8.dp))
                    // Display error message if any
                    Text(
                        text = it,
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Composable function to display a bottom navigation bar.
 *
 * @param selectedIndex The index of the currently selected navigation item.
 * @param onItemClick Callback function to handle navigation item clicks.
 */
@Composable
fun BottomNavigationBar(
    selectedIndex: Int,
    onItemClick: (Int) -> Unit
) {
    // List of navigation items
    val items = listOf(
        NavigationItem("Home", R.drawable.vector__2_),
        NavigationItem("Messages", R.drawable.vector__1_),
        NavigationItem("Grades", R.drawable.file_bar_graph_fill),
        NavigationItem("Profile", R.drawable.vector)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFF001C55), shape = RoundedCornerShape(20.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onItemClick(index) }
            ) {
                // Display navigation item icon
                Image(
                    painter = painterResource(id = item.icon),
                    contentDescription = item.label,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Display navigation item label
                Text(
                    text = item.label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (index == selectedIndex) Color.White else Color.Gray,
                    fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Data class representing a navigation item.
 *
 * @param label The label of the navigation item.
 * @param icon The resource ID of the navigation item's icon.
 */
data class NavigationItem(val label: String, val icon: Int)


data class PersonParcelable(
    val id: String,
    val name: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
    }
    fun getDetails(): String {
        return "$name,$id" // Devuelve ambos valores en un formato legible
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<PersonParcelable> {
        override fun createFromParcel(parcel: Parcel): PersonParcelable {
            return PersonParcelable(parcel)
        }

        override fun newArray(size: Int): Array<PersonParcelable?> {
            return arrayOfNulls(size)
        }
    }
}
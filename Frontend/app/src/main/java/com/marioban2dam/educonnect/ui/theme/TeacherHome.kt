package com.marioban2dam.educonnect.ui.theme

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.marioban2dam.educonnect.decodeRoleFromToken
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marioban2dam.educonnect.R
import com.marioban2dam.educonnect.retrofit.CourseInterface
import com.marioban2dam.educonnect.retrofit.CourseApi
import com.marioban2dam.educonnect.retrofit.RetrofitClient
import com.marioban2dam.educonnect.ui.CourseGradesActivity
import com.marioban2dam.educonnect.ui.CreateCourseActivity
import com.marioban2dam.educonnect.ui.ProfileActivity
import com.marioban2dam.educonnect.ui.SelectPersonActivity
import com.marioban2dam.educonnect.ui.StudentHomeActivity
import com.marioban2dam.educonnect.ui.TeacherCourseDetailActivity
import com.marioban2dam.educonnect.ui.TeacherHomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TeacherHomeViewModel : ViewModel() {
    var cours by mutableStateOf<List<CourseInterface>>(emptyList())
        private set

    fun fetchCourses(token: String?, userId: String?) {
        viewModelScope.launch {
            try {
                val retrofit = token
                    ?.let { RetrofitClient.getInstance(it) }
                    ?: RetrofitClient.instance
                val api = retrofit.create(CourseApi::class.java)
                Log.d("CoursesListAPI", "Fetching and userId: $userId")
                val fetchedCourses = api.getCoursesByTeacherId(userId.toString())
                Log.d("CoursesListAPI", fetchedCourses.toString())
                cours = fetchedCourses
            } catch (e: Exception) {
                Log.e("FetchCoursesError", "Error fetching courses: ${e.message}")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TeacherHomeScreen(
    viewModel: TeacherHomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current

    val token = remember {
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            .getString("auth_token", null)
    }
    val userId = remember {
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            .getString("userId", null)
    }

    LaunchedEffect(Unit) {
        viewModel.fetchCourses(token, userId)
    }

    val cours = viewModel.cours + CourseInterface(
        name = "Create Course",
        description = "Create a new course",
        iconResId = R.drawable.baseline_add_24
    )

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
    ) {
        Text(
            text = "EDUCONNECT",
            modifier = Modifier
                .offset(x = 12.dp, y = 15.dp)
                .padding(top = 52.dp),
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 30.sp,
                fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                fontWeight = FontWeight(700),
                color = Color(0xFFF1E5FC),
                textAlign = TextAlign.Center,
            )
        )

        Text(
            text = "Courses",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 167.dp),
            fontSize = 30.sp,
            fontFamily = FontFamily(Font(R.font.fira_code_medium)),
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF1E5FC),
            textAlign = TextAlign.Center
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 230.dp, bottom = 16.dp)
        ) {
            items(cours.size) { index ->
                val course = cours[index]
                val iconResId = when (course.name) {
                    "Programming" -> R.drawable.code_slash
                    "Databases" -> R.drawable.group
                    "Mobile Development" -> R.drawable.android
                    "Web Development" -> R.drawable.globe
                    "Services" -> R.drawable.vector__4_
                    "FOL" -> R.drawable.vector__3_
                    "Create Course" -> R.drawable.baseline_add_24
                    else -> R.drawable.android
                }

                TeacherReusableBoxForCourses(
                    name = course.name,
                    iconResId = iconResId,
                    onClick = {
                        if (course.name == "Create Course") {
                            val intent = Intent(context, CreateCourseActivity::class.java)
                            context.startActivity(intent)
                        } else {
                            val intent2 =
                                Intent(context, TeacherCourseDetailActivity::class.java).apply {
                                    putExtra("courseId", course.id)
                                    putExtra("courseName", course.name)
                                    putExtra("courseDescription", course.description)
                                    putExtra("courseIconResId", course.iconResId)
                                    putExtra("token", token)
                                }
                            context.startActivity(intent2)
                        }
                    }
                )
            }
        }

        TeacherBottomNavigationBar(
            selectedIndex = 0,
            onItemClick = { index ->
                when (index) {
                    0 -> {
                        val isTeacher = decodeRoleFromToken(token.toString()) == "Teacher"
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
    }
}

@Composable
fun TeacherReusableBoxForCourses(name: String, iconResId: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.45f)
            .aspectRatio(1.45f)
            .border(width = 1.dp, color = Color(0xFF0E6BA8), shape = RoundedCornerShape(10.dp))
            .background(
                if (name == "Create Course") Color(0x80000000) else Color(0xFF001C55),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = name,
                modifier = Modifier
                    .size(32.dp)
                    .padding(bottom = 8.dp)
            )
            Text(
                text = name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TeacherBottomNavigationBar(selectedIndex: Int, onItemClick: (Int) -> Unit) {
    val items = listOf(
        Option("Dashboard", R.drawable.vector__2_),
        Option("Messages", R.drawable.vector__1_),
        Option("Settings", R.drawable.vector)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .offset(y = 800.dp)
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
                        fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

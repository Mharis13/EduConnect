package com.marioban2dam.educonnect.ui.theme

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.marioban2dam.educonnect.R
import com.marioban2dam.educonnect.decodeRoleFromToken
import com.marioban2dam.educonnect.retrofit.CourseApi
import com.marioban2dam.educonnect.retrofit.RetrofitClient
import com.marioban2dam.educonnect.ui.CourseGradesActivity
import com.marioban2dam.educonnect.ui.ProfileActivity
import com.marioban2dam.educonnect.ui.SelectPersonActivity
import com.marioban2dam.educonnect.ui.StudentHomeActivity
import com.marioban2dam.educonnect.ui.TeacherHomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Base64

/**
 * Composable function to display the profile screen of a user.
 * Fetches and displays user information such as name, email, and profile image.
 *
 * @param userId The ID of the user whose profile is being displayed.
 * @param token The authentication token used for API requests.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    userId: String,
    token: String
) {
    val context = LocalContext.current
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userDniOrNia by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    val courseApi = RetrofitClient.getInstance(token).create(CourseApi::class.java)
    // Fetch user data when the composable is launched
    LaunchedEffect(userId) {
        try {
            // API call to fetch user data
            val user = withContext(Dispatchers.IO) { courseApi.getUserById(userId) }
            userName = user.name
            userEmail = user.email
            userDniOrNia = user.id
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Scaffold to structure the UI
    Scaffold(
        containerColor = Color(0xFF0A1A35),
        bottomBar = {
            // Bottom navigation bar with navigation logic
            BottomNavigationBar(selectedIndex = 3, onItemClick = { index ->
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
                                val users = courseApi.getUsers().map { user -> user.id to user.name }
                                withContext(Dispatchers.Main) {
                                    val intent = Intent(context, SelectPersonActivity::class.java)
                                    intent.putExtra("token", token)

                                    intent.putExtra("isTeacher", decodeRoleFromToken(token) == "Teacher")


                                    val people = users.map { PersonParcelable(it.first, it.second) }
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
                        intent.putExtra("userId", userId)
                        context.startActivity(intent)
                    }

                    3 -> {
                        val intent = Intent(context, ProfileActivity::class.java)
                        intent.putExtra("token", token)
                        intent.putExtra("userId", userId)
                        context.startActivity(intent)
                    }
                }
            })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A1A35))
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            // Box to display the profile image
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = "Profile image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.person_square),
                        contentDescription = "Default profile image",
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Display user name
            Text(
                text = userName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.fira_code_medium))
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Display user email
            Text(
                text = userEmail,
                fontSize = 16.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Display user DNI or NIA
            Text(
                text = userDniOrNia,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

/**
 * Decodes the role of the user from the provided authentication token.
 *
 * @param token The authentication token containing the user's role.
 * @return The role of the user (e.g., "Teacher" or "Student"), or null if decoding fails.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun decodeRoleFromToken(token: String): String? {
    return try {
        val parts = token.split(".")
        if (parts.size > 1) {
            val payload = String(Base64.getUrlDecoder().decode(parts[1]))
            val json = JSONObject(payload)
            json.getString("http://schemas.microsoft.com/ws/2008/06/identity/claims/role")
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}


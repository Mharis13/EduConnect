package com.marioban2dam.educonnect.ui.theme

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marioban2dam.educonnect.R
import com.marioban2dam.educonnect.retrofit.AuthService
import com.marioban2dam.educonnect.retrofit.LoginRequest
import com.marioban2dam.educonnect.retrofit.LoginResponse
import com.marioban2dam.educonnect.retrofit.RetrofitClient
import com.marioban2dam.educonnect.ui.RegisterActivity
import com.marioban2dam.educonnect.ui.StudentHomeActivity
import com.marioban2dam.educonnect.ui.TeacherHomeActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Composable function to display the login screen.
 * Allows users to log in by providing their credentials and selecting their role.
 */
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun LoginScreen() {
    // State variables for username, password, selected role, and error message
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf("Student") }
    var errorMessage by remember { mutableStateOf("") }

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
    ) {
        // Title text for the application
        Text(
            text = "EDUCONNECT",
            modifier = Modifier.offset(x = 55.dp, y = 40.dp),
            fontSize = 48.sp,
            fontFamily = FontFamily(Font(R.font.fira_code_medium)),
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF1E5FC),
            textAlign = TextAlign.Center,
        )

        // Subtitle text describing the application
        Text(
            text = "Student and teacher management tool",
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 170.dp),
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.fira_code_medium)),
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF1E5FC),
            textAlign = TextAlign.Center,
        )

        // Logo image
        Image(
            painter = painterResource(id = R.drawable.chatgpt_image_14_abr_2025__22_54_37),
            contentDescription = "Logo",
            modifier = Modifier
                .offset(x = 100.dp, y = 150.dp)
                .padding(top = 52.dp),
            alignment = Alignment.Center,
            colorFilter = ColorFilter.tint(Color.White)
        )

        // Instruction text for logging in
        Text(
            text = "Log into your account to get started",
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 450.dp),
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.fira_code_medium)),
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFA6E1FA),
            textAlign = TextAlign.Center,
        )

        // Row for selecting the user role (Student or Teacher)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = 500.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Button for selecting "Student" role
            Button(
                onClick = { selectedOption = "Student" },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .border(
                        width = 1.dp,
                        color = if (selectedOption == "Student") Color(0xFF0A2472) else Color(
                            0xFFA6E1FA
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedOption == "Student") Color(0xFF0A2472) else Color.Transparent,
                    contentColor = if (selectedOption == "Student") Color.White else Color(
                        0xFFA6E1FA
                    )
                )
            ) {
                Text(
                    text = "Student",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            // Button for selecting "Teacher" role
            Button(
                onClick = { selectedOption = "Teacher" },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .border(
                        width = 1.dp,
                        color = if (selectedOption == "Teacher") Color(0xFF0A2472) else Color(
                            0xFFA6E1FA
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedOption == "Teacher") Color(0xFF0A2472) else Color.Transparent,
                    contentColor = if (selectedOption == "Teacher") Color.White else Color(
                        0xFFA6E1FA
                    )
                )
            ) {
                Text(
                    text = "Teacher",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Column for input fields and login button
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .offset(y = 580.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Input field for username
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("DNI / NIA") },
                placeholder = { Text("Introduce tu DNI o NIA") },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.vector__5_),
                        contentDescription = "Icono DNI/NIA",
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier
                    .width(320.dp)
                    .height(56.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF90CAF9),
                    focusedBorderColor = Color(0xFF90CAF9),
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Input field for password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                placeholder = { Text("Put your password") },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.vector__6_),
                        contentDescription = "Icono contraseña",
                        modifier = Modifier.size(24.dp)
                    )
                },

                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .width(320.dp)
                    .height(56.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF90CAF9),
                    focusedBorderColor = Color(0xFF90CAF9),
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Error message display
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Button to log in
            Button(
                onClick = {
                    val loginRequest = LoginRequest(
                        id = username.trim(),
                        password = password.toString().trim(),
                        Role = selectedOption.trim()
                    )

                    RetrofitClient.instance.create(AuthService::class.java)
                        .login(loginRequest)
                        .enqueue(object : Callback<LoginResponse> {
                            override fun onResponse(
                                call: Call<LoginResponse>,
                                response: Response<LoginResponse>
                            ) {
                                if (response.isSuccessful) {
                                    errorMessage = ""
                                    val token = response.body()?.token

                                    // Save the token in SharedPreferences
                                    val sharedPreferences = context.getSharedPreferences(
                                        "AppPreferences",
                                        Context.MODE_PRIVATE
                                    )
                                    val editor = sharedPreferences.edit()
                                    editor.putString("auth_token", token)
                                    editor.putString("userId", username)
                                    editor.apply()

                                    // Navigate to the corresponding screen
                                    if (selectedOption == "Teacher") {
                                        val intent =
                                            Intent(context, TeacherHomeActivity::class.java)
                                        context.startActivity(intent)
                                    } else if (selectedOption == "Student") {
                                        val intent =
                                            Intent(context, StudentHomeActivity::class.java)
                                        context.startActivity(intent)
                                    }
                                } else {
                                    errorMessage = "Invalid credentials"
                                    Log.e(
                                        "LoginError",
                                        "Error: ${response.code()} - ${response.message()}"
                                    )
                                }
                            }

                            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                errorMessage = "Connection error: ${t.localizedMessage}"
                            }
                        })
                },
                modifier = Modifier
                    .padding(1.dp)
                    .width(121.dp)
                    .height(51.dp)
                    .offset(y = 70.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0A2472),
                    contentColor = Color.White
                )
            ) {
                Text("LOGIN")
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Text for navigating to the registration screen
            Text(
                text = "Not registered? Go to Register",
                modifier = Modifier.clickable {
                    val intent = Intent(context, RegisterActivity::class.java)
                    context.startActivity(intent)
                },
                color = Color(0xFFA6E1FA),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
package com.marioban2dam.educonnect.ui.theme

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marioban2dam.educonnect.MainActivity
import com.marioban2dam.educonnect.R
import com.marioban2dam.educonnect.retrofit.AuthService
import com.marioban2dam.educonnect.retrofit.RegisterRequest
import com.marioban2dam.educonnect.retrofit.RegisterResponse
import com.marioban2dam.educonnect.retrofit.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Composable function to display the registration screen.
 * Allows users to create an account by providing their details.
 */
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun RegisterScreen() {
    // State variables for user input and messages
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Student") }
    var message by remember { mutableStateOf("") }

    // Retrofit service instance for authentication
    val authService = RetrofitClient.instance.create(AuthService::class.java)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NavyBlue, DarkBlue),
                    startY = 0f, endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        // Title text for the application
        Text(
            "EDUCONNECT",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.fira_code_medium)),
            color = Color(0xFFF1E5FC),
            modifier = Modifier.offset(x = 55.dp, y = 40.dp),
            textAlign = TextAlign.Center
        )

        // Box displaying the account creation section
        Box(
            modifier = Modifier
                .width(336.dp)
                .height(48.dp)
                .offset(x = 40.dp, y = 166.dp)
                .border(1.dp, Color(0xFFA6E1FA), RoundedCornerShape(12.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF2F4286), Color(0xFF2448AF))
                    )
                )
        ) {
            Text(
                "ACCOUNT CREATION",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                color = Color(0xFFA6E1FA),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Row for selecting the user role (Teacher or Student)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 220.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            // Button for selecting "Teacher" role
            Button(
                onClick = {
                    role = "Teacher"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (role == "Teacher") Color(0xFF0A2472) else Color.Transparent,
                    contentColor = if (role == "Teacher") Color.White else Color(0xFFA6E1FA)
                )
            ) { Text("DNI") }
            Spacer(Modifier.width(8.dp))
            // Button for selecting "Student" role
            Button(
                onClick = {
                    role = "Student"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (role == "Student") Color(0xFF0A2472) else Color.Transparent,
                    contentColor = if (role == "Student") Color.White else Color(0xFFA6E1FA)
                )
            ) { Text("NIA") }
        }

        // Column for input fields and registration button
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
                .offset(y = 320.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Input field for full name
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full name", color = Color(0xFFA6E1FA)) },
                placeholder = { Text("Full name", color = Color(0xFF90CAF9)) },
                leadingIcon = {
                    Image(painterResource(R.drawable.vector__5_), null, Modifier.size(24.dp))
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
            // Input field for email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color(0xFFA6E1FA)) },
                placeholder = { Text("Email", color = Color(0xFF90CAF9)) },
                leadingIcon = {
                    Image(painterResource(R.drawable.vector__5_), null, Modifier.size(24.dp))
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
            // Input field for ID (DNI or NIA)
            OutlinedTextField(
                value = id,
                onValueChange = { id = it },
                placeholder = {
                    Text(if (role == "Teacher") "DNI" else "NIA", color = Color(0xFF90CAF9))
                },
                leadingIcon = {
                    Image(painterResource(R.drawable.vector__6_), null, Modifier.size(24.dp))
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
                label = { Text("Password", color = Color(0xFFA6E1FA)) },
                placeholder = { Text("Password", color = Color(0xFF90CAF9)) },
                leadingIcon = {
                    Image(painterResource(R.drawable.vector__6_), null, Modifier.size(24.dp))
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

            Spacer(Modifier.height(8.dp))

            // Button to submit the registration form
            Button(
                onClick = {
                    scope.launch {
                        val req = RegisterRequest(
                            id = id.trim(),
                            passwordHash = password.trim(),
                            name = fullName.trim(),
                            email = email.trim(),
                            role = role.trim()
                        )
                        authService.register(req).enqueue(object : Callback<RegisterResponse> {
                            override fun onResponse(
                                call: Call<RegisterResponse>,
                                response: Response<RegisterResponse>
                            ) {
                                if (response.isSuccessful) {
                                    message = "Registration successful"
                                    // Navigate to the main activity
                                    val intent = Intent(context, MainActivity::class.java)
                                    context.startActivity(intent)
                                } else {
                                    message = "A error occurred check your data"
                                }
                            }

                            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                                message = "Network error: ${t.localizedMessage}"
                            }
                        })
                    }
                },
                modifier = Modifier
                    .width(121.dp)
                    .height(51.dp)
                    .offset(y = 16.dp)
                    .border(1.dp, Color(0xFFA6E1FA), RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0A2472),
                    contentColor = Color(0xFFA6E1FA)
                )
            ) {
                Text(
                    "REGISTER",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.fira_code_medium))
                )
            }

            // Display a message based on the registration result
            if (message.isNotEmpty()) {
                Text(
                    message,
                    color = if (message.startsWith("Registration successful")) Color.Green else Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
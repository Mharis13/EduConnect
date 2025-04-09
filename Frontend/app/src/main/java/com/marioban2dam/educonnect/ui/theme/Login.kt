package com.marioban2dam.educonnect.ui.theme

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


@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun LoginScreen() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
        // Título
        Text(
            text = "EDUCONNECT",
            modifier = Modifier
                .offset(x = 55.dp, y = 40.dp),
            fontSize = 48.sp,
            fontFamily = FontFamily(Font(R.font.fira_code_medium)),
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF1E5FC),
            textAlign = TextAlign.Center,
        )

        // Subtítulo
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

        // Logo
        Image(
            painter = painterResource(id = R.drawable.chatgpt_image_14_abr_2025__22_54_37),
            contentDescription = "Logo",
            modifier = Modifier
                .offset(x = 100.dp, y = 150.dp)
                .padding(top = 52.dp),
            alignment = Alignment.Center,
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
        )
        // Mensaje
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

        // Inputs
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .offset(y = 520.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = {
                    Text(
                        text = "DNI / NIA",
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFA6E1FA)
                    )
                },
                placeholder = {
                    Text(
                        text = "Introduce tu DNI o NIA",
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF90CAF9)
                    )
                },
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
                    focusedLabelColor = Color(0xFFA6E1FA),
                    unfocusedLabelColor = Color(0xFFA6E1FA),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        text = "Password",
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFA6E1FA)
                    )
                },
                placeholder = {
                    Text(
                        text = "Introduce tu contraseña",
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF90CAF9)
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.vector__6_),
                        contentDescription = "Icono contraseña",
                        modifier = Modifier.size(24.dp)
                    )
                },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Barra divisoria
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(Color(0xFFA6E1FA))
                                .offset(x = (0).dp,y=0.dp)
                        )

                        // Texto "Forgot?"
                        Text(
                            text = "Forgot?",
                            modifier = Modifier
                                .clickable { /* Acción de recuperación de contraseña */ }
                                .padding(start = 8.dp)
                                .offset(x=(-5).dp,y=0.dp),
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA6E1FA)
                        )
                    }
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
                    focusedLabelColor = Color(0xFFA6E1FA),
                    unfocusedLabelColor = Color(0xFFA6E1FA),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Text(
                modifier = Modifier
                    .width(247.dp)
                    .height(16.dp)
                .offset(y = 0.dp, x = (-30).dp),
                text = "Credentials are incorrect",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                    fontWeight = FontWeight(700),
                    color = Color(0xFFFF4E4E),
                )
            )
            // Botón de login
            Button(
                onClick = { /* TODO: lógica de login */ },
                modifier = Modifier
                    .padding(1.dp)
                    .width(121.dp)
                    .height(51.dp)
                    .offset(y =70.dp, x = 0.dp)
                    .background(color = Color(0xFF0A2472))
                    .border(width = 1.dp,
                        color = Color(0xFFA6E1FA),
                        shape= RoundedCornerShape(12.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor =  Color(0xFFA6E1FA)
                )
            ) {
                Text(
                    text = "LOGIN",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
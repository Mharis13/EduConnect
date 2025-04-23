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
fun RegisterScreen() {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var placeholderText by remember { mutableStateOf("DNI or NIA") }
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


        Box(
            modifier =Modifier
                .width(336.dp)
                .height(48.dp)
                .offset(x = 40.dp, y = 166.dp)
                .border(width = 1.dp,
                    color = Color(0xFFA6E1FA),
                    shape= RoundedCornerShape(12.dp)
                )
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF2F4286), Color(0xFF2448AF)),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        ) {
            Text(
                text = "ACCOUNT CREATION",
                modifier = Modifier
                    .width(336.dp)
                    .height(48.dp)
                    .offset(x = 0.dp, y = 10.dp),
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                    fontWeight = FontWeight(600),
                    color = Color(0xFFA6E1FA),
                    textAlign = TextAlign.Center
                )
            )
        }

        // Botones para seleccionar DNI o NIA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 220.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { placeholderText = "DNI" },
                modifier = Modifier.padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0A2472),
                    contentColor = Color(0xFFA6E1FA)
                )
            ) {
                Text(text = "DNI")
            }
            Button(
                onClick = { placeholderText = "NIA" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0A2472),
                    contentColor = Color(0xFFA6E1FA)
                )
            ) {
                Text(text = "NIA")
            }
        }


        // Inputs
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
                .offset(y = 320.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp), // Reduced spacing to accommodate error messages
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = {
                        Text(
                            text = "Full name",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFA6E1FA)
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Full name",
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
                Text(
                    modifier = Modifier
                        .width(320.dp)
                        .padding(start = 16.dp, top = 4.dp),
                    text = "Name already exists",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                        fontWeight = FontWeight(700),
                        color = Color(0xFFFF4E4E),
                    )
                )
            }

            Column {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text(
                            text = "Email",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFA6E1FA)
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Email",
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
                Text(
                    modifier = Modifier
                        .width(320.dp)
                        .padding(start = 16.dp, top = 4.dp),
                    text = "Email is incorrect",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                        fontWeight = FontWeight(700),
                        color = Color(0xFFFF4E4E),
                    )
                )
            }

            Column {
                OutlinedTextField(
                    value = placeholderText,
                    onValueChange = { placeholderText = it },
                    placeholder = {
                        Text(
                            text = placeholderText,
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF90CAF9)
                        )
                    },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.vector__6_),
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
                Text(
                    modifier = Modifier
                        .width(320.dp)
                        .padding(start = 16.dp, top = 4.dp),
                    text = "Credentials are incorrect",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                        fontWeight = FontWeight(700),
                        color = Color(0xFFFF4E4E),
                    )
                )
            }

            Column {
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
                            text = "Password",
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
                            // Empty trailing icon placeholder
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
                        .width(320.dp)
                        .padding(start = 16.dp, top = 4.dp),
                    text = "Password is incorrect",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                        fontWeight = FontWeight(700),
                        color = Color(0xFFFF4E4E),
                    )
                )
            }

            // Botón de login
            Button(
                onClick = { /* TODO: lógica de login */ },
                modifier = Modifier
                    .padding(0.dp)
                    .width(121.dp)
                    .height(51.dp)
                    .offset(y = 16.dp)
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
                    text = "REGISTER",
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
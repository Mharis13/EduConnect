package com.marioban2dam.educonnect.ui.theme

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marioban2dam.educonnect.R
import com.marioban2dam.educonnect.ui.ChatActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectPersonScreen(
    isTeacher: Boolean,
people: List<PersonParcelable>,
    onPersonSelected: (String) -> Unit,
    onBackPressed: () -> Unit,
    token: String,
) {
    val context = LocalContext.current
    Scaffold(
        containerColor = Color(0xFF0A1A35),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1C2B4A))
            )
        },
        bottomBar = {
            BottomNavigationBar(selectedIndex = 1) { println("Navigation selected: $it") }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A1A35))
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = if (isTeacher) "Select Student" else "Select Teacher",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            people.forEach { person ->
                Log.d("SelectPersonScreen", "Person: ${ person}")
                val input = person.name
                val regex = Regex("""id=(.*?), name=(.*?)\)""")
                val matchResult = regex.find(input)
                var id = ""
                var name = ""

                if (matchResult != null) {
                     id = matchResult.groupValues[1]
                     name = matchResult.groupValues[2]
                    println("SelectPersonScreen: $id")
                    println("SelectPersonScreen: $name")
                } else {
                    println("No se pudo extraer el id y el name.")
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            onPersonSelected(name) // Muestra el nombre en la interfaz

                            val intent = Intent(context, ChatActivity::class.java).apply {
                                putExtra("teacherName", name) // Envía el nombre
                                putExtra("userId", id) // Envía el ID
                                putExtra("token", token) // Envía el token
                            }
                            context.startActivity(intent)
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2B4A)),
                    shape = RoundedCornerShape(12.dp)

                ) {
                    Text(
                        text = name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
package com.marioban2dam.educonnect.ui.theme

                                                import android.util.Log
                                                import androidx.compose.foundation.background
                                                import androidx.compose.foundation.layout.*
                                                import androidx.compose.foundation.shape.RoundedCornerShape
                                                import androidx.compose.foundation.text.BasicTextField
                                                import androidx.compose.material.icons.Icons
                                                import androidx.compose.material.icons.filled.ArrowBack
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
                                                import com.marioban2dam.educonnect.R
                                                import com.marioban2dam.educonnect.retrofit.CourseApi
                                                import com.marioban2dam.educonnect.retrofit.Message
                                                import com.marioban2dam.educonnect.retrofit.RetrofitClient
                                                import kotlinx.coroutines.Dispatchers
                                                import kotlinx.coroutines.launch
                                                import kotlinx.coroutines.withContext

                                                @OptIn(ExperimentalMaterial3Api::class)
                                                @Composable
                                                fun ChatScreen(
                                                    teacherName: String,
                                                    token: String,
                                                    userId: String,
                                                    onSendMessage: (String) -> Unit,
                                                    onBackPressed: () -> Unit
                                                ) {
                                                    Log.d("token", token)
                                                    val courseApi = RetrofitClient.getInstanceWebSockets(token).create(CourseApi::class.java)
                                                    var chatMessages by remember { mutableStateOf<List<Pair<String, Boolean>>>(emptyList()) }
                                                    var inputMessage by remember { mutableStateOf("") }
                                                    val coroutineScope = rememberCoroutineScope()

                                                    LaunchedEffect(Unit) {
                                                        coroutineScope.launch {
                                                            try {
                                                                val receivedMessages = withContext(Dispatchers.IO) {
                                                                    courseApi.getReceivedMessages(userId)
                                                                }
                                                                val sentMessages = withContext(Dispatchers.IO) {
                                                                    courseApi.getSentMessages(userId)
                                                                }
                                                                chatMessages = (receivedMessages + sentMessages).map { it.content to (it.from == userId) }
                                                            } catch (e: Exception) {
                                                                e.printStackTrace()
                                                            }
                                                        }
                                                    }

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
                                                        ) {
                                                            Text(
                                                                text = "Chat with $teacherName",
                                                                fontSize = 22.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.White,
                                                                fontFamily = FontFamily(Font(R.font.fira_code_medium)),
                                                                modifier = Modifier
                                                                    .align(Alignment.CenterHorizontally)
                                                                    .padding(16.dp)
                                                            )
                                                            Spacer(modifier = Modifier.height(8.dp))
                                                            Column(
                                                                modifier = Modifier
                                                                    .weight(1f)
                                                                    .padding(horizontal = 16.dp)
                                                            ) {
                                                                chatMessages.forEach { (message, isUser) ->
                                                                    Box(
                                                                        modifier = Modifier
                                                                            .fillMaxWidth()
                                                                            .padding(vertical = 4.dp),
                                                                        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                                                                    ) {
                                                                        Text(
                                                                            text = message,
                                                                            color = if (isUser) Color.White else Color.Black,
                                                                            fontSize = 16.sp,
                                                                            modifier = Modifier
                                                                                .background(
                                                                                    color = if (isUser) Color(0xFF3391FF) else Color(0xFFE0E0E0),
                                                                                    shape = RoundedCornerShape(12.dp)
                                                                                )
                                                                                .padding(12.dp)
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                            Row(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(16.dp),
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                BasicTextField(
                                                                    value = inputMessage,
                                                                    onValueChange = { inputMessage = it },
                                                                    modifier = Modifier
                                                                        .weight(1f)
                                                                        .background(Color.White, RoundedCornerShape(8.dp))
                                                                        .padding(8.dp),
                                                                    textStyle = LocalTextStyle.current.copy(color = Color.Black)
                                                                )
                                                                Spacer(modifier = Modifier.width(8.dp))
                                                                Button(
                                                                    onClick = {
                                                                        coroutineScope.launch {
                                                                            try {
                                                                                val message = Message(
                                                                                    from = userId,
                                                                                    to = teacherName,
                                                                                    content = inputMessage
                                                                                )
                                                                                withContext(Dispatchers.IO) {
                                                                                    courseApi.sendMessage(message)
                                                                                }
                                                                                chatMessages = chatMessages + (inputMessage to true)
                                                                                inputMessage = ""
                                                                            } catch (e: Exception) {
                                                                                e.printStackTrace()
                                                                            }
                                                                        }
                                                                    },
                                                                    enabled = inputMessage.isNotBlank()
                                                                ) {
                                                                    Text("Send")
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
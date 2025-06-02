package com.marioban2dam.educonnect.ui.theme

      import android.content.Intent
      import android.util.Log
      import android.widget.Toast
      import androidx.compose.foundation.clickable
      import androidx.compose.foundation.layout.*
      import androidx.compose.foundation.lazy.LazyColumn
      import androidx.compose.foundation.lazy.items
      import androidx.compose.foundation.shape.RoundedCornerShape
      import androidx.compose.material3.*
      import androidx.compose.runtime.*
      import androidx.compose.ui.Modifier
      import androidx.compose.ui.graphics.Color
      import androidx.compose.ui.platform.LocalContext
      import androidx.compose.ui.text.font.Font
      import androidx.compose.ui.text.font.FontFamily
      import androidx.compose.ui.text.font.FontWeight
      import androidx.compose.ui.unit.dp
      import androidx.compose.ui.unit.sp
      import com.marioban2dam.educonnect.R
      import com.marioban2dam.educonnect.retrofit.*
      import kotlinx.coroutines.Dispatchers
      import kotlinx.coroutines.withContext
      import kotlinx.coroutines.launch
      import java.io.Serializable

      // Font family used for text styling
      val firaCode = FontFamily(Font(R.font.fira_code_medium))

      /**
       * Data class representing a task.
       *
       * @property title The title of the task.
       * @property description The description of the task (optional).
       * @property dueDate The due date of the task (optional).
       * @property courseId The ID of the course associated with the task (optional).
       */
      data class Task(
          val title: String,
          val description: String?,
          val dueDate: String?,
          val courseId: Int? = null,
      ) : Serializable

      /**
       * Data class representing a grade submission.
       *
       * @property taskId The ID of the task being graded.
       * @property userId The ID of the user submitting the grade.
       * @property score The score assigned to the task.
       * @property link The link associated with the submission.
       */
      data class GradeSubmission(
          val taskId: Int,
          val userId: String,
          val score: Float,
          val link: String
      )

      /**
       * Composable function to display a card for a task.
       *
       * @param task The task to be displayed.
       * @param isTeacher Boolean indicating if the user is a teacher.
       * @param userId The ID of the user interacting with the task.
       * @param token The authentication token for API requests.
       * @param onGradeAssigned Callback triggered when a grade is assigned.
       */
      @Composable
      fun TaskCard(
          task: Task,
          isTeacher: Boolean,
          userId: String,
          token: String,
          onGradeAssigned: (String) -> Unit = {}
      ) {
          val context = LocalContext.current
          val courseApi = RetrofitClient.getInstance(token).create(CourseApi::class.java)
          var inputText by remember { mutableStateOf("") }
          var gradeSubmissions by remember { mutableStateOf<List<GradeSubmission>>(emptyList()) }
          var selectedUserId by remember { mutableStateOf<String?>(null) }
          val coroutineScope = rememberCoroutineScope()

          // Fetch grade submissions for the task
          LaunchedEffect(task.courseId) {
              try {
                  val result = withContext(Dispatchers.IO) {
                      courseApi.getGrades()
                  }.filter { it.taskId == (task.courseId ?: -1) }

                  if (result.isNotEmpty()) {
                      gradeSubmissions = result.map {
                          GradeSubmission(
                              taskId = it.taskId,
                              userId = it.userId,
                              score = it.score,
                              link = it.link
                          )
                      }
                  }
              } catch (e: Exception) {
                  Log.e("TaskCard", "Error fetching grades: ${e.message}")
              }
          }

          // Card UI
          Card(
              modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 8.dp),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2B4A)),
              elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
          ) {
              Column(modifier = Modifier.padding(16.dp)) {
                  // Display task title
                  Text(
                      text = task.title,
                      color = Color.White,
                      fontSize = 20.sp,
                      fontWeight = FontWeight.Bold,
                      fontFamily = firaCode
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  // Display task description
                  Text(
                      text = task.description ?: "No description",
                      color = Color(0xFFB0BEC5),
                      fontSize = 14.sp,
                      fontFamily = firaCode
                  )

                  // Display grade submissions if the user is a teacher
                  if (isTeacher && gradeSubmissions.any { it.link.isNotEmpty() && it.taskId == task.courseId }) {
                      Spacer(modifier = Modifier.height(12.dp))
                      Text("Submitted links:", color = Color.White, fontFamily = firaCode)
                      LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                          items(gradeSubmissions.filter { it.link.isNotEmpty() && it.taskId == task.courseId }) { submission ->
                              Text(
                                  text = "• ${submission.link} (${submission.score})",
                                  color = if (selectedUserId == submission.userId) Color.Green else Color.Cyan,
                                  fontFamily = firaCode,
                                  modifier = Modifier
                                      .padding(4.dp)
                                      .clickable {
                                          selectedUserId = submission.userId
                                          Toast
                                              .makeText(
                                                  context,
                                                  "Selected user: ${submission.userId}",
                                                  Toast.LENGTH_SHORT
                                              )
                                              .show()
                                      }
                              )
                          }
                      }
                  }

                  Spacer(modifier = Modifier.height(12.dp))
                  // Input field for grade or link submission
                  OutlinedTextField(
                      value = inputText,
                      onValueChange = { inputText = it },
                      label = {
                          Text(
                              if (isTeacher) "Enter grade" else "Enter link",
                              fontFamily = firaCode
                          )
                      },
                      singleLine = true,
                      colors = OutlinedTextFieldDefaults.colors(
                          focusedContainerColor = Color.White,
                          unfocusedContainerColor = Color.White,
                          focusedTextColor = Color.Black,
                          unfocusedTextColor = Color.Black
                      ),
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier.fillMaxWidth()
                  )

                  Spacer(modifier = Modifier.height(8.dp))
                  // Button to submit grade or link
                  Button(
                      onClick = {
                          coroutineScope.launch {
                              try {
                                  if (isTeacher) {
                                      val response = withContext(Dispatchers.IO) {
                                          courseApi.gradeTask(
                                              GradeSubmissionDto(
                                                  taskId = task.courseId ?: 0,
                                                  userId = selectedUserId ?: userId,
                                                  score = inputText.toFloat()
                                              )
                                          ).execute()
                                      }
                                      if (response.isSuccessful) {
                                          Toast.makeText(
                                              context,
                                              "Grade assigned successfully",
                                              Toast.LENGTH_SHORT
                                          ).show()
                                      } else {
                                          Toast.makeText(context, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                                      }
                                  } else {
                                      val response = withContext(Dispatchers.IO) {
                                          courseApi.submitTaskLink(
                                              TaskSubmissionDto(
                                                  taskId = task.courseId ?: 0,
                                                  userId = userId,
                                                  link = inputText
                                              )
                                          ).execute()
                                      }
                                      if (response.isSuccessful) {
                                          Toast.makeText(context, "Link submitted successfully", Toast.LENGTH_SHORT).show()
                                      } else {
                                          Toast.makeText(context, "Error submitting link", Toast.LENGTH_SHORT).show()
                                      }
                                  }
                              } catch (e: Exception) {
                                  Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                              }
                          }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3391FF)),
                      shape = RoundedCornerShape(12.dp)
                  ) {
                      Text(
                          text = if (isTeacher) "Save Grade" else "Submit Link",
                          color = Color.White
                      )
                  }
              }
          }
      }

      /**
       * Composable function to display a list of tasks from an intent.
       *
       * @param intent The intent containing the task list.
       * @param isTeacher Boolean indicating if the user is a teacher.
       * @param token The authentication token for API requests.
       * @param userId The ID of the user interacting with the tasks.
       */
      @Composable
      fun TaskListScreenFromIntent(
          intent: Intent,
          isTeacher: Boolean,
          token: String,
          userId: String
      ) {
          val taskList = intent.getSerializableExtra("taskList") as? ArrayList<Task> ?: arrayListOf()

          Scaffold(
              containerColor = Color(0xFF0A1A35)
          ) { innerPadding ->
              Column(
                  modifier = Modifier
                      .padding(innerPadding)
                      .padding(horizontal = 16.dp, vertical = 8.dp)
                      .fillMaxSize()
              ) {
                  taskList.forEach { task ->
                      TaskCard(
                          task = task,
                          isTeacher = isTeacher,
                          userId = userId,
                          token = token
                      )
                  }
              }
          }
      }

      /**
       * Composable function to display the task screen.
       *
       * @param token The authentication token for API requests.
       * @param taskList The list of tasks to be displayed.
       * @param isTeacher Boolean indicating if the user is a teacher.
       * @param userId The ID of the user interacting with the tasks.
       */
     @Composable
      fun TaskScreen(
          token: String,
          taskList: List<Task>,
          isTeacher: Boolean,
          userId: String
      ) {
          val context = LocalContext.current

          Scaffold(
              containerColor = Color(0xFF0A1A35)
          ) { innerPadding ->
              Column(
                  modifier = Modifier
                      .padding(innerPadding)
                      .padding(horizontal = 16.dp, vertical = 8.dp)
                      .fillMaxSize()
              ) {

                  Button(
                      onClick = { (context as? android.app.Activity)?.finish() },
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3391FF)),
                      shape = RoundedCornerShape(12.dp),
                      modifier = Modifier.fillMaxWidth()
                  ) {
                      Text(
                          text = "Go Back",
                          color = Color.White
                      )
                  }

                  Spacer(modifier = Modifier.height(16.dp))


                  LazyColumn(
                      modifier = Modifier.fillMaxSize(),
                      verticalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                      items(taskList) { task ->
                          TaskCard(
                              task = task,
                              isTeacher = isTeacher,
                              userId = userId,
                              token = token
                          )
                      }
                  }
              }
          }
      }
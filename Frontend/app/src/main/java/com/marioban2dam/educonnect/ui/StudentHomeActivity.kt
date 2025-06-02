package com.marioban2dam.educonnect.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.marioban2dam.educonnect.retrofit.CourseApi
import com.marioban2dam.educonnect.retrofit.CourseInterface
import com.marioban2dam.educonnect.retrofit.RetrofitClient

import com.marioban2dam.educonnect.ui.theme.AddStudentsScreen
import com.marioban2dam.educonnect.ui.theme.ChatScreen
import com.marioban2dam.educonnect.ui.theme.CourseDetailScreen
import com.marioban2dam.educonnect.ui.theme.CourseGradesScreen
import com.marioban2dam.educonnect.ui.theme.CreateCourseScreen
import com.marioban2dam.educonnect.ui.theme.PersonParcelable
import com.marioban2dam.educonnect.ui.theme.ProfileScreen
import com.marioban2dam.educonnect.ui.theme.RegisterScreen
import com.marioban2dam.educonnect.ui.theme.SelectPersonScreen
import com.marioban2dam.educonnect.ui.theme.StudentHomeScreen
import com.marioban2dam.educonnect.ui.theme.StudentsListScreen
import com.marioban2dam.educonnect.ui.theme.Task
import com.marioban2dam.educonnect.ui.theme.TaskCard
import com.marioban2dam.educonnect.ui.theme.TaskScreen
import com.marioban2dam.educonnect.ui.theme.TeacherHomeScreen
import org.json.JSONObject
import java.util.Base64

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterScreen()
        }
    }
}

class StudentHomeActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudentHomeScreen()
        }
    }
}

class TeacherHomeActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeacherHomeScreen()
        }
    }
}

class CreateCourseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CreateCourseScreen()
        }
    }
}

class TeacherCourseDetailActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener los parámetros desde el Intent
        val courseId = intent.getIntExtra("courseId", -1)
        val courseName = intent.getStringExtra("courseName") ?: "Curso"
        val courseDescription = intent.getStringExtra("courseDescription") ?: "Sin descripción"
        val token = intent.getStringExtra("token") ?: ""

        // Validar que los parámetros sean válidos
        if (courseId == -1 || token.isEmpty()) {
            finish() // Cierra la actividad si los parámetros no son válidos
            Log.d("TeacherCourseDetailActivity", courseId.toString())

            Log.d("TeacherCourseDetailActivity", token)
            return
        }

        // Decodificar el token para obtener el rol
        val isTeacher = decodeRoleFromToken(token) == "Teacher"
        Log.d("TeacherCourseDetailActivity", "isTeacher: $isTeacher")

        // Crear el objeto del curso
        val course = CourseInterface(
            id = courseId,
            name = courseName,
            description = courseDescription
        )

        setContent {
            CourseDetailScreen(
                course = course,
                isTeacher = isTeacher, // Cambia el comportamiento según el rol
                context = this,
                token = token
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decodeRoleFromToken(token: String): String? {
        return try {
            val parts = token.split(".")
            if (parts.size <= 3) {
                val payload = String(Base64.getUrlDecoder().decode(parts[1]))
                val json = JSONObject(payload)
                json.getString("http://schemas.microsoft.com/ws/2008/06/identity/claims/role") // Extrae el rol del payload
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}

class AddStudentInCourseActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener los parámetros desde el Intent
        val courseId = intent.getIntExtra("courseId", -1)
        val token = intent.getStringExtra("token") ?: ""

        // Validar que los parámetros sean válidos
        if (courseId == -1 || token.isEmpty()) {
            finish() // Cierra la actividad si los parámetros no son válidos
            return
        }

        setContent {
            AddStudentsScreen(
                courseId = courseId,
                token = token,
                onBack = { finish() } // Finaliza la actividad al volver
            )
        }
    }

}

class TaskDetailActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    fun decodeRoleFromToken(token: String): String? {
        return try {
            val parts = token.split(".")
            if (parts.size <= 3) {
                val payload = String(Base64.getUrlDecoder().decode(parts[1]))
                val json = JSONObject(payload)
                json.getString("http://schemas.microsoft.com/ws/2008/06/identity/claims/role") // Extrae el rol del payload
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun decodeUserIdFromToken(token: String): String? {
        return try {
            val parts = token.split(".")
            if (parts.size <= 3) {
                val payload = String(Base64.getUrlDecoder().decode(parts[1]))
                val json = JSONObject(payload)
                json.getString("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress") // Extrae el userId del payload
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener los parámetros desde el Intent
        val taskList = intent.getSerializableExtra("taskList") as? ArrayList<Task> ?: arrayListOf()
        val token = intent.getStringExtra("token") ?: ""
        val isTeacher = decodeRoleFromToken(token) == "Teacher"
        val userId = decodeUserIdFromToken(token) ?: ""

        // Validar que los parámetros sean válidos
        if (taskList.isEmpty() || token.isEmpty() || userId.isEmpty()) {
            finish() // Cierra la actividad si los parámetros no son válidos
            return
        }

        setContent {
            TaskScreen(
                token = token,
                taskList = taskList,
                isTeacher = isTeacher,
                userId = userId,
            )
        }
    }
}

class CourseGradesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener los parámetros desde el Intent
        val token = intent.getStringExtra("token") ?: ""
        val userId = intent.getStringExtra("userId") ?: ""

        // Validar que los parámetros sean válidos
        if (token.isEmpty() || userId.isEmpty()) {
            finish() // Cierra la actividad si los parámetros no son válidos
            return
        }

        setContent {
            CourseGradesScreen(token = token, userId = userId, onBackClick = {
                finish() // Finaliza la actividad al volver
            })
        }
    }
}


class ProfileActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener los parámetros desde el Intent
        val userId = intent.getStringExtra("userId") ?: ""
        val token = intent.getStringExtra("token") ?: ""

        // Validar que los parámetros sean válidos
        if (userId.isEmpty() || token.isEmpty()) {
            finish() // Cierra la actividad si los parámetros no son válidos
            return
        }


        setContent {
            ProfileScreen(
                userId = userId,
                token = token
            )
        }
    }
}

class StudentsListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val courseId = intent.getIntExtra("courseId", -1)
        val token = intent.getStringExtra("token") ?: ""

        setContent {
            StudentsListScreen(courseId = courseId, token = token)
        }
    }
}

class SelectPersonActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener los parámetros desde el Intent
        val isTeacher = intent.getBooleanExtra("isTeacher", false)
        val token = intent.getStringExtra("token") ?: ""
       val people: ArrayList<PersonParcelable>? = intent.getParcelableArrayListExtra("people") // Recupera la lista
        // Validar que los parámetros sean válidos
        if (token.isEmpty() || people?.isEmpty() == true) {
            finish() // Cierra la actividad si los parámetros no son válidos
            return
        }

        setContent {
            SelectPersonScreen(
                isTeacher = isTeacher,
            people = people?.map { PersonParcelable(it.toString(), it.toString()) } ?: emptyList(),
                token = token,

                onPersonSelected = { person ->
                    // Manejar la selección de una persona
                    println("Persona seleccionada: $person")
                },
                onBackPressed = { finish() } // Finaliza la actividad al volver atrás
            )
        }
    }
}
class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener los parámetros desde el Intent
        val teacherName = intent.getStringExtra("teacherName") ?: ""
        val token = intent.getStringExtra("token") ?: ""
        val userId = intent.getStringExtra("userId") ?: ""

        // Validar que los parámetros sean válidos
        if (teacherName.isEmpty() || token.isEmpty() || userId.isEmpty()) {
            finish() // Cierra la actividad si los parámetros no son válidos
            return
        }

        setContent {
            ChatScreen(
                teacherName = teacherName,
                token = token,
                userId = userId,
                onSendMessage = { message ->
                    println("Mensaje enviado: $message")
                },
                onBackPressed = { finish() } // Finaliza la actividad al volver atrás
            )
        }
    }
}

package com.marioban2dam.educonnect.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marioban2dam.educonnect.retrofit.CourseInterface
import com.marioban2dam.educonnect.retrofit.CourseApi
import com.marioban2dam.educonnect.retrofit.RetrofitClient
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class TeacherHomeViewModel : ViewModel() {
    var cours by mutableStateOf<List<CourseInterface>>(emptyList())
        private set

    fun fetchCourses(token: String) {
        viewModelScope.launch {
            try {
                val api = RetrofitClient.getInstance(token).create(CourseApi::class.java)
                val fetchedCourses = api.getCourses()
                Log.d("CoursesListAPI", fetchedCourses.toString())
                cours = fetchedCourses
            } catch (e: Exception) {
                Log.e("FetchCoursesError", "Error fetching courses: ${e.message}")
            }
        }
    }
}

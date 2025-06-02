package com.marioban2dam.educonnect.retrofit

import com.marioban2dam.educonnect.ui.theme.Task
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path

data class Message(
    val from: String,
    val to: String,
    val content : String,
)
data class CourseInterface(
    val id: Int? = null,
    val name: String,
    val description: String,
    val iconResId: Int? = null
)
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String? = null
)
data class TaskSubmissionDto(
    val taskId: Int,
    val userId: String,
    val link: String
)
data class GradeSubmissionDto(
    val taskId: Int,
    val userId: String,
    val score: Float,
)


data class GradeSubmission(
    val taskId: Int,
    val userId: String,
    val score: Float,
    val link: String
)

interface CourseApi {
    @POST("/message/send")
    suspend fun sendMessage(@Body message: Message): Void

    @GET("/message/{userId}/received")
    suspend fun getReceivedMessages(@Path("userId") userId: String): List<Message>

    @GET("/message/{userId}/sent")
    suspend fun getSentMessages(@Path("userId") userId: String): List<Message>
    @GET("/api/v1/user/{id}")
    suspend fun getUserById(@Path("id") id: String): User
    @GET("api/v1/course/student/{studentId}/courses")
    suspend fun getCoursesByUserId(@Path("studentId") userId: String): List<CourseInterface>

    @GET("api/v1/course/teacher/{teacherId}/courses")
    suspend fun getCoursesByTeacherId(@Path("teacherId") teacherId: String): List<CourseInterface>

    @POST("/api/v1/grade/submit")
    suspend fun submitTaskLink(@Body submission: TaskSubmissionDto): Call<Void>

    @GET("/api/v1/grade")
    suspend fun getGrades(): List<GradeSubmission>
    @POST("/api/v1/grade/grade")
    suspend fun gradeTask(@Body gradeDto: GradeSubmissionDto): Call<Void>
    @GET("/api/v1/task")
    suspend fun getAllTasks(): List<Task>
    @POST("/api/v1/course/{courseId}/task")

    suspend fun addTaskToCourse(
        @Path("courseId") courseId: Int,
        @Body task: Task
    ): Call<Void>

  @HTTP(method = "DELETE", path = "/api/v1/course/{id}/students", hasBody = true)
    suspend fun removeStudentFromCourse(
        @Path("id") courseId: Int,
        @Body request: List<String>
    ): Void

    @GET("/api/v1/course/{id}/students/enrolled")
    suspend fun getEnrolledStudents(@Path("id") courseId: Int): List<User>
    @GET("/students/course/{courseId}")
    suspend fun getStudentsByCourse(@Path("courseId") courseId: Int): List<User>
    @GET("/api/v1/user")
   suspend fun getUsers(): List<User>
    @POST("/api/v1/course/{id}/students")
    suspend fun addStudentToCourse(
        @Path("id") courseId: Int,
        @Body request: List<String>
    ): Void
    @POST("/api/v1/course/")
    suspend fun createCourse(@Body courseInterface: CourseInterface): CourseInterface

    @GET("/api/v1/course/")
    suspend fun getCourses(): List<CourseInterface>
}
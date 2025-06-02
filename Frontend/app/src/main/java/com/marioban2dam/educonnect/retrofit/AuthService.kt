package com.marioban2dam.educonnect.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/api/v1/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
    @POST("/api/v1/auth/register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>


}



data class LoginRequest(
    val id: String,
    val password: String,
    val Role: String
)
data class RegisterRequest(
    val id: String,
    val passwordHash: String,
    val name: String,
    val email: String,
    val role: String
)

data class RegisterResponse(
    val id: String,
    val name: String,
    val email: String,
    val role: String
)
data class LoginResponse(
    val token: String
)

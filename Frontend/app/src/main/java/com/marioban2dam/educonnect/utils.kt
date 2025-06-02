package com.marioban2dam.educonnect

import android.os.Build
import androidx.annotation.RequiresApi
import org.json.JSONObject
import java.util.Base64

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
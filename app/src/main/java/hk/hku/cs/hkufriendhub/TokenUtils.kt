package hk.hku.cs.hkufriendhub

import android.util.Base64
import android.util.Log
import org.json.JSONObject

object TokenUtils {

    fun decode(token: String): JSONObject? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) {
                Log.e("JWTUtils", "Invalid token structure")
                return null
            }

            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
            val decodedString = String(decodedBytes, Charsets.UTF_8)

            JSONObject(decodedString)

        } catch (e: Exception) {
            Log.e("JWTUtils", "Failed to decode JWT: ${e.message}")
            null
        }
    }

}
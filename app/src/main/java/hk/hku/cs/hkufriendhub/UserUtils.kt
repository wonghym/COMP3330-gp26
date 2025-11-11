package hk.hku.cs.hkufriendhub

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

object UserUtils {
    interface LoginCallback {
        fun onSuccess(response:JSONObject)
        fun onError(error: VolleyError)
    }

    fun loginHandler(username: String, password: String, context: Context, callback: LoginCallback) {
        val url = "http://10.0.2.2:3001/api/login"
        val requestBody = JSONObject()
        requestBody.put("username", username)
        requestBody.put("password", password)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            { response ->
                Log.d("Login Success", response.toString())

                saveLoginData(context, response)
                callback.onSuccess(response)
            },
            { error ->
                Log.e("Login Failed", error.toString())
                callback.onError(error)
            }
        )

        Volley.newRequestQueue(context).add(jsonObjectRequest)
    }

    fun saveLoginData(context: Context, json: JSONObject) {
        val prefs = context.getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putString(MainActivity.USER_TOKEN, json.getString("token"))
        editor.putString(MainActivity.USER_NAME, json.getString("name"))
        editor.putString(MainActivity.USER_ID, json.getString("id"))
        editor.apply()
    }

    fun clearSavedData(context: Context) {
        val prefs = context.getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        Log.d("UserUtils", "Cleared all saved user data")
    }

}
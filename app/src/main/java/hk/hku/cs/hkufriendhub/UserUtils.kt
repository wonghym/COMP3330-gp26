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

    interface UserCallBack {
        fun onSuccess(response: JSONObject)
        fun onError(error: VolleyError)
    }

    interface PutCallBack {
        fun onSuccess(response: JSONObject)
        fun onError(error: VolleyError)
    }

    fun saveLoginData(context: Context, json: JSONObject) {
        val prefs = context.getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putString(MainActivity.USER_TOKEN, json.getString("token"))
        editor.putString(MainActivity.USER_USERNAME, json.getString("username"))
        editor.putString(MainActivity.USER_ID, json.getString("id"))
        editor.apply()
    }

    fun clearSavedData(context: Context) {
        val prefs = context.getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        Log.d("UserUtils", "Cleared all saved user data")
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

    fun getUserData(context: Context, id: String, callback: UserCallBack) {
        val url = "http://10.0.2.2:3001/api/user/$id"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("UserUtils", response.toString())
                callback.onSuccess(response)
            },
            { error ->
                Log.e("UserUtils", error.toString())
                callback.onError(error)
            }
        )

        Volley.newRequestQueue(context).add(jsonObjectRequest)
    }

    fun putUserData(name: String, bio: String, context: Context, id: String, callback: PutCallBack) {
        var url = "http://10.0.2.2:3001/api/user/$id"
        val requestBody = JSONObject()
        requestBody.put("name", name)
        requestBody.put("bio", bio)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.PUT, url, requestBody,
            { response ->
                callback.onSuccess(response)
            },
            { error ->
                callback.onError(error)
            }
        )
        Volley.newRequestQueue(context).add(jsonObjectRequest)
    }

}
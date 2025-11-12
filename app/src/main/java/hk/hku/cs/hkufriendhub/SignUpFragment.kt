package hk.hku.cs.hkufriendhub

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class SignUpFragment : Fragment() {
    private lateinit var backButton: ImageView
    private lateinit var usernameInput: EditText
    private lateinit var nameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var signupButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        backButton = view.findViewById<ImageView>(R.id.signup_back_button)
        usernameInput = view.findViewById<EditText>(R.id.signup_username_input)
        nameInput = view.findViewById<EditText>(R.id.signup_name_input)
        passwordInput = view.findViewById<EditText>(R.id.signup_password_input)
        confirmPasswordInput = view.findViewById<EditText>(R.id.signup_confirm_password_input)
        signupButton = view.findViewById<Button>(R.id.signup_button)

        backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        signupButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val name = nameInput.text.toString()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()


            if (inputValidation(username, name, password, confirmPassword)) {
                signupButton.isEnabled = false

                val payload = JSONObject()
                payload.put("username", username)
                payload.put("name", password)
                payload.put("password", password)
                postUser(payload)
            }
        }

        return view
    }

    private fun inputValidation(username: String, name: String, password: String, confirmPassword: String): Boolean {
        usernameInput.error = null
        nameInput.error = null
        passwordInput.error = null
        confirmPasswordInput.error = null

        var temp: Boolean = true

        if (username.length < 3) {
            usernameInput.error = "Username cannot be empty and must be 3 or more characters"
            temp = false
        }
        if (name.length < 3) {
            nameInput.error = "Name cannot be empty and must be 3 or more characters"
            temp = false
        } else if (name.length > 20) {
            nameInput.error = "Name must be 20 characters or less"
            temp = false
        }
        if (password.length < 3) {
            passwordInput.error = "Password cannot be empty and must be 3 or more characters"
            temp = false
        }
        if (confirmPassword.isBlank()){
            confirmPasswordInput.error = "Please type the password again for confirmation"
            temp = false
        } else if (confirmPassword != password) {
            confirmPasswordInput.error = "Please type the password again"
            temp = false
        }

        return temp
    }

    private fun postUser(payload: JSONObject) {
        var url = "http://10.0.2.2:3001/api/user"
        Log.d("TEST", payload.toString())

        val request = JsonObjectRequest(
            Request.Method.POST, url, payload,
            {
                response ->
                Log.d("TEST", "SUCCESS")
                Toast.makeText(requireContext(), "User created!", Toast.LENGTH_SHORT).show()
                signupButton.isEnabled = true
                activity?.supportFragmentManager?.popBackStack()
            },
            {
                error ->
                try{
                    usernameInput.error = JSONObject(String(error.networkResponse.data, Charsets.UTF_8)).getString("error")
                    Log.e("TEST", JSONObject(String(error.networkResponse.data, Charsets.UTF_8)).getString("error"))
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Server Error. Please try again.", Toast.LENGTH_SHORT).show()
                } finally {
                    signupButton.isEnabled = true
                }
            }
        )

        Volley.newRequestQueue(requireContext()).add(request)
    }

}
package hk.hku.cs.hkufriendhub

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.VolleyError
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class LoginFragment : Fragment(), UserUtils.LoginCallback {

    private lateinit var usernameInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var signupText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        usernameInput = view.findViewById<TextInputEditText>(R.id.login_username_input)
        passwordInput = view.findViewById<TextInputEditText>(R.id.login_password_input)
        loginButton = view.findViewById<Button>(R.id.login_button)
        signupText = view.findViewById<TextView>(R.id.login_signup)

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginButton.isEnabled = false

                UserUtils.loginHandler(username, password, requireContext(), this)
            } else {
                Toast.makeText(requireContext(), "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }

        setupSignup(view)

        return view
    }

    private fun setupSignup(view: View) {
        val text1 = "Do not have an account? "
        val text2 = "Sign Up"
        val spannable = SpannableString(text2)
        spannable.setSpan(ForegroundColorSpan(Color.BLUE), 0, text2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(UnderlineSpan(), 0, text2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        signupText.text = TextUtils.concat(text1, spannable)

        signupText.setOnClickListener {
            val mainActivity = activity as? MainActivity

            if (mainActivity != null) {
                mainActivity.isLoggedIn = false
                mainActivity.loadFragment(SignUpFragment(), false)
            }
        }
    }

    override fun onSuccess(response: JSONObject) {
        Log.d("LoginFragment", "Login successful: $response")

        Toast.makeText(requireContext(), "Login Successful!", Toast.LENGTH_SHORT).show()

        val mainActivity = activity as? MainActivity
        if (mainActivity != null) {
            mainActivity.isLoggedIn = true
            mainActivity.loadFragment(ProfileFragment(), false)
        }
        loginButton.isEnabled = true
    }

    override fun onError(error: VolleyError) {
        Log.e("LoginFragment", "Login error: ${error.message}")

        passwordInput.text?.clear()

        Toast.makeText(requireContext(), "Login failed: Invalid username or password", Toast.LENGTH_SHORT).show()
        loginButton.isEnabled = true
    }
}
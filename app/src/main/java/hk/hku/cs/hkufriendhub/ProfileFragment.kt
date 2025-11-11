package hk.hku.cs.hkufriendhub

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.android.volley.VolleyError
import org.json.JSONObject

class ProfileFragment : Fragment() {

    private lateinit var message: TextView
    private lateinit var nameInput: TextView
    private lateinit var bioInput: TextView
    private var mainActivity: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val prefs = requireActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE)
        mainActivity = activity as? MainActivity

        val logoutButton = view.findViewById<Button>(R.id.profile_logout_button)
        message = view.findViewById<TextView>(R.id.profile_message)
        nameInput = view.findViewById<TextView>(R.id.profile_name_input)
        bioInput = view.findViewById<TextView>(R.id.profile_bio_input)

        val id = prefs.getString(MainActivity.USER_ID, null)
        val username = prefs.getString(MainActivity.USER_USERNAME, null)

        message.text = "Welcome, $username!"

        fetchUserData(id)

        nameInput

        logoutButton.setOnClickListener {
            UserUtils.clearSavedData(requireContext())

            if (mainActivity != null) {
                mainActivity?.isLoggedIn = false
                mainActivity?.loadFragment(LoginFragment(), false)
            }
        }

        nameInput.setOnClickListener { navToEdit(id) }
        bioInput.setOnClickListener { navToEdit(id) }

        return view
    }

    private fun fetchUserData(id: String?) {
        if (id != null) {
            val callback = fetchUserCallback()

            UserUtils.getUserData(requireContext(), id, callback)
        }
    }

    private inner class fetchUserCallback() : UserUtils.UserCallBack {
        override fun onSuccess(response: JSONObject) {
            Log.d("ProfileFragment", response.toString())

            val username = response.getString("username")
            nameInput.text = response.getString("name")
            bioInput.text = response.getString("bio")
        }

        override fun onError(error: VolleyError) {
            Log.e("ProfileFragment", "Error fetching user data: $error")
        }
    }

    private fun navToEdit(id: String?) {
        val name = nameInput.text.toString()
        val bio = bioInput.text.toString()

        val bundle = Bundle().apply {
            putString("NAME", name)
            putString("BIO", bio)
            putString("ID", id)
        }
        val editFragment = EditProfileFragment().apply {arguments = bundle}
        mainActivity?.loadFragment(editFragment, true)
    }
}
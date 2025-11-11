package hk.hku.cs.hkufriendhub

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val prefs = requireActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE)

        val logoutButton = view.findViewById<Button>(R.id.profile_logout_button)
        val profileMessage = view.findViewById<TextView>(R.id.profile_message)

        val username = prefs.getString(MainActivity.USER_NAME, "User")
        profileMessage.text = "Welcome, $username!"

        logoutButton.setOnClickListener {
            UserUtils.clearSavedData(requireContext())

            val mainActivity = activity as? MainActivity
            if (mainActivity != null) {
                mainActivity.isLoggedIn = false
                mainActivity.loadFragment(LoginFragment(), false)
            }
        }

        return view
    }
}
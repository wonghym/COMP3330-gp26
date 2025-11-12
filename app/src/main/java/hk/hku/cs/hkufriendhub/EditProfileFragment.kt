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
import com.android.volley.VolleyError
import org.json.JSONObject

class EditProfileFragment : Fragment() {

    private lateinit var backButton: ImageView
    private lateinit var nameInput: EditText
    private lateinit var bioInput: EditText
    private lateinit var saveButton: Button
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        backButton = view.findViewById<ImageView>(R.id.editProfile_back_button)
        nameInput = view.findViewById<EditText>(R.id.editProfile_name_input)
        bioInput = view.findViewById<EditText>(R.id.editProfile_bio_input)
        saveButton = view.findViewById<Button>(R.id.editProfile_save_button)
        userId = arguments?.getString("ID")

        nameInput.setText(arguments?.getString("NAME"))
        bioInput.setText(arguments?.getString("BIO", null))

        backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        saveButton.setOnClickListener {
            val name = nameInput.text.toString()
            val bio = bioInput.text.toString()

            if (inputValidation(name, bio)) {
                saveButton.isEnabled = false
                UserUtils.putUserData(name, bio, requireContext(), userId.toString(), putCallback())
            }
        }

        return view
    }

    private fun inputValidation(name: String, bio: String): Boolean {
        nameInput.error = null
        bioInput.error = null
        var temp: Boolean = true

        if (name.length < 3) {
            nameInput.error = "Name cannot be empty and must be 3 or more characters"
            temp = false
        } else if (name.length > 20) {
            nameInput.error = "Name must be 20 characters or less"
            temp = false
        }
        if (bioInput.lineCount > 10) {
            bioInput.error = "Bio must be 10 lines or less"
            temp = false
        }
        return temp
    }

    private inner class putCallback() : UserUtils.PutCallBack {
        override fun onSuccess(response: JSONObject) {
            Log.d("ProfileFragment", response.toString())
            Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()

            val mainActivity = activity as? MainActivity
            if (mainActivity != null) {
                mainActivity.loadFragment(ProfileFragment(), false)
            }
            saveButton.isEnabled = true
        }

        override fun onError(error: VolleyError) {
            Log.e("ProfileFragment", error.toString())
            Toast.makeText(requireContext(), "Server error", Toast.LENGTH_LONG).show()
            saveButton.isEnabled = true
        }
    }
}
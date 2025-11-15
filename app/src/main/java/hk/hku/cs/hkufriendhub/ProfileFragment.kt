package hk.hku.cs.hkufriendhub

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.android.volley.VolleyError
import org.json.JSONObject
import java.io.IOException

class ProfileFragment : Fragment() {

    private lateinit var message: TextView
    private lateinit var profilePicView: ImageView
    private lateinit var nameInput: TextView
    private lateinit var bioInput: TextView
    private var mainActivity: MainActivity? = null
    var base64: String = ""
    private lateinit var selectedAttachment: Uri


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val prefs = requireActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE)
        mainActivity = activity as? MainActivity

        val logoutButton = view.findViewById<Button>(R.id.profile_logout_button)
        message = view.findViewById<TextView>(R.id.profile_message)
        profilePicView = view.findViewById<ImageView>(R.id.profile_pic)
        nameInput = view.findViewById<TextView>(R.id.profile_name_input)
        bioInput = view.findViewById<TextView>(R.id.profile_bio_input)

        val id: String? = mainActivity?.userId
        val username = prefs.getString(MainActivity.USER_USERNAME, null)

        message.text = "Welcome, $username!"

        fetchUserData(id)

        profilePicView.setOnClickListener {
            val popup = PopupMenu(requireContext(), profilePicView)

            popup.menu.add(0, 1, 0, "Upload Picture")
            popup.menu.add(0, 2, 0, "Delete Picture")

            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    1 -> {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, 565)
                    }
                    2 -> {
                        UserUtils.putUserImg(null, requireContext(), mainActivity?.userId, imgDeleteCallback())
                        Toast.makeText(requireContext(), "Deleting profile picture...", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }

            popup.show()

        }

        logoutButton.setOnClickListener {
            UserUtils.clearSavedData(requireContext())

            if (mainActivity != null) {
                mainActivity?.userId = null
                mainActivity?.loadFragment(LoginFragment(), false)
            }
        }

        nameInput.setOnClickListener { navToEdit(id) }
        bioInput.setOnClickListener { navToEdit(id) }

        return view
    }

    private fun fetchUserData(id: String?) {
        if (id != null) {
            UserUtils.getUserData(requireContext(), id, fetchUserCallback())
        }
    }

    private inner class fetchUserCallback() : UserUtils.UserCallBack {
        override fun onSuccess(response: JSONObject) {
            Log.d("ProfileFragment", response.toString())

            nameInput.text = response.getString("name")
            bioInput.text = response.getString("bio")

            val base64ImageString = response.optString("profilePic", null)

            if (!base64ImageString.isNullOrEmpty()) {
                try {
                    val imageBytes = Base64.decode(base64ImageString, Base64.URL_SAFE)

                    val decodedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                    if (decodedBitmap != null) {
                        profilePicView.setImageBitmap(decodedBitmap)
                    } else {
                        Log.e("profilePic", "Failed to decode Base64 into Bitmap.")
                    }
                } catch (e: IllegalArgumentException) {
                    Log.e("profilePic", "Invalid Base64 string format: ${e.message}")
                }
            }
        }

        override fun onError(error: VolleyError) {
            Log.e("profilePic", "Error fetching user data: $error")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 565) {
            val uri: Uri? = data?.data

            if (uri != null) {
                selectedAttachment = uri

                base64 = ""
                try {
                    val bytes = uri?.let {
                        context?.contentResolver?.openInputStream(it)?.readBytes()
                    }
                    base64 = Base64.encodeToString(bytes, Base64.URL_SAFE)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Error processing image.", Toast.LENGTH_SHORT).show()
                    return
                }

                UserUtils.putUserImg(base64, requireContext(), mainActivity?.userId, imgCallback())
            }
        }
    }


    private inner class imgCallback() : UserUtils.ImgCallBack {
        override fun onSuccess(response: JSONObject) {
            Log.d("ProfileFragment", response.toString())
            Toast.makeText(requireContext(), "Profile picture updated", Toast.LENGTH_SHORT).show()

            profilePicView.setImageURI(selectedAttachment)
        }

        override fun onError(error: VolleyError) {
            Log.e("ProfileFragment", error.toString())
            Toast.makeText(requireContext(), "Server error", Toast.LENGTH_LONG).show()
        }
    }

    private inner class imgDeleteCallback() : UserUtils.ImgCallBack {
        override fun onSuccess(response: JSONObject) {
            Log.d("ProfileFragment", "Delete success: $response")

            profilePicView.setImageResource(R.drawable.ic_person_24)

            Toast.makeText(requireContext(), "Profile picture successfully removed.", Toast.LENGTH_SHORT).show()
        }

        override fun onError(error: VolleyError) {
            Log.e("ProfileFragment", error.toString())
            Toast.makeText(requireContext(), "Server error.", Toast.LENGTH_LONG).show()
        }
    }
}
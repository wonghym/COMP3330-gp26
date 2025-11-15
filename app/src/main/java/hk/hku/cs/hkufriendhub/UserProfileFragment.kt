package hk.hku.cs.hkufriendhub

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.VolleyError
import org.json.JSONObject

class UserProfileFragment : Fragment() {

    private lateinit var backButton: ImageView
    private lateinit var profilePicView: ImageView
    private lateinit var nameView: TextView
    private lateinit var bioView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)

        backButton = view.findViewById<ImageView>(R.id.user_profile_back_button)
        profilePicView = view.findViewById<ImageView>(R.id.user_profile_pic)
        nameView = view.findViewById<TextView>(R.id.user_profile_name_input)
        bioView = view.findViewById<TextView>(R.id.user_profile_bio_input)

        backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        arguments?.let { bundle ->
            val userId = bundle.getString("ID")
            val profilePic = bundle.getString("profilePic")

            UserUtils.getUserData(requireContext(), userId.toString(), userCallback())

            if (!profilePic.isNullOrEmpty()) {
                try {
                    val imageBytes = Base64.decode(profilePic, Base64.URL_SAFE)

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

        return view
    }

    private inner class userCallback() : UserUtils.UserCallBack {
        override fun onSuccess(response: JSONObject){
            nameView.text = response.getString("name")
            bioView.text = response.getString("bio")
        }

        override fun onError(error: VolleyError) {
            Toast.makeText(requireContext(), "Server error", Toast.LENGTH_LONG).show()
        }
    }
}
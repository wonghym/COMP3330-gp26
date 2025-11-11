package hk.hku.cs.hkufriendhub

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class AddPostFragment : Fragment() {

    private lateinit var backButton: ImageView
    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var hashtag1Input: EditText
    private lateinit var hashtag2Input: EditText
    private lateinit var hashtag3Input: EditText
    private lateinit var maxStudentsInput: AutoCompleteTextView
    private lateinit var submitButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_add_post, container, false)

        backButton = view.findViewById<ImageView>(R.id.addPost_back_button)
        titleInput = view.findViewById<EditText>(R.id.addPost_title_input)
        descriptionInput = view.findViewById<EditText>(R.id.addPost_text_input)
        hashtag1Input = view.findViewById<EditText>(R.id.addPost_hashtag1)
        hashtag2Input = view.findViewById<EditText>(R.id.addPost_hashtag2)
        hashtag3Input = view.findViewById<EditText>(R.id.addPost_hashtag3)
        maxStudentsInput = view.findViewById<AutoCompleteTextView>(R.id.addPost_gpsize_input)
        submitButton = view.findViewById<Button>(R.id.addPost_submit_button)

        setupDropdown()

        backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        submitButton.setOnClickListener {
            submitPostHander()
        }

        return view
    }

    private fun setupDropdown() {
        val gpsizeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gpsize,
            android.R.layout.simple_spinner_dropdown_item,
        )

        maxStudentsInput.setAdapter(gpsizeAdapter)
    }

    private fun submitPostHander() {
        val prefs = requireActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE)
        val userId = prefs.getString(MainActivity.USER_ID, null)

        if (userId == null) {
            return
        }

        val title = titleInput.text.toString().trim()
        val content = descriptionInput.text.toString().trim()
        val maxStat = maxStudentsInput.text.toString()

        if (title.isEmpty() || content.isEmpty() || maxStat.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val payload = JSONObject()
        try {
            val hashtags = JSONArray()
            for (i in arrayOf(hashtag1Input, hashtag2Input, hashtag3Input)) {
                if (i.text.isNotBlank()) hashtags.put(i.text.toString().trim())
            }

            payload.put("title", title)
            payload.put("content", content)
            payload.put("hashtags", hashtags)
            payload.put("curstat", "0")
            payload.put("maxstat", if (maxStat == "Unlimited") "0" else maxStat)
            payload.put("user", userId)
        } catch (e: Exception) {
            Log.e("PostDetail", "Failed to build JSON", e)
            return
        }

        postPost(payload)
    }

    private fun postPost(payload: JSONObject) {
        var url = "http://10.0.2.2:3001/api/post"
        submitButton.isEnabled = false

        val request = JsonObjectRequest(
            Request.Method.POST, url, payload,
            {
                response ->
                Toast.makeText(requireContext(), "Post created!", Toast.LENGTH_SHORT).show()
                submitButton.isEnabled = true

                activity?.supportFragmentManager?.popBackStack()

            },
            {
                error ->
                Toast.makeText(requireContext(), "Server Error. Please try again.", Toast.LENGTH_LONG).show()
                submitButton.isEnabled = true
            }
        )

        Volley.newRequestQueue(requireContext()).add(request)
    }

}
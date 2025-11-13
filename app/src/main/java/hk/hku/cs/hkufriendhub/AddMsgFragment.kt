package hk.hku.cs.hkufriendhub

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import hk.hku.cs.hkufriendhub.MainActivity.Companion.SHARED_PREFS
import org.json.JSONObject

class AddMsgFragment : Fragment() {
    private var postId: String? = null
    private lateinit var backButton: ImageView
    private lateinit var text: EditText
    private lateinit var postButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_msg, container, false)

        val mainActivity = activity as? MainActivity
        val userId: String? = mainActivity?.userId

        backButton = view.findViewById<ImageView>(R.id.addMsg_back_button)
        text = view.findViewById<EditText>(R.id.addMsg_text_input)
        postButton = view.findViewById<Button>(R.id.addMsg_submit_button)
        postId = arguments?.getString("ID")

        backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        postButton.setOnClickListener {
            if (inputValidate(text.text.toString())) {
                val requestBody = JSONObject()
                requestBody.put("content", text.text.toString())
                requestBody.put("like", "0")
                requestBody.put("user", userId)
                requestBody.put("post", postId)

                postMsg(requestBody)
            }
        }

        return view
    }

    fun inputValidate(comment: String): Boolean {
        text.error = null
        var temp = true
        if (comment.isEmpty()) {
            text.error = "Comment can not be empty"
            temp = false
        }
        if (comment.length > 200) {
            text.error = "Comment is too long"
            temp = false
        }
        return temp
    }

    fun postMsg(payload: JSONObject) {
        val url = "http://10.0.2.2:3001/api/forum"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, payload,
            {
                response ->
                Toast.makeText(context, "Posted comment!", Toast.LENGTH_SHORT).show()
                activity?.supportFragmentManager?.popBackStack()
            },
            {
                error ->

            }
        )
        Volley.newRequestQueue(context).add(jsonObjectRequest)
    }

}
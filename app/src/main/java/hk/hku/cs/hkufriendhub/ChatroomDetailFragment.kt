package hk.hku.cs.hkufriendhub

import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.concurrent.Executors

class ChatroomDetailFragment : Fragment() {

    private lateinit var chatroom: ChatroomModel
    private lateinit var backButton: ImageView
    private lateinit var title: TextView
    private lateinit var msgInput: EditText
    private lateinit var msgSendButton: ImageButton
    private lateinit var chatroomDetailAdapter: ChatroomDetailAdapter
    private lateinit var recyclerView: RecyclerView
    private var userId: String? = null
    private val chatList = ArrayList<ChatModel>()

    val executor = Executors.newSingleThreadExecutor()
    val handler = android.os.Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainActivity = activity as? MainActivity
        val view = inflater.inflate(R.layout.fragment_chatroom_detail, container, false)

        arguments?.let {
            chatroom = it.getSerializable("CHATROOM_DATA") as ChatroomModel
        }

        backButton = view.findViewById<ImageView>(R.id.chatroom_back_button)
        title = view.findViewById<TextView>(R.id.chatroom_title)
        msgInput = view.findViewById<EditText>(R.id.chatroom_input)
        msgSendButton = view.findViewById<ImageButton>(R.id.chatroom_send)
        recyclerView = view.findViewById<RecyclerView>(R.id.chatroom_recycler)

        userId = mainActivity?.userId

        backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        msgSendButton.setOnClickListener {
            executor.execute { handler.post { postChat(chatroom.id) } }
        }

        title.text = chatroom.name

        getChat(chatroom.id)

        chatroomDetailAdapter = ChatroomDetailAdapter(chatList, userId)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = chatroomDetailAdapter

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val url = "http://10.0.2.2:3001/api/chat/${userId}"
        val payload = JSONObject()
        payload.put("postId", chatroom.id)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.PUT, url, payload,
            {
                response ->
            },
            {
                error ->
            }
        )

        parentFragmentManager.setFragmentResult(
            "RESET_NOTI",
            Bundle().apply {
                putString("CHATROOM_ID", chatroom.id)
            }
        )
        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest)

        recyclerView.adapter = null
    }

    fun getChat(postId: String) {
        val url = "http://10.0.2.2:3001/api/chat/post/$postId"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            {
                response ->

                chatList.clear()

                for (i in 0 until response.length()) {
                    val jsonObject = response.getJSONObject(i)

                    val chatItem = ChatModel(
                        content = jsonObject.getString("content"),
                        sender = jsonObject.getJSONObject("sender"),
                        time = jsonObject.getString("time")
                    )
                    chatList.add(chatItem)
                }

                updateUI()
            },
            {
                error ->
            }
        )
        Volley.newRequestQueue(context).add(jsonArrayRequest)
    }

    fun postChat(postId: String) {
        val content = msgInput.text.toString().trim()
        if (content.isEmpty() || userId.isNullOrEmpty()) return

        val payload = JSONObject()
        try {
            payload.put("content", content)
            payload.put("postId", postId)
            payload.put("userId", userId)
        } catch (e: Exception) {
            Log.e("PostMsg", "Failed to build JSON", e)
            return
        }

        val url = "http://10.0.2.2:3001/api/chat"
        msgSendButton.isEnabled = false

        val request = JsonObjectRequest(
            Request.Method.POST, url, payload,
            { response ->
                try {
                    val newChatModel = ChatModel(
                        content = response.getString("content"),
                        sender = response.getJSONObject("sender"),
                        time = response.getString("time")
                    )

                    chatList.add(newChatModel)

                    val newPosition = chatList.size - 1
                    chatroomDetailAdapter.notifyItemInserted(newPosition)

                    recyclerView.scrollToPosition(newPosition)

                    msgInput.setText("")

                } catch (e: Exception) {
                    Log.e("PostMsg", "Failed to parse API response", e)
                    getChat(postId)
                }

                msgSendButton.isEnabled = true
            },
            { error ->
                Log.e("PostMsg", "Post failed: $error")
                msgSendButton.isEnabled = true
            }
        )
        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun updateUI() {
        chatroomDetailAdapter.notifyDataSetChanged()

        if (chatList.isNotEmpty()) {
            recyclerView.scrollToPosition(chatList.size - 1)
        }
    }

}
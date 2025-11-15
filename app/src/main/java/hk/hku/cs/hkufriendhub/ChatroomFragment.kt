package hk.hku.cs.hkufriendhub

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

class ChatroomFragment : Fragment(), OnChatroomClickListener {

    private lateinit var recyclerView: RecyclerView
    private val chatroomList = ArrayList<ChatroomModel>()
    private lateinit var chatroomAdapter: ChatroomAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parentFragmentManager.setFragmentResultListener("RESET_NOTI", this){
            key, bundle ->
            val updatedChatroomId = bundle.getString("CHATROOM_ID")

            if (updatedChatroomId != null) {
                val index = chatroomList.indexOfFirst { it.id == updatedChatroomId }

                if (index != -1) {
                    chatroomList[index].notificationCount = 0

                    Log.d("ChatUpdate", "Item at index $index count set to 0. $updatedChatroomId")
                    chatroomAdapter.notifyItemChanged(index)
                }
            }
        }

        val view = inflater.inflate(R.layout.fragment_chatroom, container, false)

        recyclerView = view.findViewById<RecyclerView>(R.id.chatroom_list_recycler)

        chatroomAdapter = ChatroomAdapter(chatroomList, this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = chatroomAdapter

        getChatrooms()

        return view
    }

    override fun onChatroomClick(chatroom: ChatroomModel) {
        val chatroomDetailFragment = ChatroomDetailFragment()
        val bundle = Bundle()
        bundle.putSerializable("CHATROOM_DATA", chatroom)
        chatroomDetailFragment.arguments = bundle

        (activity as? MainActivity)?.loadFragment(chatroomDetailFragment, true)
    }

    private fun getChatrooms() {
        val mainActivity = activity as? MainActivity
        val url = "http://10.0.2.2:3001/api/chat/user/${mainActivity?.userId}"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            {
                response ->
                chatroomList.clear()

                for (i in 0 until response.length()) {
                    val jsonObject = response.getJSONObject(i)

                    val chatroomItem = ChatroomModel(
                        id = jsonObject.getString("id"),
                        name = jsonObject.getString("title"),
                        notificationCount = jsonObject.getInt("notiCount")
                    )
                    chatroomList.add(chatroomItem)
                }


                updateUI()
            },
            {
                error ->
                Log.e("ChatroomFragmentError", error.toString())
            }
        )
        Volley.newRequestQueue(context).add(jsonArrayRequest)
    }

    fun updateUI() {
        chatroomAdapter.notifyDataSetChanged()
    }
}
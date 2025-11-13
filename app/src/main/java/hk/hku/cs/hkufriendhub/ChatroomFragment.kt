package hk.hku.cs.hkufriendhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatroomFragment : Fragment(), OnChatroomClickListener {

    private lateinit var recyclerView: RecyclerView
    private val chatroomList = ArrayList<ChatroomModel>()
    private lateinit var chatroomAdapter: ChatroomAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chatroom, container, false)

        recyclerView = view.findViewById<RecyclerView>(R.id.chatroom_list_recycler)

        chatroomAdapter = ChatroomAdapter(chatroomList, this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = chatroomAdapter

        return view
    }

    override fun onChatroomClick(chatroom: ChatroomModel) {

    }

    fun updateUI() {
        chatroomAdapter.notifyDataSetChanged()
    }
}
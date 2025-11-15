package hk.hku.cs.hkufriendhub

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

interface OnChatroomClickListener {
    fun onChatroomClick(chatroom: ChatroomModel)
}

class ChatroomAdapter(val chatroomList: ArrayList<ChatroomModel>, val clickListener: OnChatroomClickListener): RecyclerView.Adapter<ChatroomAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.chatroom_item, parent, false)
        return ViewHolder(v, clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(chatroomList[position])
    }

    override fun getItemCount(): Int {
        return chatroomList.size
    }

    class ViewHolder(itemView: View, listener: OnChatroomClickListener): RecyclerView.ViewHolder(itemView) {
        private var currentChatroom: ChatroomModel? = null
        private val chatroomNameText: TextView = itemView.findViewById<TextView>(R.id.chatroom_item_name)
        private val chatroomNotiText: TextView = itemView.findViewById<TextView>(R.id.chatroom_item_notification)

        init {
            itemView.setOnClickListener {
                currentChatroom?.let {
                    chatroom -> listener.onChatroomClick(chatroom)
                }
            }
        }
        fun bindItems(chatroom: ChatroomModel) {
            currentChatroom = chatroom

            chatroomNameText.text = chatroom.name
            if (chatroom.notificationCount > 0) {
                chatroomNotiText.text = chatroom.notificationCount.toString()
                chatroomNotiText.visibility = View.VISIBLE
            } else {
                chatroomNotiText.visibility = View.GONE
            }
        }
    }
}
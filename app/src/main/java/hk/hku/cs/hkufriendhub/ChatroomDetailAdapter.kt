package hk.hku.cs.hkufriendhub

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class ChatroomDetailAdapter(val chatList: ArrayList<ChatModel>, val userId: String?): RecyclerView.Adapter<ChatroomDetailAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.chatroom_chat_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(chatList[position], userId)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val messageBubbleContainer: ConstraintLayout = itemView.findViewById<ConstraintLayout>(R.id.message_bubble_container)
        private val sender: TextView = itemView.findViewById<TextView>(R.id.text_message_sender)
        private val content: TextView = itemView.findViewById<TextView>(R.id.text_message_body)
        private val time: TextView = itemView.findViewById<TextView>(R.id.text_message_time)
        fun bindItems(chat: ChatModel, userId: String?) {
            content.text = chat.content
            time.text = TimeUtils.calculateTime(chat.time)

            val isSender = chat.sender.getString("id") == userId
            val context = itemView.context
            val params = messageBubbleContainer.layoutParams as ConstraintLayout.LayoutParams

//            Log.d("CHATROOM", chat.sender.getString("id"))
//            Log.d("CHATROOM", userId.toString())

            if (isSender) {
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                params.startToStart = ConstraintLayout.LayoutParams.UNSET

                messageBubbleContainer.background = ContextCompat.getDrawable(context, R.drawable.sent_chatbubble)
                sender.text = "You"
            } else {
                params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                params.endToEnd = ConstraintLayout.LayoutParams.UNSET

                messageBubbleContainer.background = ContextCompat.getDrawable(context, R.drawable.received_chatbubble)
                sender.text = chat.sender.getString("name")
            }

            messageBubbleContainer.layoutParams = params

        }
    }
}
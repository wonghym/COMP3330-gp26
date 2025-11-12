package hk.hku.cs.hkufriendhub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ForumAdapter(val messageList: ArrayList<ForumModel>): RecyclerView.Adapter<ForumAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(messageList[position])
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindItems(message: ForumModel) {
            val name = itemView.findViewById<TextView>(R.id.forum_name)
            val time = itemView.findViewById<TextView>(R.id.forum_time)
            val content = itemView.findViewById<TextView>(R.id.forum_post_content)

            name.text = message.name
            time.text = TimeUtils.calculateTime(message.timestamp)
            content.text = message.text
        }
    }
}
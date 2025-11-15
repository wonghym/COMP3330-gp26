package hk.hku.cs.hkufriendhub

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
            val profilePic = itemView.findViewById<ImageView>(R.id.forum_profilepic)
            val name = itemView.findViewById<TextView>(R.id.forum_name)
            val time = itemView.findViewById<TextView>(R.id.forum_time)
            val content = itemView.findViewById<TextView>(R.id.forum_post_content)


            val base64ImageString = message.profilePic

            if (!base64ImageString.isNullOrEmpty()) {
                try {
                    val imageBytes = Base64.decode(base64ImageString, Base64.URL_SAFE)

                    val decodedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                    if (decodedBitmap != null) {
                        profilePic.setImageBitmap(decodedBitmap)
                    } else {
                        Log.e("profilePic", "Failed to decode Base64 into Bitmap.")
                    }
                } catch (e: IllegalArgumentException) {
                    Log.e("profilePic", "Invalid Base64 string format: ${e.message}")
                }
            }

            profilePic.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("ID", message.id)
                    putString("profilePic", message.profilePic)
                }

                val userProfileFragment = UserProfileFragment().apply{arguments = bundle}
                val context = itemView.context
                if (context is MainActivity) {
                    context.loadFragment(userProfileFragment, true)
                }
            }

            name.text = message.name
            time.text = TimeUtils.calculateTime(message.timestamp)
            content.text = message.text
        }
    }
}
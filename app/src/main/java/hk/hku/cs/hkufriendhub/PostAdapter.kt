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
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlin.coroutines.coroutineContext

interface OnPostClickListener {
    fun onPostClick(post: PostModel)
}

class PostAdapter(val postList: ArrayList<PostModel>, val clickListener: OnPostClickListener) : RecyclerView.Adapter<PostAdapter.ViewHolder> () {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_post, parent, false);
        return ViewHolder(v, clickListener)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bindItems(postList[position]);
    }

    override fun getItemCount(): Int {
        return postList.size;
    }

    class ViewHolder(itemView: View, listener: OnPostClickListener): RecyclerView.ViewHolder(itemView) {

        private var currentPost: PostModel? = null
        init{
            itemView.setOnClickListener {
                currentPost?.let {
                    post -> listener.onPostClick(post)
                }
            }
        }
        fun bindItems(post: PostModel) {
            currentPost = post

            itemView.findViewById<TextView>(R.id.post_username).text = if (post.hidename == true) "Anonymous" else post.name;
            itemView.findViewById<TextView>(R.id.post_time).text = TimeUtils.getFormattedDate(post.timestamp);
            itemView.findViewById<TextView>(R.id.post_title).text = post.title;
            itemView.findViewById<TextView>(R.id.post_text).text = post.text;
            itemView.findViewById<TextView>(R.id.post_group_stat).text = post.groupStat;

            if (post.hidename == false){
                val base64ImageString = post.profilePic

                if (!base64ImageString.isNullOrEmpty()) {
                    try {
                        val imageBytes = Base64.decode(base64ImageString, Base64.URL_SAFE)

                        val decodedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                        if (decodedBitmap != null) {
                            itemView.findViewById<ImageView>(R.id.post_profile).setImageBitmap(decodedBitmap)
                        } else {
                            Log.e("profilePic", "Failed to decode Base64 into Bitmap.")
                        }
                    } catch (e: IllegalArgumentException) {
                        Log.e("profilePic", "Invalid Base64 string format: ${e.message}")
                    }
                }

                itemView.findViewById<ImageView>(R.id.post_profile).setOnClickListener {
                    val bundle = Bundle().apply {
                        putString("ID", post.userId)
                        putString("profilePic", post.profilePic)
                    }

                    val userProfileFragment = UserProfileFragment().apply{arguments = bundle}
                    val context = itemView.context
                    if (context is MainActivity) {
                        context.loadFragment(userProfileFragment, true)
                    }
                }
            }

            val hashtagContainer = itemView.findViewById<ChipGroup>(R.id.post_hashtag_container)
            hashtagContainer.removeAllViews()

            for (hashtag in post.hashtags) {
                val chip = Chip(itemView.context)
                chip.text = hashtag
                chip.setChipBackgroundColorResource(R.color.grey)
                chip.shapeAppearanceModel = chip.shapeAppearanceModel.toBuilder().setAllCornerSizes(100f).build()
                chip.chipStrokeWidth = 0f
                hashtagContainer.addView(chip)
            }
        }
    }
}
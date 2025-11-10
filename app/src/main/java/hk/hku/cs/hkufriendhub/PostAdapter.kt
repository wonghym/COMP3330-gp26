package hk.hku.cs.hkufriendhub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class PostAdapter(val postList: ArrayList<PostModel>) : RecyclerView.Adapter<PostAdapter.ViewHolder> () {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_post, parent, false);
        return ViewHolder(v)
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

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindItems(post: PostModel) {
            itemView.findViewById<TextView>(R.id.post_username).text = post.username;
            itemView.findViewById<TextView>(R.id.post_time).text = TimeUtils.getFormattedDate(post.timestamp);
            itemView.findViewById<TextView>(R.id.post_title).text = post.title;
            itemView.findViewById<TextView>(R.id.post_text).text = post.text;
            itemView.findViewById<TextView>(R.id.post_group_stat).text = post.groupStat;
            //            itemView.findViewById<TextView>(R.id.post_hashtag_container)

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
package hk.hku.cs.hkufriendhub

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class PostDetailFragment : Fragment() {

    private lateinit var post: PostModel
    private lateinit var backButton: ImageView
    private lateinit var title: TextView
    private lateinit var username: TextView
    private lateinit var time: TextView
    private lateinit var description: TextView
    private lateinit var joinButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_detail, container, false)

        arguments?.let {
            post = it.getSerializable("POST_DATA") as PostModel
        }

        backButton = view.findViewById<ImageView>(R.id.postDetail_back_button)
        title = view.findViewById<TextView>(R.id.postDetail_title)
        username = view.findViewById<TextView>(R.id.postDetail_username)
        time = view.findViewById<TextView>(R.id.postDetail_time)
        description = view.findViewById<TextView>(R.id.postDetail_description)
        joinButton = view.findViewById<Button>(R.id.postDetail_join_button)

        backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        joinButton.setOnClickListener {

        }

        title.text = post.title
        username.text = post.username
        time.text = TimeUtils.getFormattedDate(post.timestamp)
        description.text = post.text
        joinButton.text = if (post.groupStat == "--/--") "Join" else "Join (${post.groupStat})"

        return view
    }
}
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class PostDetailFragment : Fragment() {

    private lateinit var post: PostModel
    private lateinit var backButton: ImageView
    private lateinit var title: TextView
    private lateinit var username: TextView
    private lateinit var time: TextView
    private lateinit var description: TextView
    private lateinit var joinButton: Button
    private lateinit var forumRecyclerView: RecyclerView
    private lateinit var forumAdapter: ForumAdapter
    private var userId: String? = null
    private val msgList = ArrayList<ForumModel>()
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

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
        username = view.findViewById<TextView>(R.id.postDetail_name)
        time = view.findViewById<TextView>(R.id.postDetail_time)
        description = view.findViewById<TextView>(R.id.postDetail_description)
        joinButton = view.findViewById<Button>(R.id.postDetail_join_button)

        val addMsgButton = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.postDetail_forum_addMsg)
        forumRecyclerView = view.findViewById<RecyclerView>(R.id.postDetail_forum_recycler)
        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.postDetail_swipe_refresh)

        val mainActivity = activity as? MainActivity
        userId = mainActivity?.userId
        addMsgButton.visibility = if (mainActivity != null && mainActivity.userId != null) View.VISIBLE else View.GONE

        backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        swipeRefreshLayout.setOnRefreshListener {
            getMsg(post.id)
        }

        addMsgButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("ID", post.id)
            }
            val addMsgFragment = AddMsgFragment().apply{arguments = bundle}
            (activity as? MainActivity)?.loadFragment(addMsgFragment, true)
        }

        joinButton.setOnClickListener {
            putJoin(post.id, userId)
        }

        title.text = post.title
        username.text = post.name
        time.text = TimeUtils.getFormattedDate(post.timestamp)
        description.text = post.text
        joinButton.text = if (post.userId == mainActivity?.userId) "Cannot join to own group!" else when (post.groupStat) {
            "FULL" -> {
                if (post.isJoined) "Joined (FULL)" else "FULL"
            }
            "--/--" -> {
                if (post.isJoined) "Joined" else "Join"
            }
            else -> {
                if (post.isJoined) "Joined (${post.groupStat})" else "Join (${post.groupStat})"
            }
        }
        joinButton.isEnabled = !(post.isJoined || post.groupStat == "FULL" || post.userId == mainActivity?.userId)

        getMsg(post.id)

        forumAdapter = ForumAdapter(msgList)
        forumRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        forumRecyclerView.adapter = forumAdapter

        swipeRefreshLayout.isRefreshing = true

        return view
    }

    fun getMsg(id: String) {
        val url = "http://10.0.2.2:3001/api/forum/$id"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            {
                response ->

                msgList.clear()

                for (i in 0 until response.length()) {
                    val jsonObject = response.getJSONObject(i)

                    val forumItem = ForumModel(
                        id = jsonObject.getString("id"),
                        name = jsonObject.getJSONObject("user").getString("name"),
                        timestamp = jsonObject.getString("date"),
                        text = jsonObject.getString("content"),
                        like = jsonObject.getInt("like")
                    )
                    msgList.add(forumItem)
                }
                updateUI()

                swipeRefreshLayout.isRefreshing = false

            },
            {
                error ->
                swipeRefreshLayout.isRefreshing = false
            }
        )
        Volley.newRequestQueue(context).add(jsonArrayRequest)
    }

    fun putJoin(post: String, user: String?) {
        if (user == null) {
            return
        }
        joinButton.isEnabled = false
        val payload = JSONObject()
        payload.put("user", user)
        payload.put("post", post)

        val url = "http://10.0.2.2:3001/api/post/join"
        val jsonObjectRequest = JsonObjectRequest (
            Request.Method.PUT, url, payload,
            {
                response ->
                val curstat = response.getJSONObject("post").getInt("curstat")
                val maxstat = response.getJSONObject("post").getInt("maxstat")
                joinButton.isEnabled = false
                joinButton.text = when (maxstat) {
                    curstat -> "Joined (FULL)"
                    0 -> "Joined"
                    else -> "Joined ($curstat/$maxstat)"
                }
                updateUI()
            },
            {
                error ->
                joinButton.isEnabled = true
            }
        )

        Volley.newRequestQueue(context).add(jsonObjectRequest)
    }

    fun updateUI() {
        forumAdapter.notifyDataSetChanged()
    }
}
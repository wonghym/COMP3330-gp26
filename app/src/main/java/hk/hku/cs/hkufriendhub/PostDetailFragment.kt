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
import com.android.volley.toolbox.Volley

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
        addMsgButton.visibility = if (mainActivity != null && mainActivity.isLoggedIn) View.VISIBLE else View.GONE

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

        }

        title.text = post.title
        username.text = post.name
        time.text = TimeUtils.getFormattedDate(post.timestamp)
        description.text = post.text
        joinButton.text = if (post.groupStat == "--/--") "Join" else "Join (${post.groupStat})"

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
                        like = jsonObject.getString("like")
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

    fun updateUI() {
        forumAdapter.notifyDataSetChanged()
    }
}
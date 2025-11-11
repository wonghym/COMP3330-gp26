package hk.hku.cs.hkufriendhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
class MainFragment : Fragment(), OnPostClickListener {
    private lateinit var recyclerView: RecyclerView
    private val postList = ArrayList<PostModel>()
    private lateinit var postAdapter: PostAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val addPostButton = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.main_add_post)

        recyclerView = view.findViewById<RecyclerView>(R.id.main_recyclerView)
        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.main_swipe_refresh)

        addPostButton.setOnClickListener {
            (activity as? MainActivity)?.loadFragment(AddDetailFragment(), true)
        }

        val mainActivity = activity as? MainActivity
        addPostButton.visibility = if (mainActivity != null && mainActivity.isLoggedIn) View.VISIBLE else View.GONE

        swipeRefreshLayout.setOnRefreshListener {
            getPosts()
        }

        postAdapter = PostAdapter(postList, this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = postAdapter

        swipeRefreshLayout.isRefreshing = true
        getPosts()

        return view
    }

    override fun onPostClick(post: PostModel) {
        val postDetailFragment = PostDetailFragment()
        val bundle = Bundle()
        bundle.putSerializable("POST_DATA", post)
        postDetailFragment.arguments = bundle

        (activity as? MainActivity)?.loadFragment(postDetailFragment, true)
    }

    fun getPosts() {
        val url = "http://10.0.2.2:3001/api/post"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            {
                response ->

                postList.clear()

                for (i in 0 until response.length()) {
                    val jsonObject = response.getJSONObject(i)
                    val user = jsonObject.getJSONObject("user").getString("name")
                    val date = jsonObject.getString("date")
                    val title = jsonObject.getString("title")
                    val text = jsonObject.getString("content")

                    val hashtags = ArrayList<String>();
                    val hashtagsList = jsonObject.getJSONArray("hashtags")
                    for (j in 0 until hashtagsList.length()) {
                        hashtags.add(hashtagsList[j].toString())
                    }

                    var curstat = jsonObject.getString("curstat")
                    var maxstat = jsonObject.getString("maxstat")
                    val stat = if (maxstat == "0") "--/--" else "$curstat/$maxstat"

                    val postItem = PostModel(
                        username = jsonObject.getJSONObject("user").getString("name"),
                        timestamp = jsonObject.getString("date"),
                        title = jsonObject.getString("title"),
                        text = jsonObject.getString("content"),
                        hashtags = hashtags,
                        groupStat = stat
                    )
                    postList.add(postItem)
                }
//                Log.d("Response Parsing pre", postList.toString())
                updateUI()

                swipeRefreshLayout.isRefreshing = false
            },
            {error ->
                Log.e("MainFragmentError", error.toString())
                swipeRefreshLayout.isRefreshing = false
            }
        )
        Volley.newRequestQueue(context).add(jsonArrayRequest);
    }

    fun updateUI() {
        postAdapter.notifyDataSetChanged()
    }
}
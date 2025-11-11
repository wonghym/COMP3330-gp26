package hk.hku.cs.hkufriendhub

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView

import androidx.recyclerview.widget.RecyclerView;

class MainActivity : AppCompatActivity() {
    private lateinit var navBar: BottomNavigationView
    var isLoggedIn : Boolean = false

    companion object {
        const val SHARED_PREFS = "SHARED_PREFS"
        const val USER_TOKEN = "USER_TOKEN"
        const val USER_USERNAME = "USER_USERNAME"
        const val USER_ID = "USER_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        useEffectUserToken()

        Log.d("MainActivityUser", getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(USER_ID, null).toString())
        Log.d("MainActivityUser", getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(USER_USERNAME, null).toString())

        // navigation bar
        loadFragment(MainFragment(), false)
        navBar = findViewById<BottomNavigationView>(R.id.nav_bar)
        navBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_main -> {
                    loadFragment(MainFragment(), false)
                    true
                }
                R.id.nav_forum -> {
                    loadFragment(ForumFragment(), false)
                    true
                }
                R.id.nav_profile -> {
                    if (isLoggedIn) {
                        loadFragment(ProfileFragment(), false)
                    } else {
                        loadFragment(LoginFragment(), false)
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun useEffectUserToken() {
        val prefs = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val token = prefs.getString(USER_TOKEN, null)

        if (token.isNullOrEmpty()) {
            Log.d("MainActivity", "No token is found")
            this.isLoggedIn = false
            return
        }

        val payload = TokenUtils.decode(token)

        if (payload == null) {
            this.isLoggedIn = false
            UserUtils.clearSavedData(this)
            return
        }
        Log.d("MainActivityUser", payload.toString())
        this.isLoggedIn = true
    }

    fun loadFragment(fragment: Fragment, addToBackStack: Boolean){
        val manager: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()
        if (addToBackStack) {
            ft.addToBackStack(null)
        }
        if (fragment != null) {
            ft.replace(R.id.frame_container, fragment)
        }
        ft.commitAllowingStateLoss()
    }
}
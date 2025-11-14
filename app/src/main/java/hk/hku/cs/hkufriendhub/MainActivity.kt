package hk.hku.cs.hkufriendhub

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navBar: BottomNavigationView
    var userId : String? = null

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

        // navigation bar
        loadFragment(MainFragment(), false)
        navBar = findViewById<BottomNavigationView>(R.id.nav_bar)
        navBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_main -> {
                    loadFragment(MainFragment(), false)
                    true
                }
                R.id.nav_chatroom -> {
                    if (userId != null) {
                        loadFragment(ChatroomFragment(), false)
                    } else {
                        loadFragment(LoginFragment(), false)
                    }
                    true
                }
                R.id.nav_profile -> {
                    if (userId != null) {
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
            this.userId = null
            return
        }

        val payload = TokenUtils.decode(token)

        if (payload == null) {
            this.userId = null
            UserUtils.clearSavedData(this)
            return
        }
        Log.d("MainActivityUser", payload.toString())
        this.userId = payload.getString("id")
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
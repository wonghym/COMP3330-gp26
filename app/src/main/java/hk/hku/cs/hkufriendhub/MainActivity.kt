package hk.hku.cs.hkufriendhub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView

import androidx.recyclerview.widget.RecyclerView;

class MainActivity : AppCompatActivity() {
    private lateinit var navBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // navigation bar
        loadFragment(MainFragment())
        navBar = findViewById<BottomNavigationView>(R.id.nav_bar)
        navBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_main -> {
                    loadFragment(MainFragment())
                    true
                }
                R.id.nav_forum -> {
                    loadFragment(ForumFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    fun loadFragment(fragment: Fragment){
        val manager: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()
        ft.replace(R.id.frame_container, fragment)
        ft.commitAllowingStateLoss()
    }
}
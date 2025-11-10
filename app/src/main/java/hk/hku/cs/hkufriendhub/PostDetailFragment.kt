package hk.hku.cs.hkufriendhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton

class PostDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_post_detail, container, false)
        val backButton = view.findViewById<ImageButton>(R.id.addPost_back_button)

        backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        val gpsizeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gpsize,
            android.R.layout.simple_spinner_dropdown_item,
        )
        val dropdownMenu = view.findViewById<AutoCompleteTextView>(R.id.addPost_gpsize_input)
        dropdownMenu.setAdapter(gpsizeAdapter)

//        getPosts()

        return view
    }

}
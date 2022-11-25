package com.surajmanshal.mannsign.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.surajmanshal.mannsign.R

class CustomOrderFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_custom_order, container, false)

        setupActionBar()
        return view
    }

    fun setupActionBar(){
        if(activity!= null && requireActivity().actionBar != null){
            requireActivity().actionBar!!.title = "Custom Orders"
        }
    }


}
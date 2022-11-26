package com.surajmanshal.mannsign.ui.fragments

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.databinding.FragmentCustomOrderBinding
import com.surajmanshal.mannsign.ui.activity.CustomAcpBoardActivity

class CustomOrderFragment : Fragment() {

    lateinit var binding : FragmentCustomOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_custom_order, container, false)
        binding = DataBindingUtil.bind(view)!!


        binding.btnClickMe.setOnClickListener {
            val i = Intent(requireActivity(),CustomAcpBoardActivity::class.java)
            startActivity(i)
        }
        return binding.root
    }




}
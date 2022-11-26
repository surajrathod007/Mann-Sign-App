package com.surajmanshal.mannsign.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.databinding.FragmentHomeBinding
import com.surajmanshal.mannsign.ui.activity.CartActivity

class HomeFragment : Fragment() {


    lateinit var binding: FragmentHomeBinding
    lateinit var bottomMenu: BottomSheetDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        binding = DataBindingUtil.bind(view)!!

        binding.btnHamBurgur.setOnClickListener {
            showBottomMenu()
        }

        binding.btnCart.setOnClickListener {
            val i = Intent(requireActivity(),CartActivity::class.java)
            startActivity(i)
        }

        return binding.root
    }

    private fun showBottomMenu() {
        bottomMenu = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
        val sheetView =
            LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_menu, null)
        bottomMenu.setContentView(sheetView)
        bottomMenu.show()
    }


}
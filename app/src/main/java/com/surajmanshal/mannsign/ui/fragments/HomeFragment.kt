package com.surajmanshal.mannsign.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.surajmanshal.mannsign.ProfileActivity
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.adapter.recyclerview.CategoryAdapter
import com.surajmanshal.mannsign.adapter.recyclerview.ProductAdapter
import com.surajmanshal.mannsign.databinding.FragmentHomeBinding
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.ui.activity.CartActivity
import com.surajmanshal.mannsign.ui.activity.OrdersActivity
import com.surajmanshal.mannsign.ui.activity.ReviewsActivity
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.viewmodel.HomeViewModel
import nl.joery.animatedbottombar.AnimatedBottomBar

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var bottomMenu: BottomSheetDialog
    lateinit var vm: HomeViewModel
    lateinit var bottomNavigation: AnimatedBottomBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        binding = DataBindingUtil.bind(view)!!

        bottomNavigation = requireActivity().findViewById(R.id.bottomNavigationView)
        binding.btnHamBurgur.setOnClickListener {
            showBottomMenu()
        }

        binding.btnCart.setOnClickListener {
            val i = Intent(requireActivity(), CartActivity::class.java)
            startActivity(i)
        }




        if (NetworkService.checkForInternet(requireContext())) {
            loadData()
            setupObservers()

        } else {
            Functions.makeToast(requireContext(), "No internet", true)
        }



        return binding.root
    }

    private fun showBottomMenu() {
        bottomMenu = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
        val sheetView =
            LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_menu, null)

        val btnOrders = sheetView.findViewById<LinearLayout>(R.id.btnOrdersBottomSheet)
        val btnMyReviews = sheetView.findViewById<LinearLayout>(R.id.btnMyReviewsBottomSheet)
        val btnProfile = sheetView.findViewById<LinearLayout>(R.id.btnProfileBottomSheet)
        val btnTransactions = sheetView.findViewById<LinearLayout>(R.id.btnTransactionsBottomSheet)
        btnOrders.setOnClickListener {
            startActivity(Intent(requireActivity(), OrdersActivity::class.java))
        }
        btnMyReviews.setOnClickListener {
            startActivity(Intent(requireActivity(), ReviewsActivity::class.java))
        }
        btnProfile.setOnClickListener {
            startActivity(Intent(requireActivity(), ProfileActivity::class.java))
        }
        bottomMenu.setContentView(sheetView)
        bottomMenu.show()
    }


    private fun loadData() {
        vm.getSubCategories()
        vm.getAllPosters()
    }

    private fun setupObservers() {
        vm.msg.observe(viewLifecycleOwner) {
            Functions.makeToast(requireContext(), it)
        }
        vm.subCategories.observe(viewLifecycleOwner) {
            binding.rvCategories.adapter = CategoryAdapter(requireContext(), it)
        }
        vm.products.observe(viewLifecycleOwner) {
            binding.rvProducts.adapter = ProductAdapter(requireContext(), it, vm,)
        }
    }
}
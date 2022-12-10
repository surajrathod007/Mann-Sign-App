package com.surajmanshal.mannsign.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.onesignal.OneSignal
import com.surajmanshal.mannsign.ProfileActivity
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.adapter.recyclerview.CategoryAdapter
import com.surajmanshal.mannsign.adapter.recyclerview.ProductsMainAdapter
import com.surajmanshal.mannsign.data.model.auth.LoginReq
import com.surajmanshal.mannsign.databinding.FragmentHomeBinding
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.ui.activity.*
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.utils.auth.DataStore
import com.surajmanshal.mannsign.utils.auth.DataStore.JWT_TOKEN
import com.surajmanshal.mannsign.utils.auth.DataStore.preferenceDataStoreAuth
import com.surajmanshal.mannsign.viewmodel.HomeViewModel
import com.surajrathod.authme.util.GetInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nl.joery.animatedbottombar.AnimatedBottomBar

class HomeFragment(var jwttoken : String?) : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var bottomMenu: BottomSheetDialog
    lateinit var vm: HomeViewModel
    lateinit var bottomNavigation: AnimatedBottomBar

    var email : String? = ""
    var token : String? = ""

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

        val sharedPreference = requireActivity().getSharedPreferences("user_e", Context.MODE_PRIVATE)
        email = sharedPreference.getString("email", "")
        token = sharedPreference.getString("token","")      //not in use

        if(jwttoken.isNullOrEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                jwttoken = getToken(JWT_TOKEN)
            }
        }

        setupDeviceId()

        bottomNavigation = requireActivity().findViewById(R.id.bottomNavigationView)
        binding.btnHamBurgur.setOnClickListener {
            showBottomMenu()
        }

        binding.btnCart.setOnClickListener {
            val i = Intent(requireActivity(), CartActivity::class.java)
            startActivity(i)
        }

        binding.btnSearch.setOnClickListener {
            try{
                val i = Intent(requireActivity(), ProductCategoryDetailsActivity::class.java)
                val text = GetInput.takeFrom(binding.edSearch)
                i.putExtra("name",text.trim())
                requireActivity().startActivity(i)
            }catch (e : Exception){
                Functions.makeToast(requireContext(),e.message.toString())
            }

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
        val btnLogout = sheetView.findViewById<LinearLayout>(R.id.btnLogoutBottomSheet)

        btnOrders.setOnClickListener {
            startActivity(Intent(requireActivity(), OrdersActivity::class.java))
        }

        btnMyReviews.setOnClickListener {
            startActivity(Intent(requireActivity(), ReviewsActivity::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(requireActivity(), ProfileActivity::class.java))
        }

        btnTransactions.setOnClickListener {
            startActivity(Intent(requireActivity(), TransactionsActivity::class.java))
        }

        btnLogout.setOnClickListener {

            try{
                vm.logout(email!!,jwttoken!!)
                Functions.makeToast(requireContext(),"$email $jwttoken")
            }catch (e : Exception){
                Functions.makeToast(requireContext(),e.message.toString())
            }

        }

        bottomMenu.setContentView(sheetView)

        bottomMenu.show()
    }

    private fun logout() {
        CoroutineScope(Dispatchers.IO).launch{
            requireActivity().preferenceDataStoreAuth.edit {
                it[stringPreferencesKey(JWT_TOKEN)] = ""
            }
        }
    }


    private fun loadData() {
        vm.getSubCategories()
        vm.getAllPosters()
    }

    private fun setupObservers() {

        val slideUp : Transition = Fade(Fade.OUT)
        val f = Fade(Fade.IN)
        f.setDuration(2000)
        slideUp.setDuration(2000)
        //slideUp.addTarget(binding.linearContent)
        slideUp.addTarget(binding.llLoading)
        f.addTarget(binding.linearContent)
        TransitionManager.beginDelayedTransition(binding.constMain,slideUp)
        TransitionManager.beginDelayedTransition(binding.constMain,f)

        vm.msg.observe(viewLifecycleOwner) {
            Functions.makeToast(requireContext(), it)
        }
        vm.subCategories.observe(viewLifecycleOwner) {
            binding.rvCategories.adapter = CategoryAdapter(requireContext(), it)
        }
//        vm.products.observe(viewLifecycleOwner) {
//            binding.rvProductsMain.adapter = ProductAdapter(requireContext(), it, vm,)
//        }
        vm.posters.observe(viewLifecycleOwner){
            binding.rvProductsMain.adapter = ProductsMainAdapter(requireContext(),it,vm,viewLifecycleOwner)
        }

        vm.isLoading.observe(viewLifecycleOwner){
            if(it){
                binding.linearContent.visibility = View.GONE
                binding.llLoading.visibility = View.VISIBLE
            }else{
                binding.linearContent.visibility = View.VISIBLE
                binding.llLoading.visibility = View.GONE
            }
        }

        vm.isLoggedOut.observe(viewLifecycleOwner){
            if(it){
                logout()
                requireActivity().finish()
                Functions.makeToast(requireContext(),"Logged out")
            }
        }
    }

    private fun setupDeviceId(){
        val id = OneSignal.getDeviceState()?.userId
        if(!id.isNullOrEmpty()){
            vm.setDeviceID(LoginReq(email!!,"",id))
        }
    }

    suspend fun getToken(key : String) : String? {
        val data = requireActivity().preferenceDataStoreAuth.data.first()
        return data[stringPreferencesKey(key)]
    }

}
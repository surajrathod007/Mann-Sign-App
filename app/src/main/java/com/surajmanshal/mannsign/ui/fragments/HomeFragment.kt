package com.surajmanshal.mannsign.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.onesignal.OneSignal
import com.surajmanshal.mannsign.AuthenticationActivity
import com.surajmanshal.mannsign.ProfileActivity
import com.surajmanshal.mannsign.ProfileEdit
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.adapter.recyclerview.CategoryAdapter
import com.surajmanshal.mannsign.adapter.recyclerview.ProductsMainAdapter
import com.surajmanshal.mannsign.data.model.auth.LoginReq
import com.surajmanshal.mannsign.databinding.FragmentHomeBinding
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.room.UserDatabase
import com.surajmanshal.mannsign.ui.activity.*
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.utils.auth.DataStore.JWT_TOKEN
import com.surajmanshal.mannsign.utils.auth.DataStore.preferenceDataStoreAuth
import com.surajmanshal.mannsign.viewmodel.HomeViewModel
import com.surajrathod.authme.util.GetInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import nl.joery.animatedbottombar.AnimatedBottomBar

class HomeFragment(var jwttoken : String?) : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var bottomMenu: BottomSheetDialog
    lateinit var vm: HomeViewModel
    lateinit var bottomNavigation: AnimatedBottomBar

    var email : String? = ""
    var token : String? = ""
    var isMinProfileSetupDone = false

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
        email = sharedPreference.getString("email", null)
        token = sharedPreference.getString("token","")      //not in use

        binding.shimmerView.startShimmer()
        if(jwttoken.isNullOrEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                jwttoken = getToken(JWT_TOKEN)
            }
        }


        if (NetworkService.checkForInternet(requireContext())) {
            if(!email.isNullOrEmpty()){
                setupDeviceId()
                Functions.makeToast(requireContext(),"Device id set $email")
            }
            loadData()
            setupObservers()

        } else {
            Functions.makeToast(requireContext(), "No internet", true)
        }


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

        val userDatabase = UserDatabase.getDatabase(requireContext()).userDao()

        val user = email?.let { userDatabase.getUser(it) }
        user?.observe(viewLifecycleOwner){
            isMinProfileSetupDone = it.firstName!=null
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
        val logoutText = sheetView.findViewById<TextView>(R.id.btnLogOutText)

        if(email.isNullOrEmpty()){
            logoutText.text = "Login"
        }
        btnOrders.setOnClickListener {
            startActivity(Intent(requireActivity(), OrdersActivity::class.java))
        }

        btnMyReviews.setOnClickListener {
            startActivity(Intent(requireActivity(), ReviewsActivity::class.java))
        }

        btnProfile.setOnClickListener {
            if(!email.isNullOrEmpty()){
                if(isMinProfileSetupDone) startActivity(Intent(requireActivity(), ProfileActivity::class.java))
                else startActivity(Intent(requireActivity(), ProfileEdit::class.java))
            }else{
                startActivity(Intent(requireActivity(),AuthenticationActivity::class.java))
            }
        }

        btnTransactions.setOnClickListener {
            startActivity(Intent(requireActivity(), TransactionsActivity::class.java))
        }

        btnLogout.setOnClickListener {

            if(!email.isNullOrEmpty())
            {
                //bottomMenu.dismiss()
                val d = AlertDialog.Builder(requireContext())
                d.setTitle("Do you want to logout ?")
                d.setMessage("You can login anytime ;)")
                d.setPositiveButton("Yes"){v,m ->
                    try{
                        vm.logout(email!!,jwttoken!!)
                    }catch (e : Exception){
                        Functions.makeToast(requireContext(),e.message.toString())
                    }
                }
                d.setNegativeButton("No"){v,m->
                    v.dismiss()
                }
                d.show()
            }else{
                startActivity(Intent(requireActivity(),AuthenticationActivity::class.java))
                requireActivity().finish()
            }
        }

        bottomMenu.setContentView(sheetView)

        bottomMenu.show()
    }

    private fun logout() {
        val sharedPreference = requireActivity().getSharedPreferences("user_e", Context.MODE_PRIVATE)
        val ed = sharedPreference.edit()
        ed.putString("email",null)
        ed.commit()
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
                binding.shimmerView.visibility = View.VISIBLE
            }else{
                Handler().postDelayed({
                    binding.linearContent.visibility = View.VISIBLE
                    binding.shimmerView.visibility = View.GONE
                },1500)
            }
        }

        vm.isLoggedOut.observe(viewLifecycleOwner){
            if(!email.isNullOrEmpty())
            {
                if(it){
                    logout()
                    Functions.makeToast(requireContext(),"Logged out")
                    Handler().postDelayed({
                        requireActivity().finish()
                    },2000)
                }
            }else{
                //Functions.makeToast(requireContext(),"Can not logout")
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
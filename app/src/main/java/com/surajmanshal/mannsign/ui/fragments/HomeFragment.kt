package com.surajmanshal.mannsign.ui.fragments

import alirezat775.lib.carouselview.Carousel
import alirezat775.lib.carouselview.CarouselModel
import alirezat775.lib.carouselview.CarouselView
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.onesignal.OneSignal
import com.surajmanshal.mannsign.AuthenticationActivity
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.adapter.BannerAdapter
import com.surajmanshal.mannsign.adapter.recyclerview.CategoryAdapter
import com.surajmanshal.mannsign.adapter.recyclerview.ProductsMainAdapter
import com.surajmanshal.mannsign.data.model.BannerImage
import com.surajmanshal.mannsign.data.model.auth.LoginReq
import com.surajmanshal.mannsign.data.response.SimpleResponse
import com.surajmanshal.mannsign.databinding.FragmentHomeBinding
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.room.LocalDatabase
import com.surajmanshal.mannsign.ui.activity.CartActivity
import com.surajmanshal.mannsign.ui.activity.OrdersActivity
import com.surajmanshal.mannsign.ui.activity.ProductCategoryDetailsActivity
import com.surajmanshal.mannsign.ui.activity.ReviewsActivity
import com.surajmanshal.mannsign.ui.activity.TransactionsActivity
import com.surajmanshal.mannsign.ui.activity.WishListActivity
import com.surajmanshal.mannsign.utils.Constants
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.utils.Functions.makeToast
import com.surajmanshal.mannsign.utils.auth.DataStore.JWT_TOKEN
import com.surajmanshal.mannsign.utils.auth.DataStore.preferenceDataStoreAuth
import com.surajmanshal.mannsign.utils.makeACall
import com.surajmanshal.mannsign.viewmodel.HomeViewModel
import com.surajrathod.authme.util.GetInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment() : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var bottomMenu: BottomSheetDialog
    lateinit var vm: HomeViewModel
    var jwttoken: String? = null
    //lateinit var bottomNavigation: AnimatedBottomBar

    var email: String? = ""
    var token: String? = ""
    var isMinProfileSetupDone = false

    val adp = BannerAdapter()
    lateinit var carousel : Carousel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
    }



    override fun onResume() {
        super.onResume()
        if(NetworkService.checkForInternet(requireContext())){
            loadData()
        }else{
            makeToast(requireContext(),"No internet")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        binding = DataBindingUtil.bind(view)!!

        val sharedPreference =
            requireActivity().getSharedPreferences("user_e", Context.MODE_PRIVATE)
        email = sharedPreference.getString("email", null)
        token = sharedPreference.getString("token", "")      //not in use

        binding.shimmerView.startShimmer()
        if (jwttoken.isNullOrEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                jwttoken = getToken(JWT_TOKEN)
            }
        }


        carousel = Carousel(activity as AppCompatActivity, binding.bannerCarousel, adp)
        if (NetworkService.checkForInternet(requireContext())) {
            if (!email.isNullOrEmpty()) {
                isUserExists(email!!) {
                    if (it) {
                        setupDeviceId()
//                        Functions.makeToast(requireContext(), "Device id set $email")
                        Log.d(this.javaClass.name,"Device id set $email")
                    } else {
                        deleteAllData()
                    }
                }
            }
            setupObservers()

        } else {
            Functions.makeToast(requireContext(), "No internet", true)
        }


        //bottomNavigation = requireActivity().findViewById(R.id.bottomNavigationView)
        binding.btnHamBurgur.setOnClickListener {
            showBottomMenu()
        }

        binding.btnCart.setOnClickListener {
            val i = Intent(requireActivity(), CartActivity::class.java)
            startActivity(i)
        }

        binding.btnSearch.setOnClickListener {
            try {
                val i = Intent(requireActivity(), ProductCategoryDetailsActivity::class.java)
                val text = GetInput.takeFrom(binding.edSearch)
                i.putExtra("name", text.trim())
                requireActivity().startActivity(i)
            } catch (e: Exception) {
                Functions.makeToast(requireContext(), e.message.toString())
            }

        }

        binding.refreshHome.setOnRefreshListener {
            loadData()
        }

        val localDatabase = LocalDatabase.getDatabase(requireContext()).userDao()

        val user = email?.let { localDatabase.getUser(it) }
        user?.observe(viewLifecycleOwner) {
            isMinProfileSetupDone = it.firstName != null
        }

        binding.btnCall.setOnClickListener {
            requireContext().makeACall(Constants.MANN_SIGN_PHONE_NUMBER)
        }
        //loadCarousal()

        return binding.root
    }

    private fun deleteAllData() {
        makeToast(requireContext(), "Your account deleted from our site !")
        logout()
    }

    private fun isUserExists(email: String, exists: (Boolean) -> Unit = {}) {

        val ans = NetworkService.networkInstance.isUserExist(email)
        ans.enqueue(object : Callback<SimpleResponse?> {
            override fun onResponse(
                call: Call<SimpleResponse?>,
                response: Response<SimpleResponse?>
            ) {
                val ans = response.body()!!
                if(ans.success){
                    exists.invoke(true)
                }else{
                    exists.invoke(false)
                }
            }

            override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {
                makeToast(requireContext(),t.message.toString())
            }
        })

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
        //val btnProfile = sheetView.findViewById<TextView>(R.id.btnProfile)

        if (email.isNullOrEmpty()) {
            logoutText.text = "Login"
        }
        btnOrders.setOnClickListener {
            startActivity(Intent(requireActivity(), OrdersActivity::class.java))
        }

        btnProfile.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_homeFragment_to_userProfileFragment)
                bottomMenu.dismiss()
            } catch (e: Exception) {
                makeToast(requireContext(), e.message.toString(), true)
            }

        }
        btnMyReviews.setOnClickListener {
            startActivity(Intent(requireActivity(), ReviewsActivity::class.java))
        }

        //Not in use
        /*
        btnProfile.setOnClickListener {
            if(!email.isNullOrEmpty()){
                if(isMinProfileSetupDone) startActivity(Intent(requireActivity(), ProfileActivity::class.java))
                else startActivity(Intent(requireActivity(), ProfileEdit::class.java))
            }else{
                startActivity(Intent(requireActivity(),AuthenticationActivity::class.java))
            }
        }

         */

        btnTransactions.setOnClickListener {
            startActivity(Intent(requireActivity(), TransactionsActivity::class.java))
        }

        btnLogout.setOnClickListener {

            if (!email.isNullOrEmpty()) {
                //bottomMenu.dismiss()
                val d = AlertDialog.Builder(requireContext())
                d.setTitle("Do you want to logout ?")
                d.setMessage("You can login anytime ;)")
                d.setPositiveButton("Yes") { v, m ->
                    try {
                        vm.logout(email!!, jwttoken!!)
                    } catch (e: Exception) {
                        Functions.makeToast(requireContext(), e.message.toString())
                    }
                }
                d.setNegativeButton("No") { v, m ->
                    v.dismiss()
                }
                d.show()
            } else {
                startActivity(Intent(requireActivity(), AuthenticationActivity::class.java))
                requireActivity().finish()
            }
        }

        with(sheetView) {
            findViewById<View>(R.id.menuItemWishlist).setOnClickListener {
                startActivity(Intent(requireContext(), WishListActivity::class.java))
                bottomMenu.dismiss()
            }
        }

        bottomMenu.setContentView(sheetView)

        bottomMenu.show()
    }

    private fun logout() {
        try {
            val localDatabase = LocalDatabase.getDatabase(requireContext()).userDao()
            if (!email.isNullOrEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    localDatabase.deleteUser(email!!)
                }
            }
            val sharedPreference =
                requireActivity().getSharedPreferences("user_e", Context.MODE_PRIVATE)
            val ed = sharedPreference.edit()
            ed.putString("email", null)
            ed.commit()
            CoroutineScope(Dispatchers.IO).launch {
                requireActivity().preferenceDataStoreAuth.edit {
                    it[stringPreferencesKey(JWT_TOKEN)] = ""
                }
            }

        } catch (e: Exception) {
            makeToast(requireContext(), e.message.toString())
        }
    }


    private fun loadData() {

        vm.getSubCategories()
        vm.getAllPosters()
        vm.getAdBanners()

    }


    private fun setupObservers() {

        vm.adBanners.observe(viewLifecycleOwner){
            if(!it.isNullOrEmpty()){
                carousel.setOrientation(CarouselView.HORIZONTAL, false)
                carousel.autoScroll(true, 2000, true)
                carousel.scaleView(true)
                val images = mutableListOf<BannerImage>()
                it.forEach {
                    images.add(BannerImage(it.imgUrl.toString()))
                }
                carousel.addAll(images as MutableList<CarouselModel>)
                carousel.resumeAutoScroll()
                binding.bannerCarousel.visibility = View.VISIBLE
            }else{
                binding.bannerCarousel.visibility = View.GONE
            }
        }
        vm.msg.observe(viewLifecycleOwner) {
           //Functions.makeToast(requireContext(), it)
            Log.e("${this.javaClass.name}:OCCURED",it)
        }
        vm.subCategories.observe(viewLifecycleOwner) {
            binding.rvCategories.adapter = CategoryAdapter(requireContext(), it)
        }
//        vm.products.observe(viewLifecycleOwner) {
//            binding.rvProductsMain.adapter = ProductAdapter(requireContext(), it, vm,)
//        }
        vm.posters.observe(viewLifecycleOwner) {
            binding.rvProductsMain.adapter =
                ProductsMainAdapter(requireContext(), it, vm, viewLifecycleOwner)
        }

        vm.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.linearContent.visibility = View.GONE
                binding.shimmerView.visibility = View.VISIBLE
            } else {
                Handler().postDelayed({
                    binding.linearContent.visibility = View.VISIBLE
                    binding.shimmerView.visibility = View.GONE
                    binding.refreshHome.isRefreshing = false
                }, 1500)
            }
        }

        vm.isLoggedOut.observe(viewLifecycleOwner) {
            if (!email.isNullOrEmpty()) {
                if (it) {
                    logout()
                    Functions.makeToast(requireContext(), "Logged out")
                    Handler().postDelayed({
                        requireActivity().finish()
                    }, 2000)
                }
            } else {
                //Functions.makeToast(requireContext(),"Can not logout")
            }
        }
    }

    private fun setupDeviceId() {
        val id = OneSignal.getDeviceState()?.userId
        if (!id.isNullOrEmpty()) {
            vm.setDeviceID(LoginReq(email!!, "", id))
        }
    }

    suspend fun getToken(key: String): String? {
        val data = requireActivity().preferenceDataStoreAuth.data.first()
        return data[stringPreferencesKey(key)]
    }

}
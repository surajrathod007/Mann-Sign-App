package com.surajmanshal.mannsign.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.surajmanshal.mannsign.AuthenticationActivity
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.adapter.recyclerview.ReviewAdapter
import com.surajmanshal.mannsign.databinding.ActivityReviewsBinding
import com.surajmanshal.mannsign.room.UserDatabase
import com.surajmanshal.mannsign.room.UserEntity
import com.surajmanshal.mannsign.viewmodel.ReviewsViewModel

class ReviewsActivity : AppCompatActivity() {

    lateinit var binding: ActivityReviewsBinding
    lateinit var vm: ReviewsViewModel
    var email : String? = null
    private var currentUser : UserEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewsBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this).get(ReviewsViewModel::class.java)


        val sharedPreference = getSharedPreferences("user_e", Context.MODE_PRIVATE)
        email = sharedPreference.getString("email", "")
        if (!email.isNullOrEmpty())
            loadUserReviews(email!!)

        val db = UserDatabase.getDatabase(this).userDao()

        val user = db.getUser(email!!)
        user.observe(this){
            currentUser = it
        }

        binding.emptyReviews.txtEmptyMessage.text = "You have no reviews !"
        binding.shimmerReviewLoading.startShimmer()
        binding.btnReviewBack.setOnClickListener {
            onBackPressed()
            finish()
        }
        binding.loginRegisterReviews.btnLoginRegister.setOnClickListener {
            startActivity(Intent(this, AuthenticationActivity::class.java))
            finish()
        }
        binding.refreshReview.setOnRefreshListener {
            if (!email.isNullOrEmpty())
                loadUserReviews(email!!)
            else
                binding.refreshReview.isRefreshing = false
        }

        setObservers()



        setContentView(binding.root)
    }

    private fun setObservers() {
        vm.msg.observe(this) {
            Toast.makeText(this@ReviewsActivity, it.toString(), Toast.LENGTH_SHORT).show()
        }
        vm.userAllReviews.observe(this){
            if(it.isNullOrEmpty()){
                binding.emptyReviews.root.visibility = View.VISIBLE
                binding.shimmerReviewLoading.visibility = View.GONE
                binding.rvReviews.visibility = View.GONE
                binding.refreshReview.isRefreshing = false
            }else{
                binding.emptyReviews.root.visibility = View.GONE
            }
            binding.rvReviews.adapter = ReviewAdapter(this@ReviewsActivity,it,vm,currentUser)
        }
        vm.selectedReview.observe(this){
            showBottomSheet(this)
        }
        vm.isLoading.observe(this){
            if(!email.isNullOrEmpty()){
                if(it){
                    binding.shimmerReviewLoading.visibility = View.VISIBLE
                }else{
                    Handler().postDelayed({
                        binding.shimmerReviewLoading.visibility = View.GONE
                        binding.rvReviews.visibility = View.VISIBLE
                        binding.refreshReview.isRefreshing = false
                    },1500)
                }
            }else{
                binding.shimmerReviewLoading.visibility = View.GONE
                binding.bounceReviewScroll.visibility = View.GONE
                binding.loginRegisterReviews.root.visibility = View.VISIBLE
                binding.refreshReview.visibility = View.GONE
                binding.refreshReview.isRefreshing = false
            }

        }
    }

    private fun loadUserReviews(email: String) {
        vm.getReviewByEmailId(email)
    }

    fun showBottomSheet(c : Context){

        val bottomSheetDialog = BottomSheetDialog(c, R.style.BottomSheetTheme)
        val v = LayoutInflater.from(c).inflate(R.layout.update_review_bottom_sheet,null)
        val ratingBar = v.findViewById<RatingBar>(R.id.ratingBarUpdateReview)
        val edUpdate = v.findViewById<EditText>(R.id.edUpdateReview)
        val btnUpdateReview = v.findViewById<AppCompatButton>(R.id.btnUpdateReviewBottomSheet)

        bottomSheetDialog.behavior.skipCollapsed = true
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        ratingBar.rating = vm.selectedReview.value?.rating!!.toFloat()
        edUpdate.setText(vm.selectedReview.value!!.comment.toString())
        btnUpdateReview.setOnClickListener {
            vm.updateReview(ratingBar.rating,edUpdate.text.toString())
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(v)
        bottomSheetDialog.show()
    }
}
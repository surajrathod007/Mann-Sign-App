package com.surajmanshal.mannsign.ui.activity

import android.content.Context
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
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.adapter.recyclerview.ReviewAdapter
import com.surajmanshal.mannsign.databinding.ActivityReviewsBinding
import com.surajmanshal.mannsign.viewmodel.ReviewsViewModel

class ReviewsActivity : AppCompatActivity() {

    lateinit var binding: ActivityReviewsBinding
    lateinit var vm: ReviewsViewModel
    var email : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewsBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this).get(ReviewsViewModel::class.java)


        val sharedPreference = getSharedPreferences("user_e", Context.MODE_PRIVATE)
        val email = sharedPreference.getString("email", "")
        if (email != "")
            loadUserReviews(email!!)

        binding.shimmerReviewLoading.startShimmer()
        binding.btnReviewBack.setOnClickListener {
            onBackPressed()
            finish()
        }

        setObservers()


        setContentView(binding.root)
    }

    private fun setObservers() {
        vm.msg.observe(this) {
            Toast.makeText(this@ReviewsActivity, it.toString(), Toast.LENGTH_SHORT).show()
        }
        vm.userAllReviews.observe(this){
            binding.rvReviews.adapter = ReviewAdapter(this@ReviewsActivity,it,vm)
        }
        vm.selectedReview.observe(this){
            showBottomSheet(this)
        }
        vm.isLoading.observe(this){
            if(it){
                binding.shimmerReviewLoading.visibility = View.VISIBLE
                binding.rvReviews.visibility = View.GONE
            }else{
                Handler().postDelayed({
                    binding.shimmerReviewLoading.visibility = View.GONE
                    binding.rvReviews.visibility = View.VISIBLE
                },1500)
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
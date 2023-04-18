package com.surajmanshal.mannsign.adapter.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsign.data.model.Review
import com.surajmanshal.mannsign.databinding.ItemReviewLayoutBinding
import com.surajmanshal.mannsign.room.user.UserEntity
import com.surajmanshal.mannsign.utils.hide
import com.surajmanshal.mannsign.viewmodel.ReviewsViewModel
import java.time.format.DateTimeFormatter

class ReviewAdapter(val c: Context, val list: List<Review>, val vm: ReviewsViewModel?=null, private val currentUser: UserEntity? =null) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
    class ReviewViewHolder(val binding: ItemReviewLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val txtUserNameReview = binding.txtUserNameReview
        val ivReviewerProfile = binding.imgUserProfileReview
        val btnDeleteReview = binding.btnDeleteReview
        val ratingBar = binding.ratingBar
        val txtUserReview = binding.txtUserReview
        val txtReviewDate = binding.txtReviewDate
        val btnUpdateReview = binding.btnUpdateReview
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(
            ItemReviewLayoutBinding.inflate(LayoutInflater.from(c), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = list[position]
        with(holder) {
            txtUserNameReview.text = review.emailId
            txtUserReview.text = review.comment
            txtReviewDate.text =
                review.reviewDate.format(DateTimeFormatter.ofPattern("E, dd MMM yyyy hh:mm a"))
            ratingBar.rating = review.rating.toFloat()
            btnDeleteReview.apply {
                if(review.emailId!=currentUser!!.emailId){
                    hide()
                    return@apply
                }
                setOnClickListener {
                    val builder = AlertDialog.Builder(it.context)
                    builder.setTitle("Are you sure?")
                    builder.setMessage("Do you want to delete this review?")
                    builder.setPositiveButton("Delete"
                    ) { _, i ->
                        vm?.deleteReview(review.reviewId!!,review.emailId)
                        //vm?.getReviewByEmailId(review.emailId)
                    }
                    builder.setNegativeButton(
                        "Cancel"
                    ) { _, i ->
                        Toast.makeText(it.context, "Action cancelled", Toast.LENGTH_LONG).show()
                    }
                    builder.show()
                }
            }
            btnUpdateReview.apply {
                if(review.emailId!=currentUser!!.emailId){
                    hide()
                    return@apply
                }
                setOnClickListener {
                    vm?.selectReview(review)
//                    showBottomSheet(c,vm)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
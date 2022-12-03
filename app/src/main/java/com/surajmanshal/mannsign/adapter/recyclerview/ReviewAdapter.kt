package com.surajmanshal.mannsign.adapter.recyclerview

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.data.model.Review
import com.surajmanshal.mannsign.databinding.ItemReviewLayoutBinding
import com.surajmanshal.mannsign.viewmodel.ReviewsViewModel
import java.time.format.DateTimeFormatter

class ReviewAdapter(val c: Context, val list: List<Review>, val vm: ReviewsViewModel) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
    class ReviewViewHolder(val binding: ItemReviewLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val txtUserNameReview = binding.txtUserNameReview
        val imgUserProfileReview = binding.imgUserProfileReview
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
        val d = list[position]
        with(holder) {
            txtUserNameReview.text = d.emailId
            txtUserReview.text = d.comment
            txtReviewDate.text =
                d.reviewDate.format(DateTimeFormatter.ofPattern("E, dd MMM yyyy hh:mm a"))
            ratingBar.rating = d.rating.toFloat()
            btnDeleteReview.setOnClickListener {
                val builder = AlertDialog.Builder(it.context)
                builder.setTitle("Are you sure?")
                builder.setMessage("Do you want to delete this review?")
                builder.setPositiveButton("Yes",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        vm.deleteReview(d.reviewId!!)
                        vm.getReviewByEmailId(d.emailId)
                    })
                builder.setNegativeButton(
                    "No",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        Toast.makeText(it.context, "Action cancelled", Toast.LENGTH_LONG).show()
                    })
                builder.show()
            }
            btnUpdateReview.setOnClickListener {
                vm.selectReview(d)
                //showBottomSheet(c,vm)
            }
            //TODO : Setup user profile image !
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }



}
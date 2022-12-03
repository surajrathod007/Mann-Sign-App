package com.surajmanshal.mannsign.viewmodel

import android.widget.RatingBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsign.data.model.Review
import com.surajmanshal.mannsign.data.response.SimpleResponse
import com.surajmanshal.mannsign.network.NetworkService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime

class ReviewsViewModel : ViewModel() {

    private var _userAllReviews = MutableLiveData<List<Review>>()
    val userAllReviews: LiveData<List<Review>> get() = _userAllReviews

    private var _msg = MutableLiveData<String>()
    val msg: LiveData<String> get() = _msg

    private var _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var _selectedReview = MutableLiveData<Review>()
    val selectedReview: LiveData<Review> get() = _selectedReview

    companion object {
        val db = NetworkService.networkInstance
    }

    fun getReviewByEmailId(email: String) {
        _isLoading.postValue(true)
        val r = db.getAllUserReview(email)
        r.enqueue(object : Callback<List<Review>?> {
            override fun onResponse(call: Call<List<Review>?>, response: Response<List<Review>?>) {
                with(response.body()!!) {
                    _userAllReviews.postValue(this)
                }
                _isLoading.postValue(false)
            }

            override fun onFailure(call: Call<List<Review>?>, t: Throwable) {
                _msg.postValue(t.message.toString())
                _isLoading.postValue(false)
            }
        })
    }

    fun deleteReview(id: Int) {
        val r = db.deleteReview(id)
        r.enqueue(object : Callback<SimpleResponse?> {
            override fun onResponse(
                call: Call<SimpleResponse?>,
                response: Response<SimpleResponse?>
            ) {
                with(response.body()!!) {
                    _msg.postValue(this.message)
                }
            }

            override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {
                _msg.postValue(t.message.toString())
            }
        })
    }

    fun selectReview(r: Review) {
        _selectedReview.postValue(r)
    }

    fun addReview(email: String, comment: String, rating: Float, pid: Int) {
        val r = Review(
            reviewId = null,
            productId = pid,
            rating = rating.toInt(),
            comment = comment,
            emailId = email,
            reviewDate = LocalDateTime.now()
        )
        val s = db.addReview(r)
        s.enqueue(object : Callback<SimpleResponse?> {
            override fun onResponse(
                call: Call<SimpleResponse?>,
                response: Response<SimpleResponse?>
            ) {
                with(response.body()!!) {
                    _msg.postValue(this.message)
                }
            }

            override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {
                _msg.postValue(t.message.toString())
            }
        })
    }

    fun updateReview(ratingBar: Float, comment: String) {
        val ri = _selectedReview.value
        val r = Review(
            reviewId = ri!!.reviewId,
            productId = ri!!.productId,
            rating = ratingBar.toInt(),
            comment = comment,
            emailId = ri!!.emailId,
            reviewDate = LocalDateTime.now()
        )
        val s = db.addReview(r)
        s.enqueue(object : Callback<SimpleResponse?> {
            override fun onResponse(
                call: Call<SimpleResponse?>,
                response: Response<SimpleResponse?>
            ) {
                with(response.body()!!) {
                    if (this.success)
                        _msg.postValue("Review updated successfulyy")
                        getReviewByEmailId(r.emailId)
                }
            }
            override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {
                _msg.postValue(t.message.toString())
            }
        })
    }
}
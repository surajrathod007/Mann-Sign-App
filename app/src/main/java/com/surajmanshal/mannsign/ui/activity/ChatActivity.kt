package com.surajmanshal.mannsign.ui.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.adapter.ChatAdapter
import com.surajmanshal.mannsign.data.model.ordering.ChatMessage
import com.surajmanshal.mannsign.databinding.ActivityChatBinding
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.viewmodel.ChatViewModel

class ChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatBinding
    lateinit var vm: ChatViewModel
    var id: String? = null
    var email: String? = null


    //TODO : Do api call in every 1-2 seconds , using handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this).get(ChatViewModel::class.java)

        val sharedPreference = getSharedPreferences("user_e", Context.MODE_PRIVATE)
        email = sharedPreference.getString("email", "")
        id = intent.getStringExtra("id")
        setContentView(binding.root)

        if (!id.isNullOrEmpty() && !email.isNullOrEmpty()){
            vm.loadChats(id!!)
        }else{
            Functions.makeToast(this,"Email or orderId is empty")
        }


        setupObserver()
        btnClickListners()
    }

    fun btnClickListners() {
        binding.btnSendMessage.setOnClickListener {
            if (!id.isNullOrEmpty() && !email.isNullOrEmpty()) {
                vm.addChat(
                    ChatMessage(
                        orderId = id!!,
                        emailId = email!!,
                        message = binding.edMessage.text.toString(),
                        System.currentTimeMillis().toString(),
                        null
                    )
                )
                binding.edMessage.text = null
            }


        }
        binding.btnChatBack.setOnClickListener {
            finish()
        }
    }

    fun setupObserver() {
        vm.msg.observe(this) {
            Functions.makeToast(this@ChatActivity, it)
        }
        vm.chats.observe(this){
            binding.rvChats.adapter = ChatAdapter(this@ChatActivity,it,email)
            val pos = (binding.rvChats.adapter as ChatAdapter).itemCount-1
            binding.rvChats.smoothScroll(pos,200){

            }
        }
    }

    fun RecyclerView.smoothScroll(toPos: Int, duration: Int = 500, onFinish: () -> Unit = {}) {
        try {
            val smoothScroller: RecyclerView.SmoothScroller = object : LinearSmoothScroller(context) {
                override fun getVerticalSnapPreference(): Int {
                    return SNAP_TO_END
                }

                override fun calculateTimeForScrolling(dx: Int): Int {
                    return duration
                }

                override fun onStop() {
                    super.onStop()
                    onFinish.invoke()
                }
            }
            smoothScroller.targetPosition = toPos
            layoutManager?.startSmoothScroll(smoothScroller)
        } catch (e: Exception) {

        }
    }

    fun firstVisiblePostion() : Int{
        val manager = binding.rvChats.layoutManager
        if(manager is LinearLayoutManager){
            return (manager as LinearLayoutManager).findFirstVisibleItemPosition()
        }
        return 0
    }
}
package com.surajmanshal.mannsign.utils.auth

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.databinding.LoadingScreenBinding


class LoadingScreen(context: Context) : AppCompatDialog(context) {

    fun loadingScreen(title : String? = null) : Dialog{
       val dialog = Dialog(context)
        LoadingScreenBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.loading_screen,null)
        ).apply {
            title?.let {
                tvTitle.text = it
            }
            dialog.setCancelable(false)
            dialog.setContentView(root)
        }
        return dialog
    }
    fun toggleDialog(dialog: Dialog){
        if(dialog.isShowing){
            dialog.dismiss()
        }else{
            dialog.show()
        }
    }

}